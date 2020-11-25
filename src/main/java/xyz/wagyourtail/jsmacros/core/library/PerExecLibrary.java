package xyz.wagyourtail.jsmacros.core.library;

public abstract class PerExecLibrary extends BaseLibrary {
    protected Object context;
    protected Thread thread;
    public PerExecLibrary(Object context, Thread thread) {
        this.context = context;
        this.thread = thread;
    }
}
