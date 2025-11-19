package xyz.wagyourtail.jsmacros.core.library.impl.classes;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.extensions.Extension;
import xyz.wagyourtail.jsmacros.core.extensions.LanguageExtension;
import xyz.wagyourtail.jsmacros.core.language.BaseLanguage;
import xyz.wagyourtail.jsmacros.core.language.BaseScriptContext;
import xyz.wagyourtail.jsmacros.core.library.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.6.5
 */
public class LibraryBuilder extends ClassBuilder<BaseLibrary> {
    final Core<?, ?> runner;
    final boolean languages;
    final boolean perExec;
    boolean hasConstructorSet = false;

    public LibraryBuilder(Core<?, ?> runner, String name, boolean perExec, String... allowedLangs) throws NotFoundException, CannotCompileException {
        super(name, (Class<BaseLibrary>) (perExec ? (allowedLangs.length > 0 ? PerExecLanguageLibrary.class : PerExecLibrary.class) : (allowedLangs.length > 0 ?
                PerLanguageLibrary.class : BaseLibrary.class)));
        this.runner = runner;
        AnnotationBuilder b = this.addAnnotation(Library.class).putString("value", name);
        List<Class<?>> allowed = new ArrayList<>();
        for (int i = 0; i < allowedLangs.length; i++) {
            Extension ext = runner.extensions.getExtensionForName(allowedLangs[i]);
            if (ext instanceof LanguageExtension l) {
                allowed.add(l.getLanguage(runner).getClass());
            } else {
                throw new IllegalArgumentException("Language not found: " + allowedLangs[i]);
            }
        }
        AnnotationBuilder.AnnotationArrayBuilder ab = b.putArray("allowedLanguages");
        for (Class<?> c : allowed) {
            ab.putClass(c);
        }
        ab.finish();
        b.finish();
        this.perExec = perExec;
        languages = allowedLangs.length > 0;
    }

    /**
     * constructor, if perExec run every context, if per language run once for each lang;
     * params are context and language class.
     * if not per exec, param will be skipped.
     * ie:
     * BaseLibrary: no params
     * PerExecLibrary: context
     * PerExecLanguageLibrary: context, language
     * PerLanguageLibrary: language
     * <p>
     * Don't do other constructors...
     *
     * @return
     * @throws NotFoundException
     */
    public ConstructorBuilder addConstructor() throws NotFoundException {
        hasConstructorSet = true;
        List<Class<?>> params = new ArrayList<>();
        if (perExec) {
            params.add(BaseScriptContext.class);
        }
        if (languages) {
            params.add(BaseLanguage.class);
        }
        ConstructorBuilder cb = addConstructor(params.toArray(new Class<?>[0]));
        cb.makePublic();
        return cb;
    }

    @Override
    public Class<? extends BaseLibrary> finishBuildAndFreeze() throws CannotCompileException, NotFoundException {
        if (!hasConstructorSet) {
            ConstructorBuilder cb = addConstructor();
            StringBuilder body = new StringBuilder("{super(");
            for (int i = 0; i < cb.params.length; i++) {
                if (i > 0) {
                    body.append(", ");
                }
                body.append("$").append(i);
            }
            body.append(");}");
            cb.body(body.toString());
        }
        Class<? extends BaseLibrary> clazz = super.finishBuildAndFreeze();
        runner.libraryRegistry.addLibrary(clazz);
        return clazz;
    }

}
