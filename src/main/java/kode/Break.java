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
class Break extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Break() {
        super("Cannot break from top-level code.", null, false, false);
    }
}
