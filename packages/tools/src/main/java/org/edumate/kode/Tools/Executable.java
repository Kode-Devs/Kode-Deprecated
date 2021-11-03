package org.edumate.kode.Tools;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import org.edumate.kode.Tools.commands.Shell;
import org.edumate.kode.Tools.commands.Upgrade;
import org.edumate.kode.Tools.commands.Version;

import java.util.ArrayList;

@Command(
        name = "kode",
        versionProvider = Version.class,
        scope = ScopeType.INHERIT,
        usageHelpAutoWidth = true,
        abbreviateSynopsis = true,
        synopsisSubcommandLabel = "<command>",
        descriptionHeading = "%nDescription:%n",
        parameterListHeading = "%nParameters:%n",
        optionListHeading = "%nOptions:%n",
        commandListHeading = "%nCommands:%n")
public class Executable implements Runnable {

    @Spec
    CommandSpec spec;

    @Option(names = {"-v", "--verbose"}, scope = ScopeType.INHERIT,
            description = "Verbose Logging")
    boolean[] verbose;

    @Option(names = {"-h", "--help"}, usageHelp = true, scope = ScopeType.INHERIT,
            description = "display this help and exit")
    private boolean _helpRequested;

    @Option(names = {"-V", "--version"}, versionHelp = true, scope = ScopeType.INHERIT,
            description = "print version information and exit")
    private boolean _versionRequested;


    @Override
    public void run() {
        // Subcommand is Required
        throw new ParameterException(spec.commandLine(), "Missing required subcommand");
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        var commandLine = new CommandLine(new Executable());

        // add subcommands programmatically (not necessary if the parent command
        // declarative registers the subcommands via annotation)
        commandLine.addSubcommand(new Shell());
        commandLine.addSubcommand(new Upgrade());

        // Check for verbose and help
        try {
            var parseResult = commandLine.parseArgs(args);

            boolean verbose = false, helpRequested = false;
            while (true) {
                verbose = verbose || parseResult.hasMatchedOption("verbose");
                helpRequested = helpRequested || parseResult.isUsageHelpRequested();

                if (parseResult.hasSubcommand()) {
                    parseResult = parseResult.subcommand();
                } else {
                    break;
                }
            }

            if (verbose && helpRequested) {
                unHide(commandLine);
            }

            // Help
            if (helpRequested) {
                parseResult.commandSpec().commandLine().usage(System.out);
                return;
            }
        } catch (Exception ignore) {
        }

        // Execute Args
        int exitCode = commandLine.execute(args);

        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }

    private static void unHide(CommandLine commandLine) {
        CommandSpec spec = commandLine.getCommandSpec();
        for (var oldOption : new ArrayList<>(spec.options()))
            if (oldOption.hidden()) {
                var newOption = OptionSpec.builder(oldOption).hidden(false).build();

                // Replace old one with new one.
                spec.remove(oldOption);
                spec.add(newOption);
            }

        for (var oldParam : new ArrayList<>(spec.positionalParameters()))
            if (oldParam.hidden()) {
                var newParam = PositionalParamSpec.builder(oldParam).hidden(false).build();

                // Replace old one with new one.
                spec.remove(oldParam);
                spec.add(newParam);
            }

        for (CommandLine line : spec.subcommands().values()) unHide(line);
    }
}
