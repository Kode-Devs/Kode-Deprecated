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
    private Stack<ScriptObject> stack;
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
                case OP_CONSTANT:
                    ScriptObject constant = READ_CONSTANT();
                    push(constant);
                    break;
                case OP_ADD: BINARY_OP('+'); break;
                case OP_SUBTRACT: BINARY_OP('-'); break;
                case OP_MULTIPLY: BINARY_OP('*'); break;
                case OP_DIVIDE: BINARY_OP('/'); break;
                case OP_NEGATE:
                    final double result = Double.parseDouble(pop().toString());
                    push(new ScriptObject() {
                        public String toString() {
                            return "" + result;
                        }
                    });
                    break;
                case OP_RETURN:
                    System.out.println(pop());
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
        final double b = Double.parseDouble(pop().toString());
        final double a = Double.parseDouble(pop().toString());
        final double result;
        switch (c) {
            case '+': result = a + b; break;
            case '-': result = a - b; break;
            case '*': result = a * b; break;
            case '/': result = a / b; break;
            default: result = a; break;
        }
        push(new ScriptObject() {
            public String toString() {
                return "" + result;
            }
        });
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
