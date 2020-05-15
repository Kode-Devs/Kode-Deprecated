/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edumate.kode;

import java.awt.Color;
import java.util.Map;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

/**
 *
 * @author dell
 */
public class KodeIO {

    public static final TextIO textIO = TextIoFactory.getTextIO();
    
    private static final Color OUT_COLOR = Color.WHITE;
    private static final Color IN_COLOR = Color.CYAN;
    private static final Color ERR_COLOR = Color.YELLOW;
    
    public static final void main(String... args) {
        textIO.getTextTerminal().getProperties().setPromptColor(KodeIO.OUT_COLOR);
        textIO.getTextTerminal().getProperties().setInputColor(KodeIO.IN_COLOR);
        textIO.getTextTerminal().getProperties().setPromptBold(false);
        textIO.getTextTerminal().getProperties().setInputBold(false);
        textIO.getTextTerminal().getProperties().setPromptItalic(false);
        textIO.getTextTerminal().getProperties().setInputItalic(false);
        textIO.getTextTerminal().getProperties().put("pane.title", Kode.getVersion());
        
        textIO.getTextTerminal().registerUserInterruptHandler((e) -> {
            exit(0);
        }, true);
        
        Kode.start_console(args);
        exit(0);
    }

    public static void printf(Object obj) {
        textIO.getTextTerminal().print(obj.toString());
    }

    public final static void printfln(Object obj) {
        printf(obj);
        printf(System.lineSeparator());
    }

    public static void printf_err(Object obj) {
        textIO.getTextTerminal().executeWithPropertiesConfigurator(
                props -> props.setPromptColor(KodeIO.ERR_COLOR),
                term -> term.print(obj.toString()));
    }

    public final static void printfln_err(Object obj) {
        printf_err(obj);
        printf_err(System.lineSeparator());
    }

    public static String scanf() {
        return scanf("");
    }

    public static String scanf(Object obj) {
        return textIO.newStringInputReader()
                .withMinLength(0)
                .read(obj.toString());
    }

    public static String scanf_pwd() {
        return scanf_pwd("");
    }

    public static String scanf_pwd(Object obj) {
        return textIO.newStringInputReader()
                .withInputMasking(true)
                .withMinLength(0)
                .read(obj.toString());
    }
    
    public static boolean resetLine() {
        return textIO.getTextTerminal().resetLine();
    }

    //
    private static Interpreter inter = new Interpreter();

    public static final void reset() {
        inter = new Interpreter();
    }

    public static final Object eval(String cmd) throws Exception {
        return eval(cmd, "<eval>");
    }

    public static final Object eval(String cmd, String fn) throws Exception {
        return inter.toJava(Kode.run(fn, cmd, inter));
    }

    public static final void bind(Map<String, Object> var_list) {
        var_list.entrySet().forEach((item) -> {
            inter.globals.define(item.getKey(), inter.toKodeValue(item.getValue()));
        });
    }
    //

    public static final void free() {
        System.gc();
    }
    
    public static final void exit(int status){
        textIO.getTextTerminal().dispose();
        System.exit(status);
    }

}
