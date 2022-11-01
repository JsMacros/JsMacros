package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import net.minecraft.entity.Entity;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.fabric.client.access.IEntity;
import xyz.wagyourtail.jsmacros.client.access.IStyle;

public class TextBuilderFabric extends TextBuilder {


    @Override
    public TextBuilder withColor(int r, int g, int b) {
        self.styled(style -> ((IStyle)style).setCustomColor((r & 255) << 16 | (g & 255) << 8 | (b & 255)));
        return this;
    }

    @Override
    public TextBuilder withShowEntityHover(EntityHelper<Entity> entity) {
        Entity raw = entity.getRaw();
        self.styled(style -> style.setHoverEvent(((IEntity)entity.getRaw()).jsmacros_getHoverEvent()));
        return this;
    }

}
