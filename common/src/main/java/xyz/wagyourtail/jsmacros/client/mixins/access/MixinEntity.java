package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.wagyourtail.jsmacros.client.access.IEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow protected abstract HoverEvent getHoverEvent();

    @Override
    public HoverEvent jsmacros_getHoverEvent() {
        return getHoverEvent();
    }

}
