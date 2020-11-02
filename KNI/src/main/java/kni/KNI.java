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
public interface KNI {

    public KodeNativeObject call(KodeNativeObject... args) throws Throwable;
}
