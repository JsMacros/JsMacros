package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import xyz.wagyourtail.jsmacros.client.api.helpers.ModContainerHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class FabricModContainer extends ModContainerHelper<ModContainer> {

    private final ModMetadata metadata;

    public FabricModContainer(ModContainer base) {
        super(base);
        metadata = base.getMetadata();
        metadata.getDependencies();
    }

    @Override
    public String getId() {
        return metadata.getId();
    }

    @Override
    public String getName() {
        return metadata.getName();
    }

    @Override
    public String getDescription() {
        return metadata.getDescription();
    }

    @Override
    public String getVersion() {
        return metadata.getVersion().getFriendlyString();
    }

    @Override
    public String getEnv() {
        return switch (metadata.getEnvironment()) {
            case CLIENT -> "CLIENT";
            case SERVER -> "SERVER";
            case UNIVERSAL -> "BOTH";
        };
    }

    @Override
    public List<String> getAuthors() {
        return metadata.getAuthors().stream().map(Person::getName).toList();
    }

    @Override
    public List<String> getDependencies() {
        return metadata.getDependencies().stream().map(ModDependency::getModId).toList();
    }

}
