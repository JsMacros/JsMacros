package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.mob;

import net.minecraft.entity.mob.SlimeEntity;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.MobEntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class SlimeEntityHelper extends MobEntityHelper<SlimeEntity> {

    public SlimeEntityHelper(SlimeEntity base) {
        super(base);
    }

    /**
     * @return the size of this slime.
     * @since 1.8.4
     */
    public int getSize() {
        return base.getSize();
    }

    /**
     * Small slimes, with a size less than 1, don't attack the player.
     *
     * @return {@code true} if this slime is small, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSmall() {
        return base.isSmall();
    }

}
