/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.math.BigInteger;

/**
 *
 * @author dell
 */
public class KodeNumber {

    private Double num;
    private BigInteger numInt;

    public static KodeNumber valueOf(String num) {
        return new KodeNumber(Double.valueOf(num));
    }
    
    public static KodeNumber valueOf(Number num) {
        return new KodeNumber(num.doubleValue());
    }

    private KodeNumber(Double valueOf) {
        this.num = valueOf;
    }
    
    public Double getFloat(){
        return this.num;
    }
    
    public BigInteger getInteger(){
        return this.numInt;
    }
    
    public int getAsIndex(){
        return num.intValue();
    }
}
