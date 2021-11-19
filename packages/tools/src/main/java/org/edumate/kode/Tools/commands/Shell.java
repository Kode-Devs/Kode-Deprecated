package org.edumate.kode.Tools.commands;


import picocli.CommandLine.*;

import javax.script.*;
import java.util.Scanner;

@Command(name = "shell", description = "Open A New Kode Shell")
public class Shell implements Runnable {

    @Override
    public void run() {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("kode");
            Scanner scanner = new Scanner(System.in);

            System.out.println(engine.getFactory().getLanguageName() + " " + engine.getFactory().getLanguageVersion());

            while (true) {
                System.out.print(">>> ");
                final String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) System.exit(0);

                Object statusCode = engine.eval(input);
                System.out.println("Status: " + statusCode);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
