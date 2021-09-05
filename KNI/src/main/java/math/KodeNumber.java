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
package math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Kode Number
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
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
