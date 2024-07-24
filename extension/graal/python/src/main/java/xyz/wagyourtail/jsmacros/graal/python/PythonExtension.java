package xyz.wagyourtail.jsmacros.graal.python;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.graal.GraalConfig;

public class PythonExtension implements Extension {

    @Override
    public String getExtensionName() {
        return "graalpy";
    }

    @Override
    public void init(Core<?, ?> runner) {
        Thread t = new Thread(() -> {
            Context.Builder build = Context.newBuilder("python");
            Context con = build.build();
            con.eval("python", "print('py pre-loaded.')");
            con.close();
        });
        t.start();
        try {
            runner.config.addOptions("python", GraalConfig.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
