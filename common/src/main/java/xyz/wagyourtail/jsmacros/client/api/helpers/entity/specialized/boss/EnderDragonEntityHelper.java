package xyz.wagyourtail.jsmacros.client.api.helpers.entity.specialized.boss;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;

import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.MobEntityHelper;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinPhaseType;

import java.util.Arrays;
import java.util.List;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class EnderDragonEntityHelper extends MobEntityHelper<EnderDragonEntity> {

    public EnderDragonEntityHelper(EnderDragonEntity base) {
        super(base);
    }

    /**
     * @return the current phase of the dragon.
     *
     * @since 1.8.4
     */
    public String getPhase() {
        return ((MixinPhaseType) base.getPhaseManager().getCurrent().getType()).getName();
    }

    /**
     * @param index the index of the dragon's body part to get
     * @return the specified body part of the dragon.
     *
     * @since 1.8.4
     */
    public EntityHelper<?> getBodyPart(int index) {
        return EntityHelper.create(base.getBodyParts()[index]);
    }

    /**
     * @return a list of all body parts of the dragon.
     *
     * @since 1.8.4
     */
    public List<? extends EntityHelper<?>> getBodyParts() {
        return Arrays.stream(base.getBodyParts()).map(EntityHelper::create).toList();
    }

    /**
     * The name can be either {@code head}, {@code neck}, {@code body}, {@code tail} or {@code wing}.
     *
     * @param name the name of the body part to get
     * @return a list of all body parts of the dragon with the specified name.
     *
     * @since 1.8.4
     */
    public List<? extends EntityHelper<?>> getBodyParts(String name) {
        return Arrays.stream(base.getBodyParts()).filter(e -> e.name.equals(name)).map(EntityHelper::create).toList();
    }

}
