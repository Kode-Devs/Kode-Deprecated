package tools.commands;

import picocli.CommandLine.*;

@Command(name = "upgrade", description = "Upgrade your copy of Kode.")
public class Upgrade implements Runnable {

    @Option(names = {"-f", "--force"},
            description = "Force upgrade the branch, potentially discarding local changes.")
    private boolean forceFlag;

    @Option(names = "--continue", hidden = true,
            description = "Trigger the second half of the upgrade flow. This should not be invoked " +
                    "manually. It is used re-entrantly by the standard upgrade command after " +
                    "the new version is available, to hand off the upgrade process " +
                    "from the old version to the new version.")
    private boolean continueFlag;

    @Option(names = "--verify-only",
            description = "Checks for any new updates, without actually fetching them.")
    private boolean verifyOnlyFlag;

    public Upgrade() {
    }

    @Override
    public void run() {

    }
}
