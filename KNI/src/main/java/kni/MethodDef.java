/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kni;

import java.util.Map;

public abstract class MethodDef {
    
    public final String doc;
    public final String[] params_name;

    public MethodDef(String doc, String... params_name) {
        this.params_name = params_name;
        this.doc = doc;
    }
    
    public abstract KodeObject call(Map<String,KodeObject> env);
}
