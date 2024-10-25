package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import xyz.wagyourtail.jsmacros.api.helper.ModContainerHelper;

import java.util.List;
import java.util.stream.Collectors;

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
        switch (metadata.getEnvironment()) {
            case CLIENT:
                return "CLIENT";
            case SERVER:
                return "SERVER";
            case UNIVERSAL:
                return "BOTH";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public List<String> getAuthors() {
        return metadata.getAuthors().stream().map(Person::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getDependencies() {
        return metadata.getDependencies().stream().map(ModDependency::getModId).collect(Collectors.toList());
    }

}
