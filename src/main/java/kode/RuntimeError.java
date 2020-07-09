/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dell
 */
class RuntimeError extends RuntimeException {

    List<Token> token = new ArrayList<>();
    KodeInstance instance = null;
    String type = null;

    RuntimeError(String message, Token token) {
        this.token.add(token);
        this.instance = ValueError.create(message);
        this.type = "Runtime Error";
    }

    RuntimeError(String message) {
        this(message, null);
    }

    RuntimeError(KodeInstance instance) {
        this.instance = instance;
    }

    @Override
    public String getMessage() {
        String class_name = this.type == null ? this.instance.klass.class_name : this.type;
        return class_name + " : " + this.instance.toString();
    }

    @Override
    public String getLocalizedMessage() {
        return this.getMessage();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

}
