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

/**
 * Utility class for handling Math operations
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
public abstract class KodeMath {

    public static boolean equal(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return left.getInteger().equals(right.getInteger());
        }
        return left.getFloat().equals(right.getFloat());
    }

    public static boolean not_equal(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return !left.getInteger().equals(right.getInteger());
        }
        return !left.getFloat().equals(right.getFloat());
    }

    public static boolean less(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return left.getInteger().compareTo(right.getInteger()) < 0;
        }
        return left.getFloat() < right.getFloat();
    }

    public static boolean greater(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return left.getInteger().compareTo(right.getInteger()) > 0;
        }
        return left.getFloat() > right.getFloat();
    }

    public static boolean less_equal(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return left.getInteger().compareTo(right.getInteger()) <= 0;
        }
        return left.getFloat() <= right.getFloat();
    }

    public static boolean greater_equal(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return left.getInteger().compareTo(right.getInteger()) >= 0;
        }
        return left.getFloat() >= right.getFloat();
    }

    public static KodeNumber neg(KodeNumber right) {
        if (right.isInteger()) {
            return KodeNumber.valueOf(right.getInteger().negate());
        }
        return KodeNumber.valueOf(-right.getFloat());
    }

    public static KodeNumber pos(KodeNumber right) {
        return right;
    }

    public static KodeNumber add(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().add(right.getInteger()));
        }
        return KodeNumber.valueOf(left.getFloat() + right.getFloat());
    }

    public static KodeNumber subtract(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().subtract(right.getInteger()));
        }
        return KodeNumber.valueOf(left.getFloat() - right.getFloat());
    }

    public static KodeNumber multiply(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().multiply(right.getInteger()));
        }
        return KodeNumber.valueOf(left.getFloat() * right.getFloat());
    }

    public static KodeNumber divide(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (KodeMath.equal(right, KodeNumber.valueOf(0))) {
            throw new ArithmeticException("Division by Zero.");
        }
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().divide(right.getInteger())); // NOTE : Division will work as integral div if both are ints 
        }
        return KodeNumber.valueOf(left.getFloat() / right.getFloat());
    }

    public static KodeNumber floor_div(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (KodeMath.equal(right, KodeNumber.valueOf(0))) {
            throw new ArithmeticException("Division by Zero.");
        }
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().divide(right.getInteger()));
        }
        return KodeNumber.valueOf(Math.floor(left.getFloat() / right.getFloat()));
    }

    public static KodeNumber modulo(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (KodeMath.equal(right, KodeNumber.valueOf(0))) {
            throw new ArithmeticException("Division by Zero.");
        }
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().remainder(right.getInteger()));
        }
        return KodeNumber.valueOf(left.getFloat() % right.getFloat());
    }

    public static KodeNumber exponent(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (left.isInteger() && right.isInteger()) {
            try {
                return KodeNumber.valueOf(left.getInteger().pow(right.getAsIndex()));
            } catch (ArithmeticException ignored) {
            }
        }
        return KodeNumber.valueOf(Math.pow(left.getFloat(), right.getFloat()));
    }

    public static KodeNumber lshift(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().shiftLeft(right.getAsIndex()));
        }
        throw new ArithmeticException();
    }

    public static KodeNumber rshift(KodeNumber left, KodeNumber right) throws ArithmeticException {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().shiftRight(right.getAsIndex()));
        }
        throw new ArithmeticException();
    }
}
