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

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	NotImplemented() {
        super(ValueNotImplemented.create());
    }
    
    NotImplemented(KodeInstance instance){
        super(instance);
    }

}
