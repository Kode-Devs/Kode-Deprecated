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
package utils;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Performs IO operation i.e., Read or Write to the Console
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
public abstract class IO {

    static {
        AnsiConsole.systemInstall();
    }

    private static final boolean CONSOLE_LESS = System.console() == null;
    private static final Console SYS_CONSOLE = System.console();
    private static final BufferedReader ALT_CONSOLE = new BufferedReader(new InputStreamReader(System.in));

    public static void printf(Object obj) {
        System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.valueOf(obj)).reset());
    }

    public static void printfln(Object obj) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a(String.valueOf(obj)).reset());
    }

    public static void printf_err(Object obj) {
        System.out.print(Ansi.ansi().fg(Ansi.Color.YELLOW).a(String.valueOf(obj)).reset());
    }

    public static void printfln_err(Object obj) {
        System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(String.valueOf(obj)).reset());
    }

    public static String scanf() throws IOException {
        System.out.print(Ansi.ansi().fgCyan());
        String res = CONSOLE_LESS ? ALT_CONSOLE.readLine() : SYS_CONSOLE.readLine();
        System.out.print(Ansi.ansi().reset());
        if (res == null) exit(1);
        return res;
    }

    public static char[] scanf_pwd() throws IOException {
        return CONSOLE_LESS ? ALT_CONSOLE.readLine().toCharArray() : SYS_CONSOLE.readPassword();
    }

    public static void resetLine() {
        System.out.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).cursorToColumn(0));
    }

    public static void clc() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Throwable e1) {
            try {
                Runtime.getRuntime().exec("clear");
            } catch (Throwable e2) {
                System.out.print(Ansi.ansi().eraseScreen(Ansi.Erase.ALL).cursor(0, 0));
            }
        }
    }

    public static void exit(int status) {
        System.exit(status);
    }

}
