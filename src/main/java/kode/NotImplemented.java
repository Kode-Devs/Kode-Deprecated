/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

/**
 *
 * @author dell
 */
class NotImplemented extends RuntimeError {

    NotImplemented() {
        super(ValueNotImplemented.create());
    }
    
    NotImplemented(KodeInstance instance){
        super(instance);
    }

}
