#!/bin/bash
for i in {0..28}; do
    clang -lm -w -O0 output/test$i.ll -o out && ./out
done