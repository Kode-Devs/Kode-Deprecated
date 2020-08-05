/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.Stack;

/**
 *
 * @author dell
 */
class RuntimeError extends Error
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Stack<Token> token = new Stack<>();
    KodeInstance instance = null;

    RuntimeError(String message, Token token) {
        this(ValueError.create(message));
        this.token.add(token);
    }

    RuntimeError(String message) {
        this(message, null);
    }

    RuntimeError(KodeInstance instance) {
        this.instance = instance;
    }

    @Override
    public String getMessage() {
        return this.instance.klass.class_name + ": " + this.instance.toString();
    }

    @Override
    public String getLocalizedMessage() {
        return this.instance.toString();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

}
