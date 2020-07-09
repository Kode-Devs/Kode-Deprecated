/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

/**
 *
 * @author dell
 * @param <K>
 * @param <V>
 */
class Pair<K, V> {

    public K key;
    public V value;
    TokenType type = null;

    Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    Pair(K key) {
        this(key,null);
    }
    
    Pair<K,V> setType(TokenType type){
        this.type = type;
        return this;
    }
    
     Pair copy(){
         return new Pair(key,value).setType(type);
     }
    
}
