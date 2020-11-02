/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import kni.KodeObject;

/**
 *
 * @author dell
 */
interface ExtKodeObject extends KodeObject {

    default KodeObject get(Token name) {
        try{
            return this.get(name.lexeme);
        } catch (RuntimeError error){
            error.token.add(name);
            throw error;
        }
    }

     default void set(Token name, KodeObject value) {
        try{
            this.set(name.lexeme, value);
        } catch (RuntimeError error){
            error.token.add(name);
            throw error;
        }
    }
}
