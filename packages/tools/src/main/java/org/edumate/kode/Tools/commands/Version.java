package org.edumate.kode.Tools.commands;

import picocli.CommandLine;

public class Version implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() {
        return new String[]{"1.5.0"};
    }
}
