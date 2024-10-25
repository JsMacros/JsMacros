package xyz.wagyourtail.jsmacros.client.api.event.impl.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.doclet.DocletDeclareType;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.api.event.filterer.FiltererBlockUpdate;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockDataHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
@Event(value = "BlockUpdate", oldName = "BLOCK_UPDATE", filterer = FiltererBlockUpdate.class)
public class EventBlockUpdate extends BaseEvent {
    public final BlockDataHelper block;
    @DocletReplaceReturn("BlockUpdateType")
    @DocletDeclareType(name = "BlockUpdateType", type = "'STATE' | 'ENTITY'")
    public final String updateType;

    public EventBlockUpdate(BlockState block, BlockEntity blockEntity, BlockPos blockPos, String updateType) {
        this.block = new BlockDataHelper(block, blockEntity, blockPos);
        this.updateType = updateType;
    }

    @Override
    public String toString() {
        return String.format("%s:{\"block\": %s}", this.getEventName(), block);
    }

}
