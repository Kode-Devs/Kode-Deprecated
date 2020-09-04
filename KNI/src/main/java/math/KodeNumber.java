/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 *
 * @author dell
 */
public class KodeNumber {

    private Double num;
    private BigInteger numInt;
    private final boolean isInt;

    public static KodeNumber valueOf(String num) {
        try {
            return new KodeNumber(new BigInteger(num));
        } catch (Exception e) {
            return new KodeNumber(Double.valueOf(num));
        }
    }

    public static KodeNumber valueOf(Number num) {
        if (num instanceof BigInteger) {
            return new KodeNumber((BigInteger) num);
        } else if (num instanceof Float || num instanceof Double) {
            return new KodeNumber(num.doubleValue());
        } else {
            return new KodeNumber(new BigInteger("" + num));
        }
    }

    private KodeNumber(Double valueOf) {
        this.num = valueOf;
        this.isInt = false;
    }

    private KodeNumber(BigInteger valueOf) {
        this.numInt = valueOf;
        this.isInt = true;
    }

    public Double getFloat() {
        if (this.isInt) {
            return this.numInt.doubleValue();
        }
        return this.num;
    }

    public BigInteger getInteger() throws ArithmeticException {
        if (this.isInt) {
            return this.numInt;
        }
        return new BigDecimal(this.num).toBigIntegerExact();
    }

    public int getAsIndex() throws ArithmeticException {
        if (this.isInt) {
            return this.numInt.intValueExact();
        }
        return new BigDecimal(this.num).intValueExact();
    }

    public boolean isInteger() {
        return this.isInt;
    }

    @Override
    public String toString() {
        return "" + (this.isInt ? this.numInt : this.num);
    }
}
