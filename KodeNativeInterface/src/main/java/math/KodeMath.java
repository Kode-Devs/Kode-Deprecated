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
        return left.getFloat().equals(right.getFloat());
    }

    public static boolean not_equal(KodeNumber left, KodeNumber right) {
        return !left.getFloat().equals(right.getFloat());
    }

    public static boolean less(KodeNumber left, KodeNumber right) {
        return left.getFloat() < right.getFloat();
    }

    public static boolean greater(KodeNumber left, KodeNumber right) {
        return left.getFloat() > right.getFloat();
    }

    public static boolean less_equal(KodeNumber left, KodeNumber right) {
        return left.getFloat() <= right.getFloat();
    }

    public static boolean greter_equal(KodeNumber left, KodeNumber right) {
        return left.getFloat() >= right.getFloat();
    }

    public static KodeNumber neg(KodeNumber right) {
        return KodeNumber.valueOf(-right.getFloat());
    }

    public static KodeNumber pos(KodeNumber right) {
        return right;
    }

    public static KodeNumber add(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(left.getFloat() + right.getFloat());
    }

    public static KodeNumber substract(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(left.getFloat() - right.getFloat());
    }

    public static KodeNumber multiply(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(left.getFloat() * right.getFloat());
    }

    public static KodeNumber divide(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(left.getFloat() / right.getFloat());
    }

    public static KodeNumber floor_div(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(Math.floor(left.getFloat() / right.getFloat()));
    }

    public static KodeNumber modulo(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(left.getFloat() % right.getFloat());
    }

    public static KodeNumber exponent(KodeNumber left, KodeNumber right) {
        return KodeNumber.valueOf(Math.pow(left.getFloat(), right.getFloat()));
    }
}
