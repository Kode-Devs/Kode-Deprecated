/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package math;

/**
 *
 * @author dell
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

    public static boolean greter_equal(KodeNumber left, KodeNumber right) {
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

    public static KodeNumber substract(KodeNumber left, KodeNumber right) {
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

    public static KodeNumber divide(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().divide(right.getInteger())); // NOTE : Division will work as integral div if both are ints 
        }
        return KodeNumber.valueOf(left.getFloat() / right.getFloat());
    }

    public static KodeNumber floor_div(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().divide(right.getInteger()));
        }
        return KodeNumber.valueOf(Math.floor(left.getFloat() / right.getFloat()));
    }

    public static KodeNumber modulo(KodeNumber left, KodeNumber right) {
        if (left.isInteger() && right.isInteger()) {
            return KodeNumber.valueOf(left.getInteger().remainder(right.getInteger()));
        }
        return KodeNumber.valueOf(left.getFloat() % right.getFloat());
    }

    public static KodeNumber exponent(KodeNumber left, KodeNumber right) throws Exception {
        if (left.isInteger() && right.isInteger()) {
                return KodeNumber.valueOf(left.getInteger().pow(right.getAsIndex()));
        }
        return KodeNumber.valueOf(Math.pow(left.getFloat(), right.getFloat()));
    }
}
