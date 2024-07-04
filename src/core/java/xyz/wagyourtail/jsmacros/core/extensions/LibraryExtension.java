package xyz.wagyourtail.jsmacros.core.extensions;

import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;

import java.util.Set;

public interface LibraryExtension extends Extension {

    Set<Class<? extends BaseLibrary>> getLibraries();

}
