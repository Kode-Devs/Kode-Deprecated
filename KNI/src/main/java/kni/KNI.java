/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kni;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 *
 * @author dell
 */
@Retention(RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface KNI {
    
    public String doc();
}
