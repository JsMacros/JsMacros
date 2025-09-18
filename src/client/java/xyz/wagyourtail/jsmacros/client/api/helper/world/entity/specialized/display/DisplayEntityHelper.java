package xyz.wagyourtail.jsmacros.client.api.helper.world.entity.specialized.display;

import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.util.math.Box;
import xyz.wagyourtail.jsmacros.api.math.Vec3D;
import xyz.wagyourtail.jsmacros.client.api.helper.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinDisplayEntity;

/**
 * @author aMelonRind
 * @since 1.9.1
 */
@SuppressWarnings("unused")
public class DisplayEntityHelper<T extends DisplayEntity> extends EntityHelper<T> {

    public DisplayEntityHelper(T base) {
        super(base);
    }

    /**
     * @since 1.9.1
     */
    public double getLerpTargetX() {
        return base.getLerpedPos(getLerpProgress(0)).x;
    }

    /**
     * @since 1.9.1
     */
    public double getLerpTargetY() {
        return base.getLerpedPos(getLerpProgress(0)).y;
    }

    /**
     * @since 1.9.1
     */
    public double getLerpTargetZ() {
        return base.getLerpedPos(getLerpProgress(0)).z;
    }

    /**
     * @since 1.9.1
     */
    public float getLerpTargetPitch() {
        return base.getLerpedPitch(getLerpProgress(0));
    }

    /**
     * @since 1.9.1
     */
    public float getLerpTargetYaw() {
        return base.getLerpedYaw(getLerpProgress(0));
    }

    /**
     * @since 1.9.1
     */
    public Vec3D getVisibilityBoundingBox() {
        Box box = base.getVisibilityBoundingBox();
        return new Vec3D(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    /**
     * @return "fixed", "vertical", "horizontal" or "center"
     * @since 1.9.1
     */
    public String getBillboardMode() {
        return ((MixinDisplayEntity) base).callGetBillboardMode().asString();
    }

    /**
     * @since 1.9.1
     */
    public int getBrightness() {
        Brightness bri = ((MixinDisplayEntity) base).callGetBrightnessUnpacked();
        return bri == null ? 0 : Math.max(bri.sky(), bri.block());
    }

    /**
     * @since 1.9.1
     */
    public int getSkyBrightness() {
        Brightness bri = ((MixinDisplayEntity) base).callGetBrightnessUnpacked();
        return bri == null ? 0 : bri.sky();
    }

    /**
     * @since 1.9.1
     */
    public int getBlockBrightness() {
        Brightness bri = ((MixinDisplayEntity) base).callGetBrightnessUnpacked();
        return bri == null ? 0 : bri.block();
    }

    /**
     * @since 1.9.1
     */
    public float getViewRange() {
        return ((MixinDisplayEntity) base).callGetViewRange();
    }

    /**
     * @since 1.9.1
     */
    public float getShadowRadius() {
        return ((MixinDisplayEntity) base).callGetShadowRadius();
    }

    /**
     * @since 1.9.1
     */
    public float getShadowStrength() {
        return ((MixinDisplayEntity) base).callGetShadowStrength();
    }

    /**
     * @since 1.9.1
     */
    public float getDisplayWidth() {
        return ((MixinDisplayEntity) base).callGetDisplayWidth();
    }

    /**
     * @since 1.9.1
     */
    public int getGlowColorOverride() {
        return ((MixinDisplayEntity) base).callGetGlowColorOverride();
    }

    /**
     * @since 1.9.1
     */
    public float getLerpProgress(double delta) {
        return base.getLerpProgress((float) delta);
    }

    /**
     * @since 1.9.1
     */
    public float getDisplayHeight() {
        return ((MixinDisplayEntity) base).callGetDisplayHeight();
    }

}
