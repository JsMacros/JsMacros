package xyz.wagyourtail.jsmacros.core.extensions;

import java.net.URL;
import java.net.URLClassLoader;

public class ExtensionClassLoader extends URLClassLoader {

    public ExtensionClassLoader(URL[] urls) {
        super(urls, ExtensionClassLoader.class.getClassLoader());
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

}
