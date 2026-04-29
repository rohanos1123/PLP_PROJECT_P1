# Delphi Compiler Group 43
## Rohan Rao UFID: 82869832
## Tomas Rivera UFID: 16024253

## Solution Components
* Delphi grammar implementation is found under `grammar/Delphi.g4`.
* All source files for the Java compiler are found under `src/Generator/`, the entrypoint/driver program is found under `src/Main.java`.
* All test example files are in the `tests/` directory, each named `test{n}.pas` *(test files range from test0.pas to test28.pas)*.

## Working Features
* Classes and Objects
* Constructors and Destructors
* Encapsulation
* Inheritance
* Interfaces
* Functions, Procedures, and Methods
* Variables of type integer, string, float, char, and bool.
* Reading from stdin with ReadLn
* Writing to stdout with WriteLn
* While and For loops
* Recursion
* Break and Continue
* Nested Functions and Procedures
* Binary operations
* Logical operations
* Comparison operations
* Parenthetical grouping
* If, else if, and else statements
* Type checking for assignments, if statements, and for loops

## Compiling and Running the Compiler
This project was compiled and tested using the OpenJDK implementation of java, version 25.0.2. Other versions should work but if there are any issues, use that version to compile and run.
To compile all necessary files for the compiler, run:
* `java -jar lib/antlr-4.13.2-complete.jar -Dlanguage=Java -visitor -o gen -package grammar grammar/Delphi.g4`
* `javac -d bin/ -cp lib/antlr-4.13.2-complete.jar gen/grammar/*.java src/**/*.java`

To run the compiler against one of the test files, run:
* `java -cp lib/antlr-4.13.2-complete.jar:bin Main compile tests/test{n}.pas`
* The output file llvm ir file will be located under `output/test{n}.ll`

To compile to a native binary with llvm, run:
* `clang -O0 output/test{n}.ll -o out`

To run a native binary compiled with llvm, run: (Note that compiled .ll files where a Delphi Compilation Error occurred will just contain an empty main() function)
* `./out`

To compile to wasm with a js glue file, run (ENSURE EMSCRIPTEN IS INSTALLED):
* `mkdir wasm`
* `emcc output/test{n}.ll -o wasm/test{n}.js -sEXPORT_ES6=1 -sINVOKE_RUN=0 --emrun -sEXPORTED_FUNCTIONS=_f1,_f2,...`

**Note: some of the test files may require user input before displaying anything.*

**Note: some of the test files may output something like "Delphi Runtime Error: Line: k, Column: l: Msg...", these are not failing test cases but rather successful tests for erroneous behavior in the delphi source file.*
