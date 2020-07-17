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
interface KodeCallable {
    
    int arity();

    Object call(Object... arguments);
    
    @Override
    abstract String toString();
}
