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
class KodeModule {

    String name;
    Interpreter inter = new Interpreter();
    private final String path;
    String __doc__;

    KodeModule(String name, String path) {
        this.name = name;
        this.path = path;
    }

    void run() throws Throwable {
        this.__doc__ = Kode.runLib(path, inter);
    }

    Object get(Token name) {
        try {
            return inter.globals.get(name);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name.lexeme + "'.", name);
        }
    }

    void set(Token name, Object value) {
        try {
            inter.globals.assign(name, value);
        } catch (RuntimeError ex) {
            throw new RuntimeError("Module '" + this.name + "' has no attribute '" + name.lexeme + "'.", name);
        }
    }

    @Override
    public String toString() {
        return "<module '" + this.name + "'>";
    }
}
