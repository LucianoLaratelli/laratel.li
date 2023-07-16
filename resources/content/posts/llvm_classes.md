---
title: "Generating LLVM IR For Classes Using LLVM's C++ API"
date: 2020-07-03T16:13:25-04:00
description: "Using LLVM's C++ API to generate code for class declarations."
---

## Introduction

I'm spending my summer using LLVM to generate code for DJ, the programming language [Dr. Ligatti](https://www.cse.usf.edu/~ligatti/) devised for the spring 2020 Compilers course at USF. This post will demonstrate how to use LLVM's C++ API to generate LLVM IR for DJ Code that is similar to this C example:

```c
#include <stdio.h>
#include <stdlib.h>

struct B {
  struct B *a;
  int b;
};

int main() {
  struct B *b = malloc(sizeof(struct B));

  b->b = 2020;
  printf("%d\n", b->b);
}
```

At this point, you may be wondering why I'm not showing you a C++ example that actually uses a class. Let's try it:

```cpp
#include <cstdio>

class B {
public:
  int a;
  int method() { return 2; }
};

int main() {
  B *b = new B();
  b->a = 2018;
  printf("%d\n", b->a + b->method());
}
```

When I started working on generating classes, I noticed something in the LLVM IR for the C++ example:

```llvm
%class.B = type { i32 }

define linkonce_odr dso_local i32 @_ZN1B6methodEv(%class.B* %0) #4 comdat align 2 {
  %2 = alloca %class.B*, align 8
  store %class.B* %0, %class.B** %2, align 8
  %3 = load %class.B*, %class.B** %2, align 8
  ret i32 2
}
```

