package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display;

import net.minecraft.entity.decoration.DisplayEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
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
    public TextDisplayDataHelper getData() {
        DisplayEntity.TextDisplayEntity.Data data = base.getData();
        if (data == null) return null;
        return new TextDisplayDataHelper(data);
    }

    public static class TextDisplayDataHelper extends BaseHelper<DisplayEntity.TextDisplayEntity.Data> {

        public TextDisplayDataHelper(DisplayEntity.TextDisplayEntity.Data base) {
            super(base);
        }

        /**
         * @since 1.9.1
         */
        public TextHelper getText() {
            return TextHelper.wrap(base.text());
        }

        /**
         * @since 1.9.1
         */
        public int getLineWidth() {
            return base.lineWidth();
        }

        /**
         * @since 1.9.1
         */
        public int getTextOpacity() {
            return base.textOpacity().lerp(1.0f);
        }

        /**
         * @since 1.9.1
         */
        public int getBackgroundColor() {
            return base.backgroundColor().lerp(1.0f);
        }

        /**
         * @since 1.9.1
         */
        public boolean hasShadowFlag() {
            return (base.flags() & 1) != 0;
        }

        /**
         * @since 1.9.1
         */
        public boolean hasSeeThroughFlag() {
            return (base.flags() & 2) != 0;
        }

        /**
         * @since 1.9.1
         */
        public boolean hasDefaultBackgroundFlag() {
            return (base.flags() & 4) != 0;
        }

        /**
         * @return "center", "left" or "right"
         * @since 1.9.1
         */
        public String getAlignment() {
            return DisplayEntity.TextDisplayEntity.getAlignment(base.flags()).asString();
        }

    }

}
