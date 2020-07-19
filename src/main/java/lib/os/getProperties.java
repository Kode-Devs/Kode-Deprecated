/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib.os;

import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
public class getProperties implements KNI {

    @Override
    public KodeObject call(KodeObject... args) throws Throwable {
        try {
            return new KodeObject(System.getProperty(args[0].toString()));
        } catch (Exception e) {
            return new KodeObject(null);
        }
    }

}
