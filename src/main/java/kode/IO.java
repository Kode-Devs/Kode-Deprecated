/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 *
 * @author dell
 */
public abstract class IO {

    static {
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        }));
        System.err.println("\n-------- " + new Date() + " --------");
    }

    private static final boolean HAS_CONSOLE = System.console() == null;
    private static final Console SYS_CONSOLE = System.console();
    private static final BufferedReader ALT_CONSOLE = new BufferedReader(new InputStreamReader(System.in));

    public static void printf(Object obj) {
        AnsiConsole.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.valueOf(obj)).reset());
    }

    public final static void printfln(Object obj) {
        AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.valueOf(obj)).reset());
    }

    public static void printf_err(Object obj) {
        AnsiConsole.out.print(Ansi.ansi().fg(Ansi.Color.YELLOW).a(String.valueOf(obj)).reset());
    }

    public final static void printfln_err(Object obj) {
        AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(String.valueOf(obj)).reset());
    }

    public static String scanf() throws IOException {
        return HAS_CONSOLE ? ALT_CONSOLE.readLine() : SYS_CONSOLE.readLine();
    }

    public static char[] scanf_pwd() throws IOException {
        return HAS_CONSOLE ? ALT_CONSOLE.readLine().toCharArray() : SYS_CONSOLE.readPassword();
    }

    public static void resetLine() {
        AnsiConsole.out.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).cursorToColumn(0));
    }

    public static void clc() {
        try {
            new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
        } catch (Throwable e1) {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (Throwable e2) {
                AnsiConsole.out.print(Ansi.ansi().eraseScreen(Ansi.Erase.ALL).cursor(0, 0));
            }
        }
    }

    public static final void exit(int status) {
        System.exit(status);
    }

}
