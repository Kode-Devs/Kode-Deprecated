/* 
 * Copyright (C) 2020 Kode Devs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    
     Pair<K,V> copy(){
         return new Pair<>(key,value).setType(type);
     }
    
}
