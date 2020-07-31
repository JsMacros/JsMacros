package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.List;

public abstract class Functions {
    public final String libName;
    public final List<String> excludeLanguages = new ArrayList<>();
    
    public Functions(String libName) {
        this(libName, null);
    }
    
    public Functions(String libName, List<String> excludeLanguages) {
        this.libName = libName;
        if (excludeLanguages != null) this.excludeLanguages.addAll(excludeLanguages);
    }
}
