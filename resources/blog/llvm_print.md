---
title: Generating Calls to printf From LLVM IR
date: 2020-06-02T22:22:22-04:00
---

## Introduction

I'm spending my summer using LLVM to generate code for DJ, the programming language [Dr. Ligatti](https://www.cse.usf.edu/~ligatti/) devised for the spring 2020 Compilers course at USF. DJ provides a `printNat` function, which prints a natural number to the console. In the course, this function was readily available in the instruction set our compilers generated (this was also devised by Dr. Ligatti.) Because `printNat` is in this sense provided by the runtime, I had to make it available in some fashion from my LLVM-backed compiler. I wasn't able to succeed in doing this the way the LLVM [Kaleidoscope](https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl04.html) tutorial suggests, so [after](https://stackoverflow.com/questions/35526075/llvm-how-to-implement-print-function-in-my-language) [some](https://the-ravi-programming-language.readthedocs.io/en/latest/ravi-jit-initial.html) [research](https://stackoverflow.com/questions/28168815/adding-a-function-call-in-my-ir-code-in-llvm), I implemented the functionality as in the example below.

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
  /*Declare that printf exists and has signature int (i8*, ...)**/
  std::vector<Type *> args;
  args.push_back(Type::getInt8PtrTy(TheContext));
  /*`true` specifies the function as variadic*/
  FunctionType *printfType =
      FunctionType::get(Builder.getInt32Ty(), args, true);
  Function::Create(printfType, Function::ExternalLinkage, "printf",
                   TheModule.get());
  /*begin codegen for `main`*/
  FunctionType *mainType = FunctionType::get(Builder.getInt32Ty(), false);
  Function *main = Function::Create(mainType, Function::ExternalLinkage, "main",
                                    TheModule.get());
  BasicBlock *entry = BasicBlock::Create(TheContext, "entry", main);
  Builder.SetInsertPoint(entry);
  /*Set up printf arguments*/
  std::vector<Value *> printArgs;
  Value *formatStr = Builder.CreateGlobalStringPtr("%d\n");
  printArgs.push_back(formatStr);
  /*We will be printing "20"*/
  printArgs.push_back(ConstantInt::get(TheContext, APInt(32, 20)));
  Builder.CreateCall(TheModule->getFunction("printf"), printArgs);
  /*return value for `main`*/
  Builder.CreateRet(ConstantInt::get(TheContext, APInt(32, 0)));
  /*Emit the LLVM IR to the console*/
  TheModule->print(outs(), nullptr);
}
```

### Compiling

I am using clang/llvm versions 10.0.0 at the time of this writing. I compile the above as `` clang++ `llvm-config --cxxflags --ldflags --system-libs --libs all` test.cpp ``. When run, it generates the following IR:

```llvm
; ModuleID = 'inputFile'
source_filename = "inputFile"

@0 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1

declare i32 @printf(i8\*, ...)

define i32 @main() {
entry:
%0 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]\* @0, i32 0, i32 0), i32 20)
ret i32 0
}
```

Instead of writing a whole output pass (see [here](https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl08.html) for that,) we can throw the output IR into a file `test.ll` and compile it with `clang`:

```terminfo
$ clang test.ll
warning: overriding the module target triple with x86_64-pc-linux-gnu [-Woverride-module]
1 warning generated.
$ ./a.out
20
```

### A caveat and potential solution

Of course, a program won't only have one print statement in it! Using the example above as part of a `codegen` method for a print statement node in your AST will result in having a new constant declared for each print statement in the program:

```llvm
...
@0 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
@1 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
@2 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
@3 = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
...
```

I haven't resolved this issue in my own code base yet. I did attempt having the `formatStr` variable be global, but this was causing seg faults in the `CreateGlobalStringPtr` method. It seems like the [-constmerge](https://llvm.org/docs/Passes.html#constmerge-merge-duplicate-global-constants) transform pass handles this, but I have yet to implement any passes in my compiler beyond the one that emits object code.