_LLVM (and probably everyone else) separates class definitions from method declarations!_ Realizing this was a crucial moment for me; up until that point, I had been searching endlessly: "how does LLVM generate code for classes", "LLVM IR class code gen", "LLVM class generation". [This message](https://lists.llvm.org/pipermail/llvm-dev/2017-June/113607.html) from the LLVM-dev mailing list pushed me to think in terms of how LLVM lowers _C_ to its IR, instead of C++. I realized that meant I should be looking for _struct_ methods, which were not hard to [find](http://llvm.org/doxygen/classllvm_1_1StructType.html), and was finally able to get started.

## Learning From Clang

My first step when trying to determine what LLVM is doing is to first use `clang` to generate some LLVM IR for me, by running `clang -S -O0 -emit-llvm example.c`. This outputs the LLVM IR to a file `example.ll`, which will let us see exactly what's happening by asking `clang` to not do any optimizations. Here's the IR for the C example above, cleaned up a bit for brevity:

```llvm
%struct.B = type { %struct.B*, i32 }

define dso_local i32 @main() #0 {
  %1 = alloca %struct.B*, align 8
  %2 = call noalias i8* @malloc(i64 16) #3
  %3 = bitcast i8* %2 to %struct.B*
  store %struct.B* %3, %struct.B** %1, align 8
  %4 = load %struct.B*, %struct.B** %1, align 8
  %5 = getelementptr inbounds %struct.B, %struct.B* %4, i32 0, i32 1
  store i32 2020, i32* %5, align 8
  %6 = load %struct.B*, %struct.B** %1, align 8
  %7 = getelementptr inbounds %struct.B, %struct.B* %6, i32 0, i32 1
  %8 = load i32, i32* %7, align 8
  %9 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str, i64 0, i64 0), i32 %8)
  ret i32 0
}
```

Let's walk through what's happening here.

The struct declaration is in global scope, consisting of its name and the types that make it up. We create a variable of _pointer to the struct_ using `alloca`. `malloc` allocates space for the pointer; the pointer returned from malloc gets cast to the struct type and ends up in the variable we declared. Lastly, the [`getelementptr` (GEP)](https://llvm.org/docs/GetElementPtr.html#what-is-dereferenced-by-gep) instruction gets us a pointer to a requested field (the `i32 1` at the end there is getting the 1th field in the struct).

With these steps acting as a guide, let's build an example program that will generate equivalent IR from scratch.

## Implementation

We'll be building a file `example.cpp` step-by-step. I haven't included snippets that involve declaring `main` or `printf`, since we've seen those before; find those in the full code listing below.

After including the requisite LLVM headers and declaring any data structures, we start by allocating a struct using `StructType::create()`. After this, we tell LLVM what members compose this struct; in our case, we have a pointer of the struct's type and an integer. `PointerType::getUnqual` uses the default address space; we could just as easily use `PointerType::get()` by getting the address space using `PointerType::getAddressSpace()`, or passing `0` to it for the default address space.

```cpp
std::map<std::string, StructType *> allocatedClasses;
allocatedClasses["B"] = StructType::create(TheContext, "B");
std::vector<Type *> structMembers = {
    PointerType::getUnqual(allocatedClasses["B"]),
    Type::getInt32Ty(TheContext)};
allocatedClasses["B"]->setBody(structMembers);
```

Once we have a pointer to the struct's type, we can create a variable of it:

```cpp
static std::map<std::string, AllocaInst *> NamedValues;
NamedValues["b"] = Builder.CreateAlloca(
    PointerType::getUnqual(allocatedClasses["B"]), NamedValues["b"]);
```

Now we can allocate space for a struct using `CallInst::CreateMalloc()`. **_Using `Builder.Insert()` is crucial here._** You'll notice it's hidden in the `Builder.CreateStore()` call. Some LLVM API methods that return `Instruction`s [don't insert them for you](https://llvm.org/doxygen/classllvm_1_1CallInst.html#ab16e984b0e40d0ca395b70075b8ca251)!

```cpp
auto typeSize = ConstantExpr::getSizeOf(allocatedClasses["B"]);
auto I = CallInst::CreateMalloc(
    Builder.GetInsertBlock(), Type::getInt64Ty(TheContext),
    allocatedClasses["B"], typeSize, nullptr, nullptr, "");
Builder.CreateStore(Builder.Insert(I), NamedValues["b"]);
```

Once we've allocated space, we can assign to it using the infamous GEP instruction. GEP does pointer arithmetic for us. The first zero is always required; it's dereferencing the struct pointer we get from `allocatedClasses`.

```cpp
std::vector<Value *> elementIndex = {
    ConstantInt::get(TheContext, APInt(32, 0)),
    ConstantInt::get(TheContext, APInt(32, 1))};
Value *GEP =
    Builder.CreateGEP(Builder.CreateLoad(NamedValues["b"]), elementIndex);

Builder.CreateStore(ConstantInt::get(TheContext, APInt(32, 2020)), GEP);
```

Now that we've stored to the variable, we can read from it, using GEP again. We reuse `elementIndex` from above.

```cpp
GEP = Builder.CreateGEP(Builder.CreateLoad(NamedValues["b"]), elementIndex);

auto loaded = Builder.CreateLoad(GEP);
```

And we're done! At this point we proceed with the `printf` calls as before, using `loaded` as the second argument. You can check out the full IR output of this program down below.

## Caveats

There are some differences between the LLVM IR generated by our example and `clang`'s. My struct type is just `B`; it seems they've prepended `struct.` to their type name (they appear to do this for class types as well.) When `clang` allocates, they always align. I haven't taken the time to learn how to do this yet, so the example does not align. Struct size computation is not done in the `clang`-generated IR; mine does the computation at runtime.

Beyond these, I am pretty happy with how similar they turned out! This shows how powerful LLVM's API is.

## Changelog

### 2020-07-27

I updated the read and store operations to use [`IRBuilder::CreateGEP`](https://llvm.org/doxygen/classllvm_1_1IRBuilderBase.html#a8b1b619e109f326267438a91991ad27b) instead of using `GetElementPtrInst::Create` and then `IRBuilder::Insert` as I did originally. This is much cleaner.

## Full Code Listing

### C++ Code

_To compile and run this example, save it as example.cpp and then run:_

```shellSession
clang++ `llvm-config --cxxflags --ldflags --system-libs --libs all` example.cpp
./a.out > example.ll
clang example.ll
./a.out
```

I used `clang` version `10.0.0-4ubuntu1` (on x86) with version `10.0.0` of the LLVM libraries. You can check with `clang --version` and `llvm-config --version`.

```cpp
#include "llvm/ADT/APInt.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/Module.h"
#include "llvm/Support/raw_ostream.h"
#include <map>
#include <memory>
#include <vector>

using namespace llvm;

static LLVMContext TheContext; static IRBuilder<> Builder(TheContext);

int main() {
  static std::unique_ptr<Module> TheModule =
      std::make_unique<Module>("example.cpp", TheContext);

  std::map<std::string, StructType *> allocatedClasses;
  allocatedClasses["B"] = StructType::create(TheContext, "B");
  std::vector<Type *> structMembers = {
      PointerType::getUnqual(allocatedClasses["B"]),
      Type::getInt32Ty(TheContext)};
  allocatedClasses["B"]->setBody(structMembers);

  std::vector<Type *> args;
  args.push_back(Type::getInt8PtrTy(TheContext));
  FunctionType *printfType =
      FunctionType::get(Builder.getInt32Ty(), args, true);
  Function::Create(printfType, Function::ExternalLinkage, "printf",
                   TheModule.get());

  FunctionType *mainType = FunctionType::get(Builder.getInt32Ty(), false);
  Function *main = Function::Create(mainType, Function::ExternalLinkage, "main",
                                    TheModule.get());
  BasicBlock *entry = BasicBlock::Create(TheContext, "entry", main);
  Builder.SetInsertPoint(entry);

  static std::map<std::string, AllocaInst *> NamedValues;
  NamedValues["b"] = Builder.CreateAlloca(
      PointerType::getUnqual(allocatedClasses["B"]), NamedValues["b"]);

  auto typeSize = ConstantExpr::getSizeOf(allocatedClasses["B"]);
  auto I = CallInst::CreateMalloc(
      Builder.GetInsertBlock(), Type::getInt64Ty(TheContext),
      allocatedClasses["B"], typeSize, nullptr, nullptr, "");
  Builder.CreateStore(Builder.Insert(I), NamedValues["b"]);

  std::vector<Value *> elementIndex = {
      ConstantInt::get(TheContext, APInt(32, 0)),
      ConstantInt::get(TheContext, APInt(32, 1))};
  Value *GEP =
      Builder.CreateGEP(Builder.CreateLoad(NamedValues["b"]), elementIndex);

  Builder.CreateStore(ConstantInt::get(TheContext, APInt(32, 2020)), GEP);

  GEP = Builder.CreateGEP(Builder.CreateLoad(NamedValues["b"]), elementIndex);

  auto loaded = Builder.CreateLoad(GEP);

  /*Set up printf arguments*/
  std::vector<Value *> printArgs;
  Value *formatStr = Builder.CreateGlobalStringPtr("%d\n");
  printArgs.push_back(formatStr);
  printArgs.push_back(loaded);
  Builder.CreateCall(TheModule->getFunction("printf"), printArgs);
  /*return value for `main`*/
  Builder.CreateRet(ConstantInt::get(TheContext, APInt(32, 0)));
  /*Emit the LLVM IR to the console*/
  TheModule->print(outs(), nullptr);
}
```

### LLVM IR

```llvm
; ModuleID = 'example.cpp'
source_filename = "example.cpp"

%B = type { %B*, i32 }

@0 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

declare i32 @printf(i8*, ...)

define i32 @main() {
entry:
  %0 = alloca %B*
  %malloccall = tail call i8* @malloc(i64 ptrtoint (%B* getelementptr (%B, %B* null, i32 1) to i64))
  %1 = bitcast i8* %malloccall to %B*
  store %B* %1, %B** %0
  %2 = load %B*, %B** %0
  %3 = getelementptr %B, %B* %2, i32 0, i32 1
  store i32 2020, i32* %3
  %4 = load %B*, %B** %0
  %5 = getelementptr %B, %B* %4, i32 0, i32 1
  %6 = load i32, i32* %5
  %7 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @0, i32 0, i32 0), i32 %6)
  ret i32 0
}

declare noalias i8* @malloc(i64)
```
