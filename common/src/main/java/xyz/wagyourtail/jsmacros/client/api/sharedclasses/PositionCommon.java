package xyz.wagyourtail.jsmacros.client.api.sharedclasses;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockPosHelper;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
@SuppressWarnings("unused")
public class PositionCommon {

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Pos2D {
        public static final Pos2D ZERO = new Pos2D(0, 0);
        public double x;
        public double y;

        public Pos2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public Pos2D add(Pos2D pos) {
            return new Pos2D(x+pos.x, y+pos.y);
        }

        /**
         * @since 1.6.3
         * @param x
         * @param y
         *
         * @return
         */
        public Pos2D add(double x, double y) {
            return new Pos2D(this.x+x, this.y+y);
        }

        public Pos2D multiply(Pos2D pos) {
            return new Pos2D(x*pos.x, y*pos.y);
        }

        /**
         * @since 1.6.3
         * @param x
         * @param y
         *
         * @return
         */
        public Pos2D multiply(double x, double y) {
            return new Pos2D(this.x*x, this.y*y);
        }

        /**
         * @since 1.6.3
         * @param scale
         *
         * @return
         */
        public Pos2D scale(double scale) {
            return new Pos2D(x*scale, y*scale);
        }

        public String toString() {
            return String.format("%f, %f", x, y);
        }

        public Pos3D to3D() {
            return new Pos3D(x, y, 0);
        }

        public Vec2D toVector() {
            return new Vec2D(ZERO, this);
        }

        /**
         * @since 1.6.4
         * @param start_pos
         *
         * @return
         */
        public Vec2D toVector(Pos2D start_pos) {
            return new Vec2D(start_pos, this);
        }


        /**
         * @since 1.6.4
         * @param start_x
         * @param start_y
         *
         * @return
         */
        public Vec2D toVector(double start_x, double start_y) {
            return new Vec2D(start_x, start_y, this.x, this.y);
        }

        /**
         * @since 1.6.4
         * @return
         */
        public Vec2D toReverseVector() {
            return new Vec2D(this, ZERO);
        }

        /**
         * @since 1.6.4
         * @param end_pos
         *
         * @return
         */
        public Vec2D toReverseVector(Pos2D end_pos) {
            return new Vec2D(this, end_pos);
        }

        /**
         * @since 1.6.4
         * @param end_x
         * @param end_y
         *
         * @return
         */
        public Vec2D toReverseVector(double end_x, double end_y) {
            return new Vec2D(this, new Pos2D(end_x, end_y));
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Pos3D extends Pos2D {
        public static final Pos3D ZERO = new Pos3D(0, 0, 0);
        public double z;

        public Pos3D(Vec3d vec) {
            this(vec.x, vec.y, vec.z);
        }

        public Pos3D(double x, double y, double z) {
            super(x, y);
            this.z = z;
        }

        public double getZ() {
            return z;
        }

        public Pos3D add(Pos3D pos) {
            return new Pos3D(x+pos.x, y+pos.y, z+pos.z);
        }

        /**
         * @since 1.6.3
         * @param x
         * @param y
         * @param z
         *
         * @return
         */
        public Pos3D add(double x, double y, double z) {
            return new Pos3D(this.x+x, this.y+y, this.z+z);
        }

        public Pos3D multiply(Pos3D pos) {
            return new Pos3D(x*pos.x, y*pos.y, z*pos.z);
        }

        /**
         * @since 1.6.3
         * @param x
         * @param y
         * @param z
         *
         * @return
         */
        public Pos3D multiply(double x, double y, double z) {
            return new Pos3D(this.x*x, this.y*y, this.z*z);
        }

        /**
         * @since 1.6.3
         * @param scale
         *
         * @return
         */
         @Override
        public Pos3D scale(double scale) {
            return new Pos3D(x*scale, y*scale, z*scale);
        }

        public String toString() {
            return String.format("%f, %f, %f", x, y, z);
        }

        @Override
        public Vec3D toVector() {
            return new Vec3D(ZERO, this);
        }

        /**
         * @since 1.6.4
         * @param start_pos
         *
         * @return
         */
        @Override
        public Vec3D toVector(Pos2D start_pos) {
            return toVector(start_pos.to3D());
        }

        /**
         * @since 1.6.4
         * @param start_pos
         *
         * @return
         */
        public Vec3D toVector(Pos3D start_pos) {
            return new Vec3D(start_pos, this);
        }

        /**
         * @since 1.6.4
         * @param start_x
         * @param start_y
         * @param start_z
         *
         * @return
         */
        public Vec3D toVector(double start_x, double start_y, double start_z) {
            return new Vec3D(start_x, start_y, start_z, this.x, this.y, this.z);
        }

        /**
         * @since 1.6.4
         * @return
         */
        public Vec3D toReverseVector() {
            return new Vec3D(this, ZERO);
        }

        @Override
        public Vec3D toReverseVector(Pos2D end_pos) {
            return toReverseVector(end_pos.to3D());
        }

        /**
         * @since 1.6.4
         * @param end_pos
         *
         * @return
         */
        public Vec3D toReverseVector(Pos3D end_pos) {
            return new Vec3D(this, end_pos);
        }

        /**
         * @since 1.6.4
         * @param end_x
         * @param end_y
         * @param end_z
         *
         * @return
         */
        public Vec3D toReverseVector(double end_x, double end_y, double end_z) {
            return new Vec3D(this, new Pos3D(end_x, end_y, end_z));
        }

        /**
         * @since 1.8.0
         * @return
         */
        public BlockPosHelper toBlockPos() {
            return new BlockPosHelper(new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z)));
        }

        /**
         * @since 1.8.0
         * @return
         */
        public BlockPos toRawBlockPos() {
            return new BlockPos(Math.floor(x), Math.floor(y), Math.floor(z));
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Vec2D {
        public double x1;
        public double y1;
        public double x2;
        public double y2;


        public Vec2D(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public Vec2D(Pos2D start, Pos2D end) {
            this.x1 = start.x;
            this.y1 = start.y;
            this.x2 = end.x;
            this.y2 = end.y;
        }


        public double getX1() {
            return x1;
        }

        public double getY1() {
            return y1;
        }

        public double getX2() {
            return x2;
        }

        public double getY2() {
            return y2;
        }

        public double getDeltaX() {
            return x2 - x1;
        }

        public double getDeltaY() {
            return y2 - y1;
        }

        public Pos2D getStart() {
            return new Pos2D(x1, y1);
        }

        public Pos2D getEnd() {
            return new Pos2D(x2, y2);
        }

        public double getMagnitude() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            return Math.sqrt(dx*dx + dy*dy);
        }

        /**
         * @since 1.6.5
         * @return magnitude squared
         */
        public double getMagnitudeSq() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            return dx*dx + dy*dy;
        }

        public Vec2D add(Vec2D vec) {
            return new Vec2D(x1 + vec.x1, y1 + vec.y1, x2 + vec.x2, y2 + vec.y2);
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
        public Vec2D add(double x1, double y1, double x2, double y2) {
            return new Vec2D(this.x1 + x1, this.y1 + y1, this.x2 + x2, this.y2 + y2);
        }

        public Vec2D multiply(Vec2D vec) {
            return new Vec2D(x1 * vec.x1, y1 * vec.y1, x2 * vec.x2, y2 * vec.y2);
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
        public Vec2D multiply(double x1, double y1, double x2, double y2) {
            return new Vec2D(this.x1 * x1, this.y1 * y1, this.x2 * x2, this.y2 * y2);
        }

        /**
         * @since 1.6.3
         * @param scale
         *
         * @return
         */
        public Vec2D scale(double scale) {
            return new Vec2D(x1 * scale, y1 * scale, x2 * scale, y2 * scale);
        }

        public double dotProduct(Vec2D vec) {
            double dx1 = x2 - x1;
            double dx2 = vec.x2 - vec.x1;
            double dy1 = y2 - y1;
            double dy2 = vec.y2 - vec.y1;
            return dx1 * dx2 + dy1 * dy2;
        }

        public Vec2D reverse() {
            return new Vec2D(x2, y2, x1, y1);
        }

        /**
         * @return a new Vec2D with the same direction but a magnitude of 1
         * @since 1.6.5
         */
        public Vec2D normalize() {
            double mag = getMagnitude();
            return new Vec2D(x1 / mag, y1 / mag, x2 / mag, y2 / mag);
        }

        public String toString() {
            return String.format("%f, %f -> %f, %f", x1, y1, x2, y2);
        }

        public Vec3D to3D() {
            return new Vec3D(x1, y1, 0, x2, y2, 0);
        }
    }

    /**
     * @author Wagyourtail
     * @since 1.2.6 [citation needed]
     */
    public static class Vec3D extends Vec2D {
        public double z1;
        public double z2;

        public Vec3D(double x1, double y1, double z1, double x2, double y2, double z2) {
            super(x1, y1, x2, y2);
            this.z1 = z1;
            this.z2 = z2;
        }

        public Vec3D(Pos3D start, Pos3D end) {
            super(start, end);
            this.z1 = start.z;
            this.z2 = end.z;
        }

        public double getZ1() {
            return z1;
        }

        public double getZ2() {
            return z2;
        }

        public double getDeltaZ() {
            return z2 - z1;
        }

        @Override
        public Pos3D getStart() {
            return new Pos3D(x1, y1, z1);
        }

        @Override
        public Pos3D getEnd() {
            return new Pos3D(x2, y2, z2);
        }

        @Override
        public double getMagnitude() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }

        @Override
        public double getMagnitudeSq() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            return dx*dx + dy*dy + dz*dz;
        }

        public Vec3D add(Vec3D vec) {
            return new Vec3D(this.x1 + vec.x1, this.y1 + vec.y1, this.z1 + vec.z1, this.x2 + vec.x2, this.y2 + vec.y2, this.z2 + vec.z2);
        }

        /**
         * @since 1.6.4
         * @param pos
         *
         * @return
         */
        public Vec3D addStart(Pos3D pos) {
            return new Vec3D(this.x1 + pos.x, this.y1 + pos.y, this.z1 + pos.z, this.x2, this.y2, this.z2);
        }

        /**
         * @since 1.6.4
         * @param pos
         *
         * @return
         */
        public Vec3D addEnd(Pos3D pos) {
            return new Vec3D(this.x1, this.y1, this.z1, this.x2 + pos.x, this.y2 + pos.y, this.z2 + pos.z);
        }

        /**
         * @since 1.6.4
         * @param x
         * @param y
         * @param z
         *
         * @return
         */
        public Vec3D addStart(double x, double y, double z) {
            return new Vec3D(this.x1 + x, this.y1 + y, this.z1 + z, this.x2, this.y2, this.z2);
        }

        /**
         * @since 1.6.4
         * @param x
         * @param y
         * @param z
         *
         * @return
         */
        public Vec3D addEnd(double x, double y, double z) {
            return new Vec3D(this.x1, this.y1, this.z1, this.x2 + x, this.y2 + y, this.z2 + z);
        }


        /**
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
        public Vec3D add(double x1, double y1, double z1, double x2, double y2, double z2) {
            return new Vec3D(this.x1 + x1, this.y1 + y1, this.z1 + z1, this.x2 + x2, this.y2 + y2, this.z2 + z2);
        }

        public Vec3D multiply(Vec3D vec) {
            return new Vec3D(this.x1 * vec.x1, this.y1 * vec.y1, this.z1 * vec.z1, this.x2 * vec.x2, this.y2 * vec.y2, this.z2 * vec.z2);
        }

        /**
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
        public Vec3D multiply(double x1, double y1, double z1, double x2, double y2, double z2) {
            return new Vec3D(this.x1 * x1, this.y1 * y1, this.z1 * z1, this.x2 * x2, this.y2 * y2, this.z2 * z2);
        }

        /**
         *
         * @since 1.6.3
         * @param scale
         *
         * @return
         */
         @Override
        public Vec3D scale(double scale) {
            return new Vec3D(x1 * scale, y1 * scale, z1 * scale, x2 * scale, y2 * scale, z2 * scale);
        }

        /**
         * @since 1.6.5
         *
         * @return
         */
        @Override
        public Vec3D normalize() {
            double mag = getMagnitude();
            return new Vec3D(x1 / mag, y1 / mag, z1 / mag, x2 / mag, y2 / mag, z2 / mag);
        }

        public float getPitch() {
            double dx = x2 - x1;
            double dy = y2 - y1;
            double dz = z2 - z1;
            double xz = Math.sqrt(dx*dx + dz*dz);
            return  90F - (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(xz, -dy)));
        }

        public float getYaw() {
            double dx = x2 - x1;
            double dz = z2 - z1;
            return (float) -MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(dx, dz)));
        }

        public double dotProduct(Vec3D vec) {
            double dz1 = z2 - z1;
            double dz2 = vec.z2 - vec.z1;
            return super.dotProduct(vec) + dz1 * dz2;
        }

        public Vec3D crossProduct(Vec3D vec) {
            double dx1 = x2 - x1;
            double dx2 = vec.x2 - vec.x1;
            double dy1 = y2 - y1;
            double dy2 = vec.y2 - vec.y1;
            double dz1 = z2 - z1;
            double dz2 = vec.z2 - vec.z1;
            return new Vec3D(0, 0, 0, dy1*dz2 - dz1*dy2, dz1*dx2 - dx1*dz2, dx1*dy2 - dy1*dx2);
        }

        @Override
        public Vec3D reverse() {
            return new Vec3D(x2, y2, z2, x1, y1, z1);
        }

        @Override
        public String toString() {
            return String.format("%f, %f, %f -> %f, %f, %f", x1, y1, z1, x2, y2, z2);
        }
    }

    /**
     * @since 1.6.5
     */
    public static class Plane3D {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;
        public double x3;
        public double y3;
        public double z3;

        public Plane3D(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
            this.x3 = x3;
            this.y3 = y3;
            this.z3 = z3;
        }

        public Vec3D getNormalVector() {
            return new Vec3D(x1, y1, z1, x2, y2, z2).crossProduct(new Vec3D(x2, y2, z2, x3, y3, z3));
        }

        public Vec3D getVec12() {
            return new Vec3D(x1, y1, z1, x2, y2, z2);
        }

        public Vec3D getVec13() {
            return new Vec3D(x1, y1, z1, x3, y3, z3);
        }

        public Vec3D getVec23() {
            return new Vec3D(x2, y2, z2, x3, y3, z3);
        }
    }

}
