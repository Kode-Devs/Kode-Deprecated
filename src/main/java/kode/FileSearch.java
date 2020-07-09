/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author dell
 */
class FileSearch {

    private String name;
    public Path path = null;

    private FileSearch() {
    }

    FileSearch(String dir, String name) {
        File root = new File(dir);
        this.name = name;
        String p = search(root);
        if (p != null) {
            this.path = Paths.get(p);
        }
    }

//    private String search(File root) {
//        if (!root.exists()) {
//            return null;
//        }
//        try {
//            String[] split = this.name.split(File.separator);
//            if (root.isDirectory()) {
//                File[] listFiles = root.listFiles();
//                if(listFiles == null) return null;
//                for (File f : listFiles) {
//                    String res = this.search(f);
//                    if (res != null) {
//                        return res;
//                    }
//                }
//            } else if (split[split.length-1].equals(root.getName())) {
//                return root.getCanonicalPath();
//            }
//            return null;
//        } catch (IOException e) {
//            return null;
//        }
//    }
    private String search(File root) {
        if (!root.exists()) {
            return null;
        }
        String split = new File(this.name).getName();
        File[] listFiles = root.listFiles();
        if (listFiles == null) {
            return null;
        }
        for (File f : listFiles) {
            if (split.equals(f.getName())) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }

    boolean exists() {
        if (this.path == null) {
            return false;
        }
        return path.toAbsolutePath().toFile().exists();
    }
}
