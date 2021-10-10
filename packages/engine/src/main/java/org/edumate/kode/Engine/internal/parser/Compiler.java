package org.edumate.kode.Engine.internal.parser;

import org.edumate.kode.Engine.internal.enums.OpCode;
import org.edumate.kode.Engine.internal.enums.TokenType;
import org.edumate.kode.Engine.internal.runtime.ScriptObject;

import java.util.HashMap;
import java.util.Map;

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
        parsePrecedence(Precedence.PREC_ASSIGNMENT);
    }

    private void number() {
        final double value = Double.parseDouble(toLiteral(this.previous));
        emitConstant(new ScriptObject() {
            @Override
            public String toString() {
                return "" + value;
            }
        });
    }

    private void grouping() {
        expression();
        consume(TokenType.TOKEN_RIGHT_PAREN, "Expect ')' after expression.");
    }

    private void unary() {
        final TokenType operatorType = this.previous.type;

        // Compile the operand.
        parsePrecedence(Precedence.PREC_UNARY);

        // Emit the operator instruction.
        switch (operatorType) {
            case TOKEN_MINUS:
                emitByte(OpCode.OP_NEGATE);
                break;
        }
    }

    private void binary() {
        final TokenType operatorType = this.previous.type;
        ParseRule rule = getRule(operatorType);
        parsePrecedence(rule.precedence + 1);

        switch (operatorType) {
            case TOKEN_PLUS:
                emitByte(OpCode.OP_ADD);
                break;
            case TOKEN_MINUS:
                emitByte(OpCode.OP_SUBTRACT);
                break;
            case TOKEN_STAR:
                emitByte(OpCode.OP_MULTIPLY);
                break;
            case TOKEN_SLASH:
                emitByte(OpCode.OP_DIVIDE);
                break;
        }
    }

    private ParseRule getRule(final TokenType type) {
        return rules.get(type);
    }

    private void parsePrecedence(final int precedence) {
        advance();
        var prefixRule = getRule(this.previous.type).prefix;
        if (prefixRule == null) {
            error("Expect expression.");
            return;
        }

        prefixRule.run();

        while (precedence <= getRule(this.current.type).precedence) {
            advance();
            var infixRule = getRule(this.previous.type).infix;
            infixRule.run();
        }
    }

    // ----------------------------------------------------------------------- parse rules

    private final Map<TokenType, ParseRule> rules = new HashMap<>();

    {
        rules.put(TokenType.TOKEN_LEFT_PAREN, new ParseRule(this::grouping, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_RIGHT_PAREN, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_LEFT_BRACE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_RIGHT_BRACE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_COMMA, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_DOT, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_MINUS, new ParseRule(this::unary, this::binary, Precedence.PREC_TERM));
        rules.put(TokenType.TOKEN_PLUS, new ParseRule(null, this::binary, Precedence.PREC_TERM));
        rules.put(TokenType.TOKEN_SEMICOLON, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_SLASH, new ParseRule(null, this::binary, Precedence.PREC_FACTOR));
        rules.put(TokenType.TOKEN_STAR, new ParseRule(null, this::binary, Precedence.PREC_FACTOR));
        rules.put(TokenType.TOKEN_BANG, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_BANG_EQUAL, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_EQUAL, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_EQUAL_EQUAL, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_GREATER, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_GREATER_EQUAL, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_LESS, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_LESS_EQUAL, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_IDENTIFIER, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_STRING, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_NUMBER, new ParseRule(this::number, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_AND, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_CLASS, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_ELSE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_FALSE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_FOR, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_FUN, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_IF, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_NONE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_OR, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_PRINT, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_RETURN, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_SUPER, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_THIS, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_TRUE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_VAR, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_WHILE, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_ERROR, new ParseRule(null, null, Precedence.PREC_NONE));
        rules.put(TokenType.TOKEN_EOF, new ParseRule(null, null, Precedence.PREC_NONE));
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

    private String toLiteral(final Token token) {
        return source.substring(token.start, token.start + token.length);
    }

    // ------------------------------------------------------------------------- emit

    private <T> void emitByte(final T code) {
        this.compilingChunk.writeByte(code, this.previous.line);
    }

    private <T1, T2> void emitBytes(final T1 byte1, final T2 byte2) {
        emitByte(byte1);
        emitByte(byte2);
    }

    private void emitReturn() {
        emitByte(OpCode.OP_RETURN);
    }

    private void emitConstant(final ScriptObject value) {
        emitBytes(OpCode.OP_CONSTANT, makeConstant(value));
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
                System.err.printf(" at '%s'", toLiteral(token));
                break;
        }

        System.err.printf(": %s\n", message);
        this.hadError = true;
    }
}
