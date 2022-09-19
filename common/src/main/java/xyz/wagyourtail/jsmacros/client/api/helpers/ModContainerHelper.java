package xyz.wagyourtail.jsmacros.client.api.helpers;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
public abstract class ModContainerHelper<T> extends BaseHelper<T> {

    protected ModContainerHelper(T base) {
        super(base);
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getVersion();

    public abstract String getEnv();

    public abstract List<String> getAuthors();

    public abstract List<String> getDependencies();

}
