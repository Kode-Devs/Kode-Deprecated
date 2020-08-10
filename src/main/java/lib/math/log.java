/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib.math;

import kni.KNI;
import kni.KodeObject;
import math.KodeNumber;

/**
 *
 * @author dell
 */
public class log implements KNI {

    @Override
    public KodeObject call(KodeObject... args) throws Throwable {
        return new KodeObject(KodeNumber.valueOf(Math.log(((KodeNumber)args[0].get()).getFloat())));
    }
    
}
