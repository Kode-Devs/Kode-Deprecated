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
public final class KodeNativeObject {
    private final Object data;
    private boolean isnative;
    
    public KodeNativeObject(Object data){
        this.isnative = false;
        this.data = data;
    }
    
    public KodeNativeObject asNative(){
        isnative = true;
        return this;
    }
    
    public boolean isNative(){
        return this.isnative;
    }
    
    public Object get(){
        return this.data;
    }
    
    @Override
    public String toString(){
        return data.toString();
    }
}
