package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockHelper extends BaseHelper<Block> {

    public BlockHelper(Block base) {
        super(base);
    }
    
    public BlockStateHelper getDefaultState() {
        return new BlockStateHelper(base.getDefaultState());
    }
    
    public ItemStackHelper getDefaultItemStack() {
        return new ItemStackHelper(base.asItem().getDefaultStack());
    }
    
    public boolean canMobSpawnInside() {
        return base.canMobSpawnInside();
    }

    public boolean hasDynamicBounds() {
        return base.hasDynamicBounds();
    }
    
    public float getBlastResistance() {
        return base.getBlastResistance();
    }

    public float getJumpVelocityMultiplier() {
        return base.getJumpVelocityMultiplier();
    }

    public float getSlipperiness() {
        return base.getSlipperiness();
    }
    
    public float getHardness() {
        return base.getHardness();
    }
    
    public float getVelocityMultiplier() {
        return base.getVelocityMultiplier();
    }
    
    public List<String> getTags() {
        return MinecraftClient.getInstance().getNetworkHandler().getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY).getTagsFor(base).stream().map(Identifier::toString).toList();
    }
    
    public List<BlockStateHelper> getStates() {
        return base.getStateManager().getStates().stream().map(BlockStateHelper::new).toList();
    }
    
    public String getId() {
        return Registry.BLOCK.getId(base).toString();
    }

    @Override
    public String toString() {
        return String.format("BlockDataHelper:{%s}", getId());
    }
    
}
