/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lib;

import kni.KNI;
import kni.KodeObject;

/**
 *
 * @author dell
 */
public class NewClass implements KNI {

    static private String temp = "";

    @Override
    public KodeObject call(KodeObject[] args) {
        if (args.length == 0) {
            return new KodeObject(temp);
        } else {
            temp = args[0].get().toString();
        }
        return null;
    }

}
