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
class Return extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final Object value;

    Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}
