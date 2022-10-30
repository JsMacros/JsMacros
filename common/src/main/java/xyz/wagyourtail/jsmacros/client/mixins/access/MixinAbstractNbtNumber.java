package xyz.wagyourtail.jsmacros.client.mixins.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = {"net.minecraft.nbt.AbstractNbtNumber"})
public interface MixinAbstractNbtNumber {

    @Invoker("byteValue")
    byte jsmacros_getByte();

    @Invoker("shortValue")
    short jsmacros_getShort();

    @Invoker("intValue")
    int jsmacros_getInt();

    @Invoker("longValue")
    long jsmacros_getLong();

    @Invoker("floatValue")
    float jsmacros_getFloat();

    @Invoker("doubleValue")
    double jsmacros_getDouble();
}
