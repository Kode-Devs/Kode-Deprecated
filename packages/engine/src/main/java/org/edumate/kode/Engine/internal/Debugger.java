package org.edumate.kode.Engine.internal;

import org.edumate.kode.Engine.internal.enums.OpCode;
import org.edumate.kode.Engine.internal.enums.TokenType;
import org.edumate.kode.Engine.internal.parser.Chunk;
import org.edumate.kode.Engine.internal.parser.Compiler;
import org.edumate.kode.Engine.internal.parser.Lexer;
import org.edumate.kode.Engine.internal.parser.Token;
import org.edumate.kode.Engine.internal.runtime.ScriptObject;
import org.edumate.kode.Engine.internal.runtime.VirtualMachine;

import java.util.Stack;

public final class Debugger {

    private Debugger() {

    }

    // -------------------------------------------------------------------------------------------

    public static void debugLexer(final String source) {
        final Lexer lexer = new Lexer(source);
        int line = -1;
        while (true) {
            final Token token = lexer.scanTokenOnDemand();
            if (token.line != line) {
                System.out.printf("%4d ", token.line);
                line = token.line;
            } else {
                System.out.printf("   | ");
            }
            System.out.printf("%2d '%s'\n", token.type.ordinal(), source.substring(token.start, token.start + token.length));

            if (token.type == TokenType.TOKEN_EOF) break;
        }
    }

    // -------------------------------------------------------------------------------------------

    public static void debugCompiler(final String source) {
        final Chunk chunk = new Chunk();
        final Compiler compiler = new Compiler("test", source, chunk);
        if (compiler.compile()) {
            System.out.printf("== test chunk ==\n");

            for (int offset = 0; offset < chunk.count(); ) {
                offset = disassembleInstruction(chunk, offset);
            }
        }
    }

    public static int disassembleInstruction(final Chunk chunk, int offset) {
        Object instruction = chunk.readByte(offset);

        if (instruction == OpCode.OP_LINENUMBER) {
            System.out.printf("%04d %4d ", offset, (int) chunk.readByte(offset + 1));
            offset += 2;
            instruction = chunk.readByte(offset);
        } else {
            System.out.printf("%04d    | ", offset);
        }

        if (instruction instanceof OpCode) {
            switch ((OpCode) instruction) {
                case OP_CONSTANT:
                    return constantInstruction(chunk, offset);
                case OP_ADD:
                case OP_SUBTRACT:
                case OP_MULTIPLY:
                case OP_DIVIDE:
                case OP_NEGATE:
                case OP_RETURN:
                    return simpleInstruction((OpCode) instruction, offset);
            }
        }

        System.out.printf("Unknown opcode %s\n", instruction);
        return offset + 1;
    }

    private static int constantInstruction(Chunk chunk, int offset) {
        int constant = chunk.readByte(offset + 1);
        System.out.printf("%-16s %4d '", OpCode.OP_CONSTANT, constant);
        System.out.print(chunk.readConstant(constant));
        System.out.printf("'\n");
        return offset + 2;
    }

    private static int simpleInstruction(OpCode name, int offset) {
        System.out.printf("%s\n", name);
        return offset + 1;
    }

    // -------------------------------------------------------------------------------------------

    public static void debugVM(final String source) {
        System.out.printf("== test vm ==\n");
        VirtualMachine vm = new VirtualMachine("test");
        //noinspection InstantiationOfUtilityClass
        vm.setDebugger(new Debugger());
        vm.interpret(source);
    }

    public static void printStack(final Stack<ScriptObject> stack) {
        System.out.printf("          ");
        for (var slot : stack) {
            System.out.printf("[ ");
            System.out.print(slot);
            System.out.printf(" ]");
        }
        System.out.printf("\n");
    }
}
