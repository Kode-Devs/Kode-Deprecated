/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edumate.kode;

/**
 *
 * @author dell
 */
class Hash {

    final String hash;
    
    final KodeInstance obj;

    Hash(KodeInstance obj) {
        this.obj = obj;
        this.hash = Hash.encode(obj.hashCode());
    }

    private static String encode(Integer hash) {
        return "" + hash;
    }

    @Override
    public String toString() {
        return this.hash;
    }

}
