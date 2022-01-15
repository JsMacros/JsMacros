package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockStateHelper extends BaseHelper<BlockState> {

    public BlockStateHelper(BlockState base) {
        super(base);
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<Property<?>, Comparable<?>> e : base.getEntries().entrySet()) {
            map.put(e.getKey().getName(), Util.getValueAsString(e.getKey(), e.getValue()));
        }
        return map;
    }
    
    public BlockHelper getBlock() {
        return new BlockHelper(base.getBlock());
    }

    public float getHardness() {
        return base.getHardness(null, null);
    }
    
    public int getLuminance() {
        return base.getLuminance();
    }
    
    public boolean emitsRedstonePower() {
        return base.emitsRedstonePower();
    }
    
    public boolean exceedsCube() {
        return base.exceedsCube();
    }
    
    public boolean isAir() {
        return base.isAir();
    }
    
    public boolean isOpaque() {
        return base.isOpaque();
    }
    
    public boolean isToolRequired() {
        return base.isToolRequired();
    }
    
    public boolean hasBlockEntity() {
        return base.hasBlockEntity();
    }
    
    public boolean hasRandomTicks() {
        return base.hasRandomTicks();
    }
    
    public boolean hasComparatorOutput() {
        return base.hasComparatorOutput();
    }

    public String getPistonBehaviour() {
        System.out.println(this + " " + base.getPistonBehavior());
        switch (base.getPistonBehavior()) {
            case NORMAL:
                return "NORMAL";
            case BLOCK:
                return "BLOCK";
            case PUSH_ONLY:
                return "PUSH_ONLY";
            case DESTROY:
                return "DESTROY";
            case IGNORE:
                return "IGNORE";
            default:
                throw new IllegalStateException("Unexpected value: " + base.getPistonBehavior());
        }
    }
    
    public boolean allowsSpawning(BlockView world, BlockPosHelper pos, String entity) {
        return base.allowsSpawning(world, pos.getRaw(), Registry.ENTITY_TYPE.get(new Identifier(entity)));
    }

    public boolean shouldSuffocate(BlockView world, BlockPosHelper pos) {
        return base.shouldSuffocate(world, pos.getRaw());
    }

    public boolean canPathfindThrough(BlockView world, BlockPosHelper pos, String navigationType) {
        return base.canPathfindThrough(world, pos.getRaw(), getNavigationType(navigationType));
    }

    private static NavigationType getNavigationType(String navigationType) {
        switch (navigationType.toUpperCase(Locale.ROOT)) {
            case "LAND":
                return NavigationType.LAND;
            case "WATER":
                return NavigationType.WATER;
            case "AIR":
                return NavigationType.AIR;
            default:
                throw new IllegalStateException("Unexpected value: " + navigationType);
        }
    }

    @Override
    public String toString() {
        return String.format("BlockStateHelper:{%s, %s}", this.getBlock().getId(), this.toMap());
    }
    
}
