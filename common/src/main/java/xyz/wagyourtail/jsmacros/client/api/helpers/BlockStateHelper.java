package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Etheradon
 * @since 1.6.5
 */
public class BlockStateHelper extends BaseHelper<BlockState> {

    public BlockStateHelper(BlockState base) {
        super(base);
    }

    public Map<String, String> toMap() {
        return base.getEntries().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().getName(),
                entry -> Util.getValueAsString(entry.getKey(), entry.getValue())
        ));
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

    public boolean isInstaMineable() {
        return getHardness() <= 0;
    }
    
    public boolean blocksLight() {
        return base.getMaterial().blocksLight();
    }

    public boolean blocksMovement() {
        return base.getMaterial().blocksMovement();
    }

    public boolean isBurnable() {
        return base.getMaterial().isBurnable();
    }

    public boolean isLiquid() {
        return base.getMaterial().isLiquid();
    }

    public boolean isSolid() {
        return base.getMaterial().isSolid();
    }

    public boolean isReplaceable() {
        return base.getMaterial().isReplaceable();
    }

    public boolean allowsSpawning(BlockPosHelper pos, String entity) {
        return base.allowsSpawning(MinecraftClient.getInstance().world, pos.getRaw(), Registry.ENTITY_TYPE.get(new Identifier(entity)));
    }

    public boolean shouldSuffocate(BlockPosHelper pos) {
        return base.shouldSuffocate(MinecraftClient.getInstance().world, pos.getRaw());
    }

    public boolean canPathfindThrough(BlockPosHelper pos, String navigationType) {
        return base.canPathfindThrough(MinecraftClient.getInstance().world, pos.getRaw(), getNavigationType(navigationType));
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
        return String.format("BlockStateHelper:{%s, %s}", getBlock().getId(), toMap());
    }

}
