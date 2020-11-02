package kni;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dell
 */
public interface KodeObject {

    /**
     * Call
     */
    public KodeObject call(KodeObject... args);

    public KodeObject get(String name);

    public void set(String name, KodeObject value);
}
