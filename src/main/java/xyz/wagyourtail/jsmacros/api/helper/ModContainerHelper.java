package xyz.wagyourtail.jsmacros.api.helper;

import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public abstract class ModContainerHelper<T> extends BaseHelper<T> {

    protected ModContainerHelper(T base) {
        super(base);
    }

    /**
     * @return the mod's id.
     * @since 1.8.4
     */
    public abstract String getId();

    /**
     * @return the mod's name.
     * @since 1.8.4
     */
    public abstract String getName();

    /**
     * @return the mod's description.
     * @since 1.8.4
     */
    public abstract String getDescription();

    /**
     * @return the mod's version.
     * @since 1.8.4
     */
    public abstract String getVersion();

    /**
     * @return the environment this mod is intended for.
     * @since 1.8.4
     */
    public abstract String getEnv();

    /**
     * @return a list of all authors.
     * @since 1.8.4
     */
    public abstract List<String> getAuthors();

    /**
     * @return a list of all dependencies.
     * @since 1.8.4
     */
    public abstract List<String> getDependencies();

    @Override
    public String toString() {
        return String.format("ModContainerHelper:{\"id\": \"%s\", \"name\": \"%s\", \"version\": \"%s\"}", getId(), getName(), getVersion());
    }

}
