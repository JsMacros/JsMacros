package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.classes.filter.api.IFilter;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockStateHelper;

public class WorldScannerBuilder {
    
    private IFilter<BlockHelper> blockFilter;
    private IFilter<BlockStateHelper> stateFilter;
    
    public WorldScanner build() {
        return new WorldScanner(MinecraftClient.getInstance().world, blockFilter, stateFilter);
    }
    
}
