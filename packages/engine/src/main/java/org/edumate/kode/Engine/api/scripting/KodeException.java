package org.edumate.kode.Engine.api.scripting;

public abstract class KodeException extends RuntimeException {

    // script file name
    private String fileName;
    // script line number
    private int line;
    // are the line and fileName unknown?
    private boolean lineAndFileNameUnknown;
    // script column number
    private int column;

    /**
     * Constructor to initialize error message, file name, line and column numbers.
     *
     * @param msg       exception message
     * @param fileName  file name
     * @param line      line number
     * @param column    column number
     */
    protected KodeException(final String msg, final String fileName, final int line, final int column) {
        this(msg, null, fileName, line, column);
    }

    /**
     * Constructor to initialize error message, cause exception, file name, line and column numbers.
     *
     * @param msg       exception message
     * @param cause     exception cause
     * @param fileName  file name
     * @param line      line number
     * @param column    column number
     */
    protected KodeException(final String msg, final Throwable cause, final String fileName, final int line, final int column) {
        super(msg, cause);
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }

    /**
     * Constructor to initialize error message and cause exception.
     *
     * @param msg       exception message
     * @param cause     exception cause
     */
    protected KodeException(final String msg, final Throwable cause) {
        super(msg, cause);
        // Hard luck - no column number info
        this.column = -1;
        // We can retrieve the line number and file name from the stack trace if needed
        this.lineAndFileNameUnknown = true;
    }

    /**
     * Get the source file name for this {@code KodeException}
     *
     * @return the file name
     */
    public final String getFileName() {
        ensureLineAndFileName();
        return fileName;
    }

    /**
     * Set the source file name for this {@code KodeException}
     *
     * @param fileName the file name
     */
    public final void setFileName(final String fileName) {
        this.fileName = fileName;
        lineAndFileNameUnknown = false;
    }

    /**
     * Get the line number for this {@code KodeException}
     *
     * @return the line number
     */
    public final int getLineNumber() {
        ensureLineAndFileName();
        return line;
    }

    /**
     * Set the line number for this {@code KodeException}
     *
     * @param line the line number
     */
    public final void setLineNumber(final int line) {
        lineAndFileNameUnknown = false;
        this.line = line;
    }

    /**
     * Get the column for this {@code KodeException}
     *
     * @return the column number
     */
    public final int getColumnNumber() {
        return column;
    }

    /**
     * Set the column for this {@code KodeException}
     *
     * @param column the column number
     */
    public final void setColumnNumber(final int column) {
        this.column = column;
    }

    private void ensureLineAndFileName() {
        if (lineAndFileNameUnknown) {
            for (final StackTraceElement ste : getStackTrace()) {
                    fileName = ste.getFileName();
                    line = ste.getLineNumber();
                    break;
            }

            lineAndFileNameUnknown = false;
        }
    }
}
