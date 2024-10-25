package xyz.wagyourtail.jsmacros.client.api.event.filterer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventBlockUpdate;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class FiltererBlockUpdate implements EventFilterer {
    @Nullable
    public BlockPosHelper pos;
    /**
     * if this and pos are not null, filters area<br>
     * this should always be larger or equal than pos
     */
    @Nullable
    public BlockPosHelper pos2;
    @Nullable
    @DocletReplaceReturn("BlockId | null")
    public String blockId;
    @Nullable
    public Map<String, String> blockState;
    @Nullable
    @DocletReplaceReturn("BlockUpdateType | null")
    public String updateType;

    @Override
    public boolean canFilter(String event) {
        return "BlockUpdate".equals(event);
    }

    @Override
    public boolean test(BaseEvent baseEvent) {
        if (!(baseEvent instanceof EventBlockUpdate event)) return false;
        if (updateType != null && !updateType.equals(event.updateType)) return false;
        if (blockId != null && !blockId.equals(event.block.getId())) return false;
        if (pos != null) {
            if (pos2 == null) {
                if (!pos.equals(event.block.getBlockPos())) return false;
            } else {
                BlockPosHelper bp = event.block.getBlockPos();
                if (
                        !(bp.getX() >= pos.getX() && bp.getX() <= pos2.getX()) ||
                        !(bp.getY() >= pos.getY() && bp.getY() <= pos2.getY()) ||
                        !(bp.getZ() >= pos.getZ() && bp.getZ() <= pos2.getZ())
                ) return false;
            }
        }
        if (blockState != null) {
            Map<String, String> states = event.block.getBlockState();
            for (var ent : blockState.entrySet()) {
                boolean contains = states.containsKey(ent.getKey());
                if (ent.getValue() == null) {
                    if (contains) return false;
                } else if (!contains || !Objects.equals(states.get(ent.getKey()), ent.getValue())) return false;
            }
        }
        return true;
    }

    public FiltererBlockUpdate setPos(int x, int y, int z) {
        return setPos(new BlockPosHelper(x, y, z));
    }

    public FiltererBlockUpdate setPos(@Nullable BlockPosHelper pos) {
        this.pos = pos;
        pos2 = null;
        return this;
    }

    public FiltererBlockUpdate setArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        return setArea(new BlockPosHelper(x1, y1, z1), new BlockPosHelper(x2, y2, z2));
    }

    public FiltererBlockUpdate setArea(@NotNull BlockPosHelper pos1, @NotNull BlockPosHelper pos2) {
        if (pos1.getX() > pos2.getX() || pos1.getY() > pos2.getY() || pos1.getZ() > pos2.getZ()) {
            return setArea(
                    Math.min(pos1.getX(), pos2.getX()),
                    Math.min(pos1.getY(), pos2.getY()),
                    Math.min(pos1.getZ(), pos2.getZ()),
                    Math.max(pos1.getX(), pos2.getX()),
                    Math.max(pos1.getY(), pos2.getY()),
                    Math.max(pos1.getZ(), pos2.getZ())
            );
        }
        this.pos = pos1;
        this.pos2 = pos2;
        return this;
    }

    @DocletReplaceParams("id: BlockId")
    public FiltererBlockUpdate setBlockId(@Nullable String id) {
        blockId = id == null ? null : RegistryHelper.parseNameSpace(id);
        return this;
    }

    @DocletReplaceParams("type: BlockUpdateType")
    public FiltererBlockUpdate setUpdateType(@Nullable String type) {
        updateType = type;
        return this;
    }

    public FiltererBlockUpdate setBlockStates(@Nullable Map<String, String> states) {
        blockState = states;
        return this;
    }

    /**
     * @param value setting to null will make sure the block doesn't have this property
     */
    public FiltererBlockUpdate setBlockState(String property, @Nullable String value) {
        if (blockState == null) blockState = new HashMap<>();
        blockState.put(property, value);
        return this;
    }

}
