package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
            return (IPalettedContainerData<T>) getField(dataField);
        }

        for (Field f : PalettedContainer.class.getDeclaredFields()) {
            if (IPalettedContainerData.class.isAssignableFrom(f.getType())) {
                dataField = f;
                return (IPalettedContainerData<T>) getField(f);
            }
        }
        throw new RuntimeException(Arrays.toString(PalettedContainer.class.getDeclaredFields()));
    }

    private Object getField(Field f) {
        try {
            return f.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException(Arrays.toString(PalettedContainer.class.getDeclaredFields()));
    }

}
