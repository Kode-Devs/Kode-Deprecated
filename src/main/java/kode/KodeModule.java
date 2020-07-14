/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

/**
 *
 * @author dell
 */
class KodeModule extends KodeInstance {

    String name;
    Interpreter inter = new Interpreter();
    boolean hadError = false;
    boolean hadRuntimeError = false;
    private final String path;

    KodeModule(String name, String path) {
        super(null);
        this.name = name;
        this.path = path;
    }

    void run() throws Exception {
        this.__doc__ = Kode.runLib(path, inter);
        this.hadError = Kode.hadError;
        this.hadRuntimeError = Kode.hadRuntimeError;
    }

    @Override
    Object get(Token name) {
        try {
            return inter.globals.get(name);
        } catch (Exception e) {
            throw new RuntimeError(
                    "Undefined property '" + name.lexeme + "'.",
                    name);
        }
    }
    
    @Override
    void set(String name, Object value) {
        inter.globals.define(name, value);
    }
    
    @Override
    public String toString() {
        return "<module '" + this.name + "'>";
    }
}
