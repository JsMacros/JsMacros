package xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.specialized.display;

import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class TextDisplayEntityHelper extends DisplayEntityHelper<DisplayEntity.TextDisplayEntity> {

    public TextDisplayEntityHelper(DisplayEntity.TextDisplayEntity base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    @Nullable
    public TextHelper getData() {
        return TextHelper.wrap(base.getText());
    }

}
