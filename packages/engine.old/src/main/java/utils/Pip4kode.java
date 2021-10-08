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

import java.io.File;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.fusesource.jansi.Ansi;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Pip4Kode downloads and handles all libraries from their online repositories
 *
 * @author Arpan Mahanty < edumate696@gmail.com >
 */
public class Pip4kode {

    public static class PipError extends Exception {

        private static final long serialVersionUID = 1L;

        public PipError(String message) {
            super(message);
        }
    }

    public String sizeInWords;
    public final String pkg;
    private CloneCommand call;

    public Pip4kode(String pkg) throws Exception {
        if (((HttpsURLConnection) new URL("https://www.github.com").openConnection()).getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new PipError("Internet Connection Unavailable."); // Checks weather Internet is accessible
        }
        this.pkg = pkg;
    }

    public void init(String desPath) throws Exception {
        URL url = new URL("https://github.com/Kode-Devs/package-" + pkg);
        if (((HttpsURLConnection) url.openConnection()).getResponseCode() != HttpsURLConnection.HTTP_OK) {
            throw new PipError("Package '" + this.pkg + "' not available in Repository."); // Checks validation of the package
        }
        sizeInWords = calculateSize(pkg);
        File file = new File(desPath);
        if (file.canWrite()) {
            int i = 0;
            while (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                i++;
                if (i == 5) {
                    throw new PipError("Can not clear the old local copy. Try manually removing it from '" + file.getAbsolutePath() + "'"); // Can not clear the old version
                }
            }
        }
        call = Git.cloneRepository()
                .setURI(url.toString())
                .setDirectory(file)
                .setBranch(Constants.HEAD)
                .setCloneSubmodules(true)
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(new OutputStreamWriter(System.out))));
    }

    public boolean download() {
        try {
            System.out.print(Ansi.ansi().fgBlue());
            call.call();
            System.out.print(Ansi.ansi().reset());
            return true;
        } catch (Throwable e) {
            System.out.print(Ansi.ansi().reset());
            return false;
        }
    }

    private String calculateSize(String pkg) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) (new URL("https://api.github.com/repos/Kode-Devs/package-" + pkg)).openConnection();
            con.setInstanceFollowRedirects(true);
            String string = new String(con.getInputStream().readAllBytes());
            int i = string.indexOf("size");
            return BytesToString(Long.parseUnsignedLong(string.substring(i + 6, string.indexOf(',', i))));
        } catch (Throwable e) {
            return "N/A";
        }
    }

    private String BytesToString(long byteCount) {
        String[] suf = new String[]{"KB", "MB", "GB", "TB", "PB", "EB"};
        double len = (double) byteCount;
        int order = 0;
        while (len >= 1024 && order < suf.length) {
            order++;
            len /= 1024;
        }
        return (Double.toString(len).endsWith(".0")
                ? Double.toString(len).substring(0, Double.toString(len).length() - 2)
                : String.format("%.2f", len))
                + " " + suf[order];
    }

    public static boolean checkUpdate(String pkg, String local) {
        final String localVersion, remoteVersion;

        try {
            FileReader fr = new FileReader(Paths.get(local, "version.json").toString());
            StringWriter out = new StringWriter();
            fr.transferTo(out);
            localVersion = readVersion(out.toString());
            fr.close();
            out.close();
        } catch (Throwable e) {
            return Paths.get(local).toFile().exists();
        }

        try {
            HttpsURLConnection con = (HttpsURLConnection) (new URL("https://raw.github.com/Kode-Devs/package-" + pkg + "/HEAD/version.json")).openConnection();
            con.setInstanceFollowRedirects(true);
            remoteVersion = readVersion(new String(con.getInputStream().readAllBytes()));
            con.disconnect();
        } catch (Throwable e) {
            return false;
        }

        return !localVersion.contentEquals(remoteVersion);
    }

    static String readVersion(String version) throws ParseException {
        Object parse = new JSONParser().parse(version);
        if (parse instanceof Map)
            //noinspection rawtypes
            return ((Map) parse).get("version").toString();
        else return "un-versioned";
    }
}
