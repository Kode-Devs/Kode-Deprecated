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

import java.util.List;
import java.util.Objects;
import kni.KodeObject;
import math.KodeMath;

/**
 * Utility class for performing pre-defined comparison operations (i.e., ==, !=,
 * &lt;, &lt;=, &gt; and &gt;=) on two objects of same/different type.
 *
 * @implNote If it fails to perform the operation i.e., it can not find any
 * pre-defined operation for the types of the object, then it throws an instance
 * of the {@link NotImplemented} error class.
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
abstract class Comparator {

    /**
     * Checks for weather {@literal left} object is equal to {@literal right}
     * object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean eq(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) left) && ValueNone.isNone((KodeInstance) right)) {
                res = true;
            } else if (ValueNone.isNone((KodeInstance) left) || ValueNone.isNone((KodeInstance) right)) {
                res = false;
            }
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.equal(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = Objects.equals(ValueString.toStr(left), ValueString.toStr(right));
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                List<KodeObject> l = ValueList.toList(left);
                List<KodeObject> r = ValueList.toList(right);
                if (l.size() == r.size()) {
                    res = true;
                    for (int i = 0; i < l.size(); i++) {
                        if (!eq(l.get(i), r.get(i), interpreter)) {
                            res = false;
                        }
                    }
                } else {
                    res = false;
                }
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * Checks for weather {@literal left} object is not equal to
     * {@literal right} object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean ne(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) left) && ValueNone.isNone((KodeInstance) right)) {
                res = false;
            } else if (ValueNone.isNone((KodeInstance) left) || ValueNone.isNone((KodeInstance) right)) {
                res = true;
            }
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.not_equal(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = !Objects.equals(ValueString.toStr(left), ValueString.toStr(right));
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                List<KodeObject> l = ValueList.toList(left);
                List<KodeObject> r = ValueList.toList(right);
                if (l.size() == r.size()) {
                    res = false;
                    for (int i = 0; i < l.size(); i++) {
                        if (ne(l.get(i), r.get(i), interpreter)) {
                            res = true;
                        }
                    }
                } else {
                    res = true;
                }
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * Checks for weather {@literal left} object is less than {@literal right}
     * object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean lt(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.less(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = (ValueString.toStr(left).compareTo(ValueString.toStr(right)) < 0);
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right), interpreter) < 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * Checks for weather {@literal left} object is less than or equal to
     * {@literal right} object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean le(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.less_equal(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = (ValueString.toStr(left).compareTo(ValueString.toStr(right)) <= 0);
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right), interpreter) <= 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * Checks for weather {@literal left} object is greater than
     * {@literal right} object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean gt(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.greater(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = (ValueString.toStr(left).compareTo(ValueString.toStr(right)) > 0);
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right), interpreter) > 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * Checks for weather {@literal left} object is greater than or equal to
     * {@literal right} object, or not.
     *
     * @param left Left Object.
     * @param right Right Object.
     * @param interpreter Instance of associated interpreter.
     * @return Returns either {@code true} or {@code false} based on the
     * operation to be performed.
     */
    static boolean ge(KodeObject left, KodeObject right, Interpreter interpreter) {
        Boolean res = null;
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueBool.isBool((KodeInstance) left)) {
                left = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) left) ? 1 : 0);
            }
            if (ValueBool.isBool((KodeInstance) right)) {
                right = interpreter.toKodeValue(ValueBool.toBoolean((KodeInstance) right) ? 1 : 0);
            }
            if (ValueNumber.isNumber((KodeInstance) left) && ValueNumber.isNumber((KodeInstance) right)) {
                res = KodeMath.greter_equal(ValueNumber.toNumber(left), ValueNumber.toNumber(right));
            }
            if (ValueString.isString((KodeInstance) left) && ValueString.isString((KodeInstance) right)) {
                res = (ValueString.toStr(left).compareTo(ValueString.toStr(right)) >= 0);
            }
            if (ValueList.isList((KodeInstance) left) && ValueList.isList((KodeInstance) right)) {
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right), interpreter) >= 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    /**
     * This method performs element-wise comparison of two list objects and
     * finally returns a integer value representing the result.
     *
     * @param l1 Left list object.
     * @param l2 Right list object.
     * @param interpreter Instance of the associated interpreter.
     * @return Returns negative integer, zero, or positive integer based on
     * weather {@literal l1} is less than, equal to, or greater than
     * {@literal l2} respectively.
     */
    private static int list_comparator(List<KodeObject> l1, List<KodeObject> l2, Interpreter interpreter) {
        int lim = Math.min(l1.size(), l2.size());
        for (int k = 0; k < lim; k++) {
            KodeObject c1 = l1.get(k);
            KodeObject c2 = l2.get(k);
            try {
                if (lt(c1, c2, interpreter)) {
                    return -1;
                } else if (gt(c1, c2, interpreter)) {
                    return 1;
                }
            } catch (NotImplemented e) {
                throw new RuntimeError("Either of the Lists contains non-comparable elements", null);
            }
        }
        return l1.size() - l2.size();
    }
}
