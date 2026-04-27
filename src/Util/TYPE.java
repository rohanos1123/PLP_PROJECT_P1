package Util;

public enum TYPE implements GenericType {
    INT,        // Integer (i32)
    BOOL,        // Boolean (i1)
    STRING,     // String (i8*)
    VOID,        // Unassigned (void)

    INTPTR,     // i32*
    BOOLPTR,     // i1*

    REFERENCE;  // Generic Object Reference
}