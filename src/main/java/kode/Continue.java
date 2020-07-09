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
class Continue extends RuntimeException {

    Continue() {
        super("Cannot continue from top-level code.", null, false, false);
    }
}
