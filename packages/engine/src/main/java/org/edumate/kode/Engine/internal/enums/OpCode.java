package org.edumate.kode.Engine.internal.enums;

public enum OpCode {
    OP_LINENUMBER,      // Line Information
    OP_CONSTANT,        // Literals
    OP_NONE,            // None
    OP_TRUE,            // True
    OP_FALSE,           // False
    OP_POP,
    OP_ADD,             // Binary Addition
    OP_SUBTRACT,        // Binary Subtraction
    OP_MULTIPLY,        // Binary Multiplication
    OP_DIVIDE,          // Binary Division
    OP_NEGATE,          // Unary Negate
    OP_PRINT,           // Print
    OP_RETURN,          // End Execution and Returns the Current Result
}
