package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(Entity.class)
public interface MixinEntity2 {

    // Don't make this static, it will disable the compile and reload feature!
    @Accessor("CUSTOM_NAME")
    TrackedData<Optional<Text>> getCustomNameKey();

}
