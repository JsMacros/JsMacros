package xyz.wagyourtail.jsmacros.api.events;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.jsmacros.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.api.sharedinterfaces.IEvent;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
public class EventBlockUpdate implements IEvent {
    public final BlockDataHelper block;
    public final String updateType;
    
    public EventBlockUpdate(BlockState block, BlockEntity blockEntity, BlockPos blockPos, String updateType) {
        this.block = new BlockDataHelper(block, blockEntity, blockPos);
        this.updateType = updateType;
        
        profile.triggerMacro(this);
    }
    
    public String toString() {
        return String.format("%s:{\"block\": %s}", this.getEventName(), block.toString());
    }
}
