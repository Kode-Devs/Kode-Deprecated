/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.HashMap;

/**
 *
 * @author dell
 */
abstract class Value extends KodeClass {

    Value(String name, Interpreter interpreter) {
        this(name, null, interpreter);
    }

    Value(String name, KodeClass superclass, Interpreter interpreter) {
        super(name, superclass, new HashMap(), interpreter);
        this.__doc__ = doc();
    }
    
    String doc(){
        return null; 
    }

    final static boolean instanceOf(KodeClass i, Class c) {
        if (i == null) {
            return false;
        }
        if (c.isInstance(i)) {
            return true;
        }
        if (i.superclass != null) {
            return instanceOf(i.superclass, c);
        }
        return false;
    }

}
