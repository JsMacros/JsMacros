package xyz.wagyourtail.jsmacros.client.api.helpers;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public abstract class ModContainerHelper<T> extends BaseHelper<T> {

    protected ModContainerHelper(T base) {
        super(base);
    }

    /**
     * @return the mod id.
     *
     * @since 1.9.0
     */
    public abstract String getId();

    /**
     * @return the mod name.
     *
     * @since 1.9.0
     */
    public abstract String getName();

    /**
     * @return the mod description.
     *
     * @since 1.9.0
     */
    public abstract String getDescription();

    /**
     * @return the mod version.
     *
     * @since 1.9.0
     */
    public abstract String getVersion();

    /**
     * @return the environment the mod is intended for.
     *
     * @since 1.9.0
     */
    public abstract String getEnv();

    /**
     * @return a list of all authors.
     *
     * @since 1.9.0
     */
    public abstract List<String> getAuthors();

    /**
     * @return a list of all dependencies.
     *
     * @since 1.9.0
     */
    public abstract List<String> getDependencies();

}
