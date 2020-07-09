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
    Break() {
        super("Cannot break from top-level code.", null, false, false);
    }
}
