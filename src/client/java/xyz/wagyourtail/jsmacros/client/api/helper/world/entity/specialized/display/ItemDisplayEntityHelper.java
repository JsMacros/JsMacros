package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display;

import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class ItemDisplayEntityHelper extends DisplayEntityHelper<DisplayEntity.ItemDisplayEntity> {

    public ItemDisplayEntityHelper(DisplayEntity.ItemDisplayEntity base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    public ItemStackHelper getItem() {
        return new ItemStackHelper(base.getStackReference(0).get());
    }

    /**
     * @return "none", "thirdperson_lefthand", "thirdperson_righthand", "firstperson_lefthand",
     *         "firstperson_righthand", "head", "gui", "ground" or "fixed"
     * @since 1.9.1
     */
    @SuppressWarnings("SpellCheckingInspection")
    @Nullable
    public String getTransform() {
        DisplayEntity.ItemDisplayEntity.Data data = base.getData();
        if (data == null) return null;
        return data.itemTransform().asString();
    }

}
