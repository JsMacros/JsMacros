package xyz.wagyourtail.jsmacros.graal.js;

import org.graalvm.polyglot.Context;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.graal.GraalConfig;

public class JsExtension implements Extension {

    @Override
    public String getExtensionName() {
        return "graaljs";
    }

    @Override
    public void init(Core<?, ?> runner) {
        Thread t = new Thread(() -> {
            Context.Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js pre-loaded.')");
            con.close();
        });
        t.start();
        try {
            runner.config.addOptions("js", GraalConfig.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
