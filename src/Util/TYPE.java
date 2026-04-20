package Util;

public enum TYPE implements GenericType {
    INT,        // Integer
    BOOL,        // Boolean
    STRING,     // String
    ARRAY,      // Array
    REFERENCE,  // Object Reference
    FPTR,       // Function/Method Pointer
    VOID;        // Unassigned (void)
}