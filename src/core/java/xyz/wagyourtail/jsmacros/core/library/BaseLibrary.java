package xyz.wagyourtail.jsmacros.core.library;

import xyz.wagyourtail.jsmacros.core.Core;

public abstract class BaseLibrary {
    public Core<?, ?> runner;

    public BaseLibrary(Core<?, ?> runner) {
        this.runner = runner;
    }

}
