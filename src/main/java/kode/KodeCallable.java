/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.List;
import java.util.Map;

/**
 *
 * @author dell
 */
interface KodeCallable {
    
    List<Pair<String,Object>> arity();

    Object call(Map<String, Object> arguments);
    
    @Override
    abstract String toString();
}
