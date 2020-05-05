/*
 * MIT License
 *
 * Copyright (c) 2020 Edumate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.edumate.kode;

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
