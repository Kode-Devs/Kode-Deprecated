/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 *
 * @author dell
 */
public class Pip4kode {

    public final long latestRevision;
    public long size;
    public String sizeInWords;
    private SVNUpdateClient updateClient;
    public final String repositoryRoot;
    public final String repositoryUUID;
    public final String pkg;
    private final SVNRepository repository;
    private static final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();

    public Pip4kode(String pkg) throws Exception {
        this.pkg = pkg;
        final String url = "https://github.com/Kode-Devs/package-" + pkg + "/trunk/";
        // Initi the repo from url
        repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
        // create auth data
        repository.setAuthenticationManager(authManager);
        // verify connection
        repositoryRoot = repository.getRepositoryRoot(true).toString();
        repositoryUUID = repository.getRepositoryUUID(true);
        // identify latest rev
        latestRevision = repository.getLatestRevision();
        if (repository.info("", latestRevision).getKind() != SVNNodeKind.DIR) {
            throw new Exception(pkg + " is not a directory");
        }
    }

    public void init() throws Exception {
        size = calculateSize(repository, latestRevision, "");
        sizeInWords = BytesToString(size);
        SVNClientManager ourClientManager = SVNClientManager.newInstance();
        ourClientManager.setAuthenticationManager(authManager);
        updateClient = ourClientManager.getUpdateClient();
        updateClient.setIgnoreExternals(true);
    }

    public void download(String desPath) throws Exception {
        updateClient.doExport(repository.getLocation(), new File(desPath),
                SVNRevision.create(latestRevision), SVNRevision.create(latestRevision),
                null, true, SVNDepth.INFINITY);
    }

    private static String BytesToString(long byteCount) {
        String[] suf = new String[]{"Byte", "KB", "MB", "GB", "TB", "PB", "EB"};
        Double len = Double.valueOf(byteCount);
        int order = 0;
        while (len >= 1024 && order < suf.length) {
            order++;
            len /= 1024;
        }
        return (len.toString().endsWith(".0")
                ? len.toString().substring(0, len.toString().length() - 2)
                : String.format("%.2f", len))
                + " " + suf[order];
    }

    private static long calculateSize(SVNRepository repository, long rev, String path) throws Exception {
        long size = 0;
        Collection entries = repository.getDir(path, rev, null, (Collection) null);
        Iterator<SVNDirEntry> iterator = entries.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = iterator.next();
            if (entry.getKind() == SVNNodeKind.DIR) {
                size += calculateSize(repository, rev, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
            } else {
                size += entry.getSize();
            }
        }
        return size;
    }

    public static boolean checkUpdate(String pkg, String local) {
        final String localVersion, remoteVersion;

        try {
            FileReader fr = new FileReader(Paths.get(local, "version").toString());
            StringWriter out = new StringWriter();
            fr.transferTo(out);
            localVersion = out.toString();
        } catch (Exception e) {
            return Paths.get(local).toFile().exists();
        }

        try {
            final String url = "https://github.com/Kode-Devs/package-" + pkg + "/trunk/";
            SVNRepository repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
            repository.setAuthenticationManager(authManager);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            repository.getFile("version.json", repository.getLatestRevision(), null, out);
            remoteVersion = out.toString();
        } catch (Exception e) {
            return false;
        }

        return !localVersion.contentEquals(remoteVersion);
    }
}
