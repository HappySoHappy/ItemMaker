package me.howandev.itemmaker.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {
    public static Set<File> listFileTree(File dir) {
        Set<File> fileTree = new HashSet<>();
        if (dir == null || dir.listFiles() == null)
            return fileTree;

        for (File entry : dir.listFiles()) {
            if (entry.isFile()) fileTree.add(entry);
            else fileTree.addAll(listFileTree(entry));
        }

        return fileTree;
    }
}
