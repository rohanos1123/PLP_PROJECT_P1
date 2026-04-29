#!/bin/bash
for i in {0..18}; do
    clang -lm -w -O0 output/test$i.ll -o out && ./out
    java -cp lib/antlr-4.13.2-complete.jar:bin Main interpret tests/test$i.pas
done