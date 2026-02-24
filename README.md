# Delphi Interpreter Group 43

## Solution Components
* Delphi grammar implementation `delphi.g4` is at the root of the project
* All source files for the Java interpreter are found under `src/Interpreter/`
* All test example files are in the `samples/` directory, each named `test{n}.pas` 

## Working Features
* Classes and Objects
* Constructors and Destructors
* Encapsulation
* Inheritance
* Interfaces
* Functions, Procedures, and Methods
* Variables of integer type only
* Reading from stdin with ReadLn
* Writing to stdout with WriteLn

## Compiling and Running the Interpreter
This project was compiled and tested using the OpenJDK implementation of java, version 25.0.2. Other versions should work but if there are any issues, use that version to compile and run.
To compile all necessary files for the interpreter, run:
* `java -jar lib/antlr-4.13.2-complete.jar -Dlanguage=Java -visitor -o gen/Grammar -package Grammar delphi.g4`
* `javac -d bin/ -cp lib/antlr-4.13.2-complete.jar gen/Grammar/*.java src/Interpreter/*.java src/Main.java`

To run the interpreter against one of the test files, run:
* `java -cp lib/antlr-4.13.2-complete.jar:bin Main samples/test{n}.pas`

**Note: some of the test files may require user input before displaying anything.*

**Note: some of the test files may output something like "Delphi Runtime Error: Line: k, Column: l: Msg...", these are not failing test cases but rather successful tests for erroneous behavior in the delphi source file.*
