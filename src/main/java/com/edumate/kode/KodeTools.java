/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dell
 */
class KodeTools {

    //<editor-fold defaultstate="collapsed" desc="clock()">
    public static Double clock() {
        return (double) System.currentTimeMillis();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Number(obj)">
    public static Double toNumber(Object num) throws Exception {
        try {
            if (checkNumberFormat(num.toString())) {
                return Double.parseDouble(num.toString());
            } else {
                throw new Exception("Number Format Error : " + num);
            }
        } catch (Exception e) {
            throw new Exception("Number Format Error : " + num);
        }
    }

    private static boolean checkNumberFormat(String num) {
        if (num.isEmpty()) {
            return false;
        }
        int i = 0;
        if (charAt(num, 0) == '+' || charAt(num, 0) == '-') {
            i++;
        }
        while (isDigit(charAt(num, i))) {
            i++;
        }

        // Look for a fractional part.
        if (charAt(num, i) == '.' && isDigit(charAt(num, i + 1))) {
            // Consume the "."
            i++;

            while (isDigit(charAt(num, i))) {
                i++;
            }
        }

        // Look for a exponential part.
        if ((charAt(num, i) == 'e' || charAt(num, i) == 'E') && (isDigit(charAt(num, i + 1)) || charAt(num, i + 1) == '+' || charAt(num, i + 1) == '-')) {
            // Consume the "e"
            i++;
            if (charAt(num, i) == '+' || charAt(num, i) == '-') {
                i++;
            }

            while (isDigit(charAt(num, i))) {
                i++;
            }
        }

        return i >= num.length();
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static char charAt(String s, int i) {
        if (i < s.length()) {
            return s.charAt(i);
        } else {
            return ' ';
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="print(str="")">
    public static void print(Object str) {
        KodeIO.printfln(str);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="input()">
    public static String input() {
        return new Scanner(java.lang.System.in).nextLine();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="exit(status=0)">
    public static void exit(Object status) {
        System.exit(((Double) status).intValue());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="run(fn)">
    public static void run(Object path) throws Exception {
        try {
            Kode.warning("Try not to use this function as it is still under developing");
            Kode.runFile(path.toString(), new Interpreter());
        } catch (Exception e) {
            throw new Exception("Failed to run file from path \"" + path + "\"");
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="len(obj)">
    public static Double len(Object object) {

        if (object instanceof String) {
            return new Double(((String) object).length());
        }

        if (object instanceof List) {
            return new Double(((List) object).size());
        }

        // Unreacheble
        return null;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="type(obj)">
    public static String type(Object object) {
        return Kode.type(object);
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="error(obj)">
    public static void error(Object msg) throws Exception {
        throw new Exception(msg.toString());
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="eval(cmd)">
    public static void exec(Object cmd) throws Exception {
        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException ex) {
            throw new Exception("Can not execute System command \"" + cmd.toString() + "\"");
        }
    }
//</editor-fold>

}
