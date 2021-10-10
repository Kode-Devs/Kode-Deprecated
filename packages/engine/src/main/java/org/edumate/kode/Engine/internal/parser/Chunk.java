package org.edumate.kode.Engine.internal.parser;

import org.edumate.kode.Engine.internal.runtime.ScriptObject;

import java.util.*;

/**
 * Represents a Chunk of Bytecode
 */
public class Chunk {
    private final List<Object> code;
    private final List<ScriptObject> constants;

    public Chunk() {
        code = new ArrayList<>(8);
        constants = new ArrayList<>(8);
    }

    /**
     * Writes a new Byte to the bytecode chunk.
     *
     * @param code the new Byte to be written. Must be not {@code null}
     * @param line line number of the new Byte
     * @return Returns the offset value of the Byte written
     */
    public <T> int writeByte(final T code, final int line) {
        Objects.requireNonNull(code);
        this.code.add(code);
        return this.code.size() - 1;
    }

    /**
     * Adds a new Constant to the constant pool of the bytecode chunk.
     *
     * @param value the new Constant. Must be not {@code null}
     * @return Returns the pointer value of the Constant in the pool
     */
    public int addConstant(final ScriptObject value) {
        Objects.requireNonNull(value);
        constants.add(value);
        return constants.size() - 1;
    }

    /**
     * Reads the Byte at the given offset.
     *
     * @param offset the given offset
     * @return Returns the Byte read
     */
    public <T> T readByte(final int offset) {
        if (offset < 0 || offset >= code.size()) return null;
        return (T) code.get(offset);
    }

    /**
     * Reads the Constant at the given constant pool pointer.
     * @param index the given constant pool pointer
     * @return Returns the Constant
     */
    public ScriptObject readConstant(final int index) {
        if (index < 0 || index >= constants.size()) return null;
        return constants.get(index);
    }

    public int count() {
        return code.size();
    }
}
