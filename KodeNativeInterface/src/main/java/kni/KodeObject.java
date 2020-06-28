/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kni;


/**
 *
 * @author dell
 */
public final class KodeObject {
    private Object data;
    
    public KodeObject(Object data){
        this.data = data;
    }
    
    public Object get(){
        return this.data;
    }
    
    @Override
    public String toString(){
        return data.toString();
    }
}
