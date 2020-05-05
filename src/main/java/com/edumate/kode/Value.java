/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edumate.kode;

import java.util.HashMap;

/**
 *
 * @author dell
 */
class Value extends KodeClass {

    Value(String name, Interpreter interpreter) {
        super(name, null, new HashMap(), interpreter);
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
