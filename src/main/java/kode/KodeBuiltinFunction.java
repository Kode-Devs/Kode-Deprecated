/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import math.KodeMath;

/**
 *
 * @author dell
 */
abstract class KodeBuiltinFunction extends KodeFunction {

    final String fun_name;

    KodeBuiltinFunction(String name, Environment closure, Interpreter inter) {
        super(null, closure, inter, false);
        this.fun_name = name;
        this.__doc__ = doc();
    }
    
    String doc(){
        return null; 
    }

    @Override
    KodeFunction bind(KodeInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        this.closure = environment;
        return this;
    }

    @Override
    public String toString() {
        return "<function '" + fun_name + "'>";
    }

    @Override
    public abstract List<Pair<String, Object>> arity();

    @Override
    public abstract Object call(Map<String, Object> arguments);

    //<editor-fold defaultstate="collapsed" desc="Comparator">
    boolean eq(Object left, Object right) {
        Boolean res = Objects.equals(left, right);
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) left) && ValueNone.isNone((KodeInstance) right)){
                res = true;
            }
            else if (ValueNone.isNone((KodeInstance) left) || ValueNone.isNone((KodeInstance) right)){
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
                List l = ValueList.toList(left);
                List r = ValueList.toList(right);
                if (l.size() == r.size()) {
                    res = true;
                    for (int i = 0; i < l.size(); i++) {
                        if (!eq(l.get(i), r.get(i))) {
                            res = false;
                        }
                    }
                } else {
                    res = false;
                }
            }
        }
        return res;
    }

    boolean ne(Object left, Object right) {
        Boolean res = !Objects.equals(left, right);
        if (left instanceof KodeInstance && right instanceof KodeInstance) {
            if (ValueNone.isNone((KodeInstance) left) && ValueNone.isNone((KodeInstance) right)){
                res = false;
            }
            else if (ValueNone.isNone((KodeInstance) left) || ValueNone.isNone((KodeInstance) right)){
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
                List l = ValueList.toList(left);
                List r = ValueList.toList(right);
                if (l.size() == r.size()) {
                    res = false;
                    for (int i = 0; i < l.size(); i++) {
                        if (ne(l.get(i), r.get(i))) {
                            res = true;
                        }
                    }
                } else {
                    res = true;
                }
            }
        }
        return res;
    }

    boolean lt(Object left, Object right) {
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
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right)) < 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    boolean le(Object left, Object right) {
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
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right)) <= 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    boolean gt(Object left, Object right) {
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
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right)) > 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    boolean ge(Object left, Object right) {
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
                res = (list_comparator(ValueList.toList(left), ValueList.toList(right)) >= 0);
            }
        }
        if (res == null) {
            throw new NotImplemented();
        }
        return res;
    }

    private int list_comparator(List l1, List l2) {
        int lim = Math.min(l1.size(), l2.size());
        for (int k = 0; k < lim; k++) {
            Object c1 = l1.get(k);
            Object c2 = l2.get(k);
            try {
                if (lt(c1, c2)) {
                    return -1;
                } else if (gt(c1, c2)) {
                    return 1;
                }
            } catch (NotImplemented e) {
                throw new RuntimeError("Either of the Lists contains non-comparable elements", null);
            }
        }
        return l1.size() - l2.size();
    }
//</editor-fold>

}
