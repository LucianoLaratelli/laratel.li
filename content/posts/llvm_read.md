---
title: "Generating Calls to scanf From LLVM IR"
date: 2020-06-15T11:41:03-04:00
description: "Using LLVM's C++ API to call to the system scanf."
---

## Introduction

I'm spending my summer using LLVM to generate code for DJ, the programming language [Dr. Ligatti](https://www.cse.usf.edu/~ligatti/) devised for the spring 2020 Compilers course at USF.

DJ provides a `readNat` function, which reads a natural number from the console and returns its value. In the course, this function was readily available in the instruction set our compilers generated (this was also devised by Dr. Ligatti.) Because `readNat` is in this sense provided by the runtime, I had to make it available in some fashion from my LLVM-backed compiler. While this function is similar enough to [printNat](https://luciano.laratel.li/#/posts/Generating-Calls-to-printf-From-LLVM-IR), it presents some added difficulty. This is due to my choice to use `scanf` as `readNat`'s backend, which requires a memory location in which to store the value.

The example below generates IR which is equivalent to the pseudocode `printNat(readNat())`. This way, we can verify that the read succeeded!

## The code

```cpp
#include "llvm/ADT/APInt.h"
#include "llvm/IR/BasicBlock.h"
#include "llvm/IR/Function.h"
#include "llvm/IR/IRBuilder.h"
#include "llvm/IR/Module.h"
#include "llvm/Support/raw_ostream.h"
#include <memory>
#include <vector>

using namespace llvm;

static LLVMContext TheContext;
static IRBuilder<> Builder(TheContext);

int main() {
  static std::unique_ptr<Module> TheModule;

  TheModule = std::make_unique<Module>("inputFile", TheContext);

  /*set up the function prototype for printf and scanf*/
  std::vector<Type *> runTimeFuncArgs = {Type::getInt8PtrTy(TheContext)};
  /*true specifies the function as variadic*/
  FunctionType *runTimeFuncType =
      FunctionType::get(Builder.getInt32Ty(), runTimeFuncArgs, true);

  Function::Create(runTimeFuncType, Function::ExternalLinkage, "printf",
                   TheModule.get());
  Function::Create(runTimeFuncType, Function::ExternalLinkage, "scanf",
                   TheModule.get());

  /*set up and declare main; begin inserting into it*/
  FunctionType *mainType = FunctionType::get(Builder.getInt32Ty(), false);
  Function *main = Function::Create(mainType, Function::ExternalLinkage, "main",
                                    TheModule.get());
  BasicBlock *entry = BasicBlock::Create(TheContext, "entry", main);
  Builder.SetInsertPoint(entry);

  Builder.GetInsertBlock()->getParent();
  /*set up scanf arguments*/
  Value *scanfFormat = Builder.CreateGlobalStringPtr("%u");
  AllocaInst *Alloca =
      Builder.CreateAlloca(Type::getInt32Ty(TheContext), nullptr, "temp");
  std::vector<Value *> scanfArgs = {scanfFormat, Alloca};

  Function *theScanf = TheModule->getFunction("scanf");
  Builder.CreateCall(theScanf, scanfArgs);

  /*set up printf arguments, loading the value that was read in from Alloca*/
  Function *thePrintf = TheModule->getFunction("printf");
  Value *printfFormat = Builder.CreateGlobalStringPtr("%u\n");
  std::vector<Value *> printfArgs = {printfFormat, Builder.CreateLoad(Alloca)};
  Builder.CreateCall(thePrintf, printfArgs);

  /*return value for `main`*/
  Builder.CreateRet(Builder.CreateLoad(Alloca));
  /*Emit the LLVM IR to the console*/
  TheModule->print(outs(), nullptr);
}
```

The main takeaway here is the use of the `CreateAlloca` method to create a named value to store the value that `scanf` reads in.

### Compiling

I am using clang/llvm versions 10.0.0 at the time of this writing. I compile the above as `` clang++ `llvm-config --cxxflags --ldflags --system-libs --libs all` test.cpp ``. When run, it generates the following IR:

```llvm
; ModuleID = 'inputFile'
source_filename = "inputFile"

@0 = private unnamed_addr constant [3 x i8] c"%u\00", align 1
@1 = private unnamed_addr constant [4 x i8] c"%u\0A\00", align 1

declare i32 @printf(i8*, ...)

declare i32 @scanf(i8*, ...)

define i32 @main() {
entry:
  %temp = alloca i32
  %0 = call i32 (i8*, ...) @scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @0, i32 0, i32 0), i32* %temp)
  %1 = load i32, i32* %temp
  %2 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @1, i32 0, i32 0), i32 %1)
  %3 = load i32, i32* %temp
  ret i32 %3
}
```

Just like last time, we throw the output IR into a file `test.ll` and compile it with `clang`:

```shell
$ clang test.ll
warning: overriding the module target triple with x86_64-pc-linux-gnu [-Woverride-module]
1 warning generated.

$ ./a.out
4
4
```

### Considerations

`scanf` is [generally](https://www.quora.com/Why-is-scanf-in-C-considered-harmful-or-bad) [considered](https://stackoverflow.com/questions/2430303/disadvantages-of-scanf) [to](https://en.wikipedia.org/wiki/Scanf_format_string#Vulnerabilities) [be](http://sekrit.de/webdocs/c/beginners-guide-away-from-scanf.html) [unsafe.](https://www.quora.com/Is-using-scanf-in-C-programming-secure)

Here's what happens when we feed our example a string:

```shell
$ ./a.out
fasd
0
```

And some floats:

```shell
$ ./a.out
2.3333333
2

$ ./a.out
2.666666666666667
2
```

You'll also notice the format strings for `printf` and `scanf` differ by a newline. I noticed that using the `printf` format string (with the newline) for `scanf` resulted in the following behavior:

```shell
$ ./a.out
2 # normal readNat input
3 # entered by me
2 # printNat output
```

It's been a while since I'd used `scanf` (it's unsafe!) so I had forgotten how it deals with [trailing](https://stackoverflow.com/a/19499406/5692730) [whitespace.](https://stackoverflow.com/questions/41767371/scanf-and-newlines-in-c/41767965#41767965)

A solution to this would be to implement `readNat` directly in the compiler, as suggested by the LLVM [tutorial.](https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl04.html#full-code-listing) This would make it simple to implement error checking in `readNat`. I have not done this myself; when I attempted to follow their suggestion I ran into problems calling the function from the IR and opted to find an alternate method.
