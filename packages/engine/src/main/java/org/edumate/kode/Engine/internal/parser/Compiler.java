package org.edumate.kode.Engine.internal.parser;

import org.edumate.kode.Engine.internal.enums.OpCode;
import org.edumate.kode.Engine.internal.enums.TokenType;
import org.edumate.kode.Engine.internal.runtime.ScriptObject;

public final class Compiler {

    private final Lexer lexer;
    private final Chunk compilingChunk;
    private final String source;
    private final String fileName;

    private Token previous;
    private Token current;
    private boolean hadError;
    private boolean panicMode;

    public Compiler(final String fileName, final String source, final Chunk chunk) {
        this.lexer = new Lexer(source);
        this.compilingChunk = chunk;
        this.source = source;
        this.fileName = fileName;
    }

    public boolean compile() {
        this.hadError = false;
        this.panicMode = false;
        advance();
        expression();
        consume(TokenType.TOKEN_EOF, "Expect end of expression.");
        endCompiler();
        return !this.hadError;
    }

    public Chunk currentChunk() {
        return compilingChunk;
    }

    // ----------------------------------------------------------------------- parsing fns

    private void expression() {
    }

    // ----------------------------------------------------------------------- utility fns

    private void advance() {
        this.previous = this.current;

        while (true) {
            this.current = lexer.scanTokenOnDemand();
            if (this.current.type != TokenType.TOKEN_ERROR) break;

            errorAtCurrent("Lex Error");
        }
    }

    private void consume(final TokenType type, final String message) {
        if (this.current.type == type) advance();
        else errorAtCurrent(message);
    }

    // ------------------------------------------------------------------------- emit

    private <T> void emitByte(final T code) {
        this.compilingChunk.writeByte(code, this.previous.line);
    }

    private <T> void emitBytes(final T byte1, final T byte2) {
        emitByte(byte1);
        emitByte(byte2);
    }

    private void emitReturn() {
        emitByte(OpCode.OP_RETURN);
    }

    private int makeConstant(final ScriptObject value) {
        return currentChunk().addConstant(value);
    }

    private void endCompiler() {
        emitReturn();
    }

    // ------------------------------------------------------------------------- error

    private void error(final String message) {
        errorAt(this.previous, message);
    }

    private void errorAtCurrent(final String message) {
        errorAt(this.current, message);
    }

    private void errorAt(final Token token, final String message) {
        if (this.panicMode) return;
        this.panicMode = true;

        // Print Error
        System.err.printf("[line %d] Error", token.line);

        switch (token.type) {
            case TOKEN_EOF:
                System.err.printf(" at end");
                break;
            case TOKEN_ERROR:
                // Nothing
                break;
            default:
                System.err.printf(" at '%s'", source.substring(token.start, token.start + token.length));
                break;
        }

        System.err.printf(": %s\n", message);
        this.hadError = true;
    }
}
