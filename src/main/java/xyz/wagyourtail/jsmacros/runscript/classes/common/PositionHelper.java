package xyz.wagyourtail.jsmacros.runscript.classes.common;

public class MathHelper extends net.minecraft.util.math.MathHelper {
    public static class Pos2D {
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
    }

    public static class Pos3D {
        public double x;
        public double y;
        public double z;

        public Pos3D(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

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
    }

    public static class Vec3D {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;

        public Vec3D(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
    }

}
