package org.edumate.kode.Engine.internal.runtime;

import org.edumate.kode.Engine.internal.enums.InterpretResult;
import org.edumate.kode.Engine.internal.parser.Chunk;
import org.edumate.kode.Engine.internal.parser.Compiler;

public final class VirtualMachine {

    private final String name;
    private Chunk chunk;
    private int ip;

    public VirtualMachine(String name) {
        this.name = name;
    }

    public InterpretResult interpret(String source) {
        try {
            final Chunk chunk = new Chunk();
            Compiler compiler = new Compiler(name, source, chunk);

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
        return InterpretResult.INTERPRET_OK;
    }
}
