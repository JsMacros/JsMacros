package xyz.wagyourtail.jsmacros.client.api.event.filterer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventBlockUpdate;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.EventFilterer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EventBlockUpdateFilterer implements EventFilterer {
    @Nullable
    public BlockPosHelper pos;
    /**
     * if this and pos are not null, filters area<br>
     * this should always be larger or equal than pos
     */
    @Nullable
    public BlockPosHelper pos2;
    @Nullable
    @DocletReplaceReturn("BlockId")
    public String blockId;
    @Nullable
    public Map<String, String> blockState;
    @Nullable
    @DocletReplaceReturn("BlockUpdateType")
    public String updateType;

    @NotNull
    @Override
    public Class<? extends BaseEvent> dedicatedFor() {
        return EventBlockUpdate.class;
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
            if (!states.keySet().containsAll(blockState.keySet())) return false;
            for (String key : blockState.keySet()) {
                if (!Objects.equals(states.get(key), blockState.get(key))) return false;
            }
        }
        return true;
    }

    public EventBlockUpdateFilterer setPos(int x, int y, int z) {
        return setPos(new BlockPosHelper(x, y, z));
    }

    public EventBlockUpdateFilterer setPos(@Nullable BlockPosHelper pos) {
        this.pos = pos;
        pos2 = null;
        return this;
    }

    public EventBlockUpdateFilterer setArea(int x1, int y1, int z1, int x2, int y2, int z2) {
        return setArea(new BlockPosHelper(x1, y1, z1), new BlockPosHelper(x2, y2, z2));
    }

    public EventBlockUpdateFilterer setArea(@NotNull BlockPosHelper pos1, @NotNull BlockPosHelper pos2) {
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
    public EventBlockUpdateFilterer setBlockId(@Nullable String id) {
        blockId = id == null ? null : RegistryHelper.parseNameSpace(id);
        return this;
    }

    @DocletReplaceParams("type: BlockUpdateType")
    public EventBlockUpdateFilterer setUpdateType(@Nullable String type) {
        updateType = type;
        return this;
    }

    public EventBlockUpdateFilterer setBlockStates(@Nullable Map<String, String> states) {
        blockState = states;
        return this;
    }

    /**
     * @param value setting to null will make sure the block doesn't have this property
     */
    public EventBlockUpdateFilterer setBlockState(String property, @Nullable String value) {
        if (blockState == null) blockState = new HashMap<>();
        blockState.put(property, value);
        return this;
    }

}
