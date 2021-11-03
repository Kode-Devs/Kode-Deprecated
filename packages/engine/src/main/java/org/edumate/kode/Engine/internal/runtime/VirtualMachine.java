package org.edumate.kode.Engine.internal.runtime;

import org.edumate.kode.Engine.internal.Debugger;
import org.edumate.kode.Engine.internal.enums.InterpretResult;
import org.edumate.kode.Engine.internal.enums.OpCode;
import org.edumate.kode.Engine.internal.parser.Chunk;
import org.edumate.kode.Engine.internal.parser.Compiler;

import java.util.Stack;

public final class VirtualMachine {

    private final String name;
    private Chunk chunk;
    private int ip;
    private final Stack<ScriptObject> stack;
    private Debugger debugger = null;

    public VirtualMachine(String name) {
        this.name = name;
        this.stack = new Stack<>();
    }

    public InterpretResult interpret(String source) {
        try {
            final Chunk chunk = new Chunk();
            final Compiler compiler = new Compiler(name, source, chunk);

            // Compile
            if (!compiler.compile()) {
                System.gc();
                return InterpretResult.INTERPRET_COMPILE_ERROR;
            }

            this.chunk = chunk;
            this.ip = 0;

            // Execute
            InterpretResult result = run();

            this.chunk = null;
            System.gc();
            return result;
        } catch (Throwable error) {
            return InterpretResult.INTERPRET_RUNTIME_ERROR;
        }
    }

    private InterpretResult run() {
        while (true) {
            // Debug
            if (debugger != null) {
                Debugger.printStack(stack);
                Debugger.disassembleInstruction(chunk, ip);
            }

            final OpCode instruction = READ_BYTE();
            switch (instruction) {
                case OP_LINENUMBER:
                    READ_BYTE();
                    break;
                case OP_CONSTANT:
                    ScriptObject constant = READ_CONSTANT();
                    push(constant);
                    break;
                case OP_NONE:
                    push(new ScriptObject(null));
                    break;
                case OP_TRUE:
                    push(new ScriptObject(true));
                    break;
                case OP_FALSE:
                    push(new ScriptObject(false));
                    break;
                case OP_POP:
                    pop();
                    break;
                case OP_ADD:
                    BINARY_OP('+');
                    break;
                case OP_SUBTRACT:
                    BINARY_OP('-');
                    break;
                case OP_MULTIPLY:
                    BINARY_OP('*');
                    break;
                case OP_DIVIDE:
                    BINARY_OP('/');
                    break;
                case OP_NEGATE:
                    final double value = pop().value();
                    push(new ScriptObject(-value));
                    break;
                case OP_PRINT:
                    System.out.println(pop());
                    break;
                case OP_RETURN:
                    // Exit interpreter.
                    return InterpretResult.INTERPRET_OK;
            }
        }
    }

    private <T> T READ_BYTE() {
        return chunk.readByte(ip++);
    }

    private ScriptObject READ_CONSTANT() {
        return chunk.readConstant(READ_BYTE());
    }

    private void BINARY_OP(final char c) {
        final double b = pop().value();
        final double a = pop().value();
        final double result;
        switch (c) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            default:
                result = a;
                break;
        }
        push(new ScriptObject(result));
    }

    private void push(ScriptObject value) {
        stack.push(value);
    }

    private ScriptObject pop() {
        return stack.pop();
    }

    // -------------------------------------------------------------------- debug

    public void setDebugger(Debugger debugger) {
        this.debugger = debugger;
    }
}
