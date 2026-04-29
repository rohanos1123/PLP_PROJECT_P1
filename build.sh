#!/bin/bash
for i in {0..28}; do
    java -cp lib/antlr-4.13.2-complete.jar:bin Main compile tests/test$i.pas
done