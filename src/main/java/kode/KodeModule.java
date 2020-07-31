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
    private final String path;

    KodeModule(String name, String path) {
        super(null);
        this.name = name;
        this.path = path;
    }

    void run() throws Throwable {
        this.__doc__ = Kode.runLib(path, inter);
    }

    @Override
    Object get(Token name) {
        return inter.globals.get(name);
    }

    @Override
    Object get(String name) {
        return inter.globals.get(name);
    }

    @Override
    void set(Token name, Object value) {
        this.set(name.lexeme, value);
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
