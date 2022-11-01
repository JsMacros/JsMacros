package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import net.minecraft.entity.Entity;
import net.minecraft.text.Style;
import xyz.wagyourtail.jsmacros.client.access.IEntity;
import xyz.wagyourtail.jsmacros.client.access.IStyle;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;

public class TextBuilderForge extends TextBuilder {
    @Override
    public TextBuilder withColor(int r, int g, int b) {
        Style style = self.getStyle();
        ((IStyle)style).jsmacros_setCustomColor((r & 255) << 16 | (g & 255) << 8 | (b & 255));
        return this;
    }

    @Override
    public TextBuilder withShowEntityHover(EntityHelper<Entity> entity) {
        Entity raw = entity.getRaw();
        Style style = self.getStyle();
        style.setHoverEvent(((IEntity)entity.getRaw()).jsmacros_getHoverEvent());
        return this;
    }

}
