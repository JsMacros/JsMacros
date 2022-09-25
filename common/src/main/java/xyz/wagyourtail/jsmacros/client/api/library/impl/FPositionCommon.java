package xyz.wagyourtail.jsmacros.client.api.library.impl;

import xyz.wagyourtail.jsmacros.client.api.helpers.block.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

/**
 * position helper classes
 * @since 1.6.3
 */
@Library("PositionCommon")
@SuppressWarnings("unused")
public class FPositionCommon extends BaseLibrary {
    /**
     * create a new vector object
     *
     * @since 1.6.3
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     *
     * @return
     */
    public PositionCommon.Vec3D createVec(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new PositionCommon.Vec3D(x1, y1, z1, x2, y2, z2);
    }

    /**
     * @since 1.6.3
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     *
     * @return
     */
    public PositionCommon.Vec2D createVec(double x1, double y1, double x2, double y2) {
        return new PositionCommon.Vec2D(x1, y1, x2, y2);
    }

    /**
     * @since 1.6.3
     * @param x
     * @param y
     * @param z
     *
     * @return
     */
    public PositionCommon.Pos3D createPos(double x, double y, double z) {
        return new PositionCommon.Pos3D(x, y, z);
    }

    /**
     *
     * @since 1.6.3
     * @param x
     * @param y
     *
     * @return
     */
    public PositionCommon.Pos2D createPos(double x, double y) {
        return new PositionCommon.Pos2D(x, y);
    }

    /**
     * @param x the x position of the block
     * @param y the y position of the block
     * @param z the z position of the block
     * @return a {@link BlockPosHelper} for the given coordinates.
     *
     * @since 1.8.4
     */
    public BlockPosHelper createBlockPos(int x, int y, int z) {
        return new BlockPosHelper(x, y, z);
    }
    
}
