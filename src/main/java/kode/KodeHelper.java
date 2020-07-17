/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.awt.Color;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.beryx.textio.AbstractTextTerminal;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

/**
 *
 * @author dell
 */
public class KodeHelper {

    public static TextIO textIO = TextIoFactory.getTextIO();

    private static final Color OUT_COLOR = Color.WHITE;
    private static final Color IN_COLOR = Color.CYAN;
    private static final Color ERR_COLOR = Color.YELLOW;

    private static final String CLC = "clc";

    public static final void main(String... args) {
        textIO.getTextTerminal().getProperties().setPromptColor(KodeHelper.OUT_COLOR);
        textIO.getTextTerminal().getProperties().setInputColor(KodeHelper.IN_COLOR);
        textIO.getTextTerminal().getProperties().setPromptBold(false);
        textIO.getTextTerminal().getProperties().setInputBold(false);
        textIO.getTextTerminal().getProperties().setPromptItalic(false);
        textIO.getTextTerminal().getProperties().setInputItalic(false);
        textIO.getTextTerminal().getProperties().put("pane.title", Kode.getVersion());

        textIO.getTextTerminal().registerUserInterruptHandler((e) -> {
            exit(0);
        }, true);

        // KeyBoard Interrupt
        if (textIO.getTextTerminal().registerHandler(AbstractTextTerminal.DEFAULT_USER_INTERRUPT_KEY, t -> {
            throw new RuntimeError("Keyboard Interrupt Found.");
        }) == false) {
            JOptionPane.showMessageDialog(null, "Failed to Set KeyBoard Interrupt.", "Warning!!!", JOptionPane.WARNING_MESSAGE);
        }
        textIO.getTextTerminal().setBookmark(CLC);
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
        textIO.getTextTerminal().executeWithPropertiesConfigurator(props -> props.setPromptColor(KodeHelper.ERR_COLOR),
                term -> term.print(obj.toString()));
    }

    public final static void printfln_err(Object obj) {
        printf_err(obj);
        printf_err(System.lineSeparator());
    }

    public static String scanf(Object obj) {
        KodeHelper.printf(obj);
        return textIO.newStringInputReader()
                .withMinLength(0)
                .read();
    }

    public static String scanf_pwd(Object obj) {
        KodeHelper.printf(obj);
        return textIO.newStringInputReader()
                .withInputMasking(true)
                .withMinLength(0)
                .read();
    }

    public static boolean resetLine() {
        return textIO.getTextTerminal().resetLine();
    }

    public static void clc() {
        if (System.console() != null) {
            try {
                if (System.getProperty("os.name", "undefined").contains("Windows")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    Runtime.getRuntime().exec("clear");
                }
                return;
            } catch (IOException | InterruptedException e) {
            }
        }
        textIO.getTextTerminal().resetToBookmark(CLC);
    }

    public static final void exit(int status) {
        textIO.getTextTerminal().dispose();
        System.exit(status);
    }

}
