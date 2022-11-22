package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.ObjectIntIdentityMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IObjectIntIdentityMap;

import java.util.List;

@Mixin(ObjectIntIdentityMap.class)
public class MixinObjectIntIdentityMap implements IObjectIntIdentityMap {

    @Final
    @Shadow
    protected List<?> list;

    @Override
    public int jsmacros_getSize() {
        return list.size();
    }

}
