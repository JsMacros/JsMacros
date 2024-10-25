package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.decoration;

import net.minecraft.entity.decoration.EndCrystalEntity;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helper.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class EndCrystalEntityHelper extends EntityHelper<EndCrystalEntity> {

    public EndCrystalEntityHelper(EndCrystalEntity base) {
        super(base);
    }

    /**
     * Naturally generated end crystals will have a bedrock base, while player placed ones will
     * not.
     *
     * @return {@code true} if the end crystal was not placed by a player, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isNatural() {
        return base.shouldShowBottom();
    }

    /**
     * @return the target of the crystal's beam, or {@code null} if there is none.
     * @since 1.8.4
     */
    @Nullable
    public BlockPosHelper getBeamTarget() {
        return base.getBeamTarget() == null ? null : new BlockPosHelper(base.getBeamTarget());
    }

}
