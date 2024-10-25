package xyz.wagyourtail.jsmacros.client.mixin.access;

import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainer;
import xyz.wagyourtail.jsmacros.client.access.IPalettedContainerData;

import java.lang.reflect.Field;
import java.util.Arrays;

@Mixin(PalettedContainer.class)
public class MixinPalettedContainer<T> implements IPalettedContainer<T> {

    @Unique
    private Field dataField;

    @Override
    public IPalettedContainerData<T> jsmacros_getData() {
        if (dataField != null) {
            return (IPalettedContainerData<T>) jsmacros$getField(dataField);
        }

        for (Field f : PalettedContainer.class.getDeclaredFields()) {
            if (IPalettedContainerData.class.isAssignableFrom(f.getType())) {
                dataField = f;
                return (IPalettedContainerData<T>) jsmacros$getField(f);
            }
        }
        throw new RuntimeException(Arrays.toString(PalettedContainer.class.getDeclaredFields()));
    }

    @Unique
    private Object jsmacros$getField(Field f) {
        try {
            return f.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(Arrays.toString(PalettedContainer.class.getDeclaredFields()));
    }

}