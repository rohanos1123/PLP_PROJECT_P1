package Util;

public enum TYPE implements GenericType {
    INT,        // Integer (i32)
    REAL,      // Real (double)
    BOOL,        // Boolean (i1)
    CHAR,        // Character (i8)
    STRING,     // String (i8*)
    VOID,        // Unassigned (void)

    INTPTR,     // i32*
    BOOLPTR,     // i1*
    REALPTR,   // double*
    CHARPTR,   // i8*

    REFERENCE;  // Generic Object Reference
}