package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ObjectUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.world.EventNameChange;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinEntity2;

import java.util.Optional;

@Mixin(DataTracker.class)
public class MixinDataTracker {

    @Shadow
    @Final
    private DataTracked trackedEntity;

    @Inject(method = "copyToFrom", at = @At("HEAD"), cancellable = true)
    public void onCopyToFrom(DataTracker.Entry<Optional<Text>> to, DataTracker.SerializedEntry<Optional<Text>> from, CallbackInfo ci) {
        if (trackedEntity instanceof Entity) {
            int id = to.getData().id();
            if (id != ((MixinEntity2) trackedEntity).getCustomNameKey().id() || id != from.id()) return;

            Text newName = from.value().orElse(null);
            Text oldName = to.get().orElse(null);
            if (ObjectUtils.notEqual(oldName, newName)) {
                EventNameChange event = new EventNameChange((Entity) trackedEntity, oldName, newName);
                event.trigger();
                if (event.isCanceled()) ci.cancel();
                else {
                    TextHelper res = event.newName;
                    Text modified = res == null ? null : res.getRaw();
                    if (ObjectUtils.notEqual(newName, modified)) {
                        ci.cancel();
                        to.set(Optional.ofNullable(modified));
                    }
                }
            }
        }
    }

}
