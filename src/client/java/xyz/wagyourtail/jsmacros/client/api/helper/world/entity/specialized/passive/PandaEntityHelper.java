package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.passive;

import net.minecraft.entity.passive.PandaEntity;
import xyz.wagyourtail.doclet.DocletReplaceReturn;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class PandaEntityHelper extends AnimalEntityHelper<PandaEntity> {

    public PandaEntityHelper(PandaEntity base) {
        super(base);
    }

    /**
     * @return the id of this panda's main gene.
     * @since 1.8.4
     */
    public int getMainGene() {
        return base.getMainGene().getId();
    }

    /**
     * @return the name of this panda's main gene.
     * @since 1.8.4
     */
    @DocletReplaceReturn("PandaGene")
    public String getMainGeneName() {
        return base.getMainGene().asString();
    }

    /**
     * @return {@code true} if this panda's main gene is recessive, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isMainGeneRecessive() {
        return base.getMainGene().isRecessive();
    }

    /**
     * @return the id of this panda's hidden gene.
     * @since 1.8.4
     */
    public int getHiddenGene() {
        return base.getHiddenGene().getId();
    }

    /**
     * @return the name of this panda's hidden gene.
     * @since 1.8.4
     */
    @DocletReplaceReturn("PandaGene")
    public String getHiddenGeneName() {
        return base.getHiddenGene().asString();
    }

    /**
     * @return {@code true} if this panda's hidden gene is recessive, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isHiddenGeneRecessive() {
        return base.getHiddenGene().isRecessive();
    }

    /**
     * @return {@code true} if this panda is idling, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isIdle() {
        return base.isIdle();
    }

    /**
     * @return {@code true} if this panda is currently sneezing, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSneezing() {
        return base.isSneezing();
    }

    /**
     * @return {@code true} if this panda is playing, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPlaying() {
        return base.isPlaying();
    }

    /**
     * @return {@code true} if this panda is sitting, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isSitting() {
        return base.isSitting();
    }

    /**
     * @return {@code true} if this panda is lying on its back, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isLyingOnBack() {
        return base.isLyingOnBack();
    }

    /**
     * @return {@code true} if this panda's genes make him lazy, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isLazy() {
        return base.isLazy();
    }

    /**
     * @return {@code true} if this panda's genes make him worried, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWorried() {
        return base.isWorried();
    }

    /**
     * @return {@code true} if this panda is scared by an active thunderstorm, {@code false}
     * otherwise.
     * @since 1.8.4
     */
    public boolean isScaredByThunderstorm() {
        return base.isScaredByThunderstorm();
    }

    /**
     * @return {@code true} if this panda's genes make him playful, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isPlayful() {
        return base.isPlayful();
    }

    /**
     * @return {@code true} if this panda's genes make him brown, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isBrown() {
        return base.isBrown();
    }

    /**
     * @return {@code true} if this panda's genes make him weak, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isWeak() {
        return base.isWeak();
    }

    /**
     * @return {@code true} if this panda's genes make him aggressive, {@code false} otherwise.
     * @since 1.8.4
     */
    public boolean isAttacking() {
        return base.isAttacking();
    }

}
