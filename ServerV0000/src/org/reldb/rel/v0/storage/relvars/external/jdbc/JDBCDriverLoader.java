package org.reldb.rel.v0.storage.relvars.external.jdbc;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Based on https://stevenatkinson.wordpress.com/2011/07/15/dynamic-url-class-loaders-a-simple-use-case/

public class JDBCDriverLoader extends URLClassLoader {
    public JDBCDriverLoader(File file, ClassLoader parent) throws MalformedURLException {
        super(makeUrls(file), parent);
    }
 
    private static URL[] makeUrls(File file) throws MalformedURLException {
        List<URL> urls = new ArrayList<URL>();
        urls.add(file.toURI().toURL());
        File[] jarFilesAndDirs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return dir.isDirectory() || name.endsWith(".jar");
            }
        });
        if (jarFilesAndDirs != null) {
            for (File jarOrDir : jarFilesAndDirs) {
                if (jarOrDir.isDirectory()) {
                    urls.addAll(Arrays.asList(makeUrls(jarOrDir)));
                } else {
                    urls.add(jarOrDir.toURI().toURL());
                }
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }
 
    public JDBCDriverLoader(File file) throws MalformedURLException {
        super(makeUrls(file));
    }

    public JDBCDriverLoader(File file, ClassLoader parent, URLStreamHandlerFactory factory) throws MalformedURLException {
        super(makeUrls(file), parent, factory);
    }
}
