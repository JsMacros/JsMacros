package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import net.neoforged.neoforgespi.language.IModInfo;
import xyz.wagyourtail.jsmacros.api.helper.ModContainerHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class ForgeModContainer extends ModContainerHelper<IModInfo> {

    public ForgeModContainer(IModInfo base) {
        super(base);
    }

    @Override
    public String getId() {
        return base.getModId();
    }

    @Override
    public String getName() {
        return base.getDisplayName();
    }

    @Override
    public String getDescription() {
        return base.getDescription();
    }

    @Override
    public String getVersion() {
        return base.getVersion().getQualifier();
    }

    @Override
    public String getEnv() {
        switch (base.getConfig().<String>getConfigElement("side").orElse("UNKNOWN")) {
            case "CLIENT":
                return "CLIENT";
            case "SERVER":
                return "SERVER";
            case "BOTH":
                return "BOTH";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    public List<String> getAuthors() {
        return null;
    }

    @Override
    public List<String> getDependencies() {
        return base.getDependencies().stream().map(IModInfo.ModVersion::getModId).collect(Collectors.toList());
    }

}