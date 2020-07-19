/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

/**
 *
 * @author dell
 */
public class IO {

    public static final TextIO TEXTIO;
    private static final String CLC = "clc";

    static {
        System.err.println("\n-------- " + new Date() + " --------");
        TEXTIO = TextIoFactory.getTextIO();
        TEXTIO.getTextTerminal().getProperties().setPromptColor(Color.WHITE);
        TEXTIO.getTextTerminal().getProperties().setInputColor(Color.CYAN);
        TEXTIO.getTextTerminal().getProperties().setPromptBold(false);
        TEXTIO.getTextTerminal().getProperties().setInputBold(false);
        TEXTIO.getTextTerminal().getProperties().setPromptItalic(false);
        TEXTIO.getTextTerminal().getProperties().setInputItalic(false);
        TEXTIO.getTextTerminal().getProperties().put("pane.title", Kode.getVersion() + " - " + Paths.get("").toAbsolutePath());

        TEXTIO.getTextTerminal().registerUserInterruptHandler((e) -> {
//            throw new RuntimeError("Keyboard Interrupt Found.");
            exit(0);
        }, true);

        // KeyBoard Interrupt
//        if (TEXTIO.getTextTerminal().registerHandler(AbstractTextTerminal.DEFAULT_USER_INTERRUPT_KEY, t -> {
//            throw new RuntimeError("Keyboard Interrupt Found.");
//        }) == false) {
//            JOptionPane.showMessageDialog(null, "Failed to Set KeyBoard Interrupt.", "Warning!!!", JOptionPane.WARNING_MESSAGE);
//        }
        TEXTIO.getTextTerminal().setBookmark(CLC);
    }

    public static void printf(Object obj) {
        TEXTIO.getTextTerminal().print(obj.toString());
    }

    public final static void printfln(Object obj) {
        printf(obj);
        printf(System.lineSeparator());
    }

    public static void printf_err(Object obj) {
        TEXTIO.getTextTerminal().executeWithPropertiesConfigurator(props -> props.setPromptColor(Color.YELLOW),
                term -> term.print(obj.toString()));
    }

    public final static void printfln_err(Object obj) {
        printf_err(obj);
        printf_err(System.lineSeparator());
    }

    public static String scanf() {
        return TEXTIO.newStringInputReader()
                .withMinLength(0)
                .read();
    }

    public static String scanf_pwd() {
        return TEXTIO.newStringInputReader()
                .withInputMasking(true)
                .withMinLength(0)
                .read();
    }

    public static boolean resetLine() {
        return TEXTIO.getTextTerminal().resetLine();
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
        TEXTIO.getTextTerminal().resetToBookmark(CLC);
    }

    public static final void exit(int status) {
        TEXTIO.getTextTerminal().dispose();
        System.exit(status);
    }

}
