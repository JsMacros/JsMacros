package xyz.wagyourtail.jsmacros.api.math;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author Wagyourtail
 * @since 1.2.6 [citation needed]
 */
public class Pos2D {
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
        return new Pos2D(x + pos.x, y + pos.y);
    }

    /**
     * @param x
     * @param y
     * @return
     * @since 1.6.3
     */
    public Pos2D add(double x, double y) {
        return new Pos2D(this.x + x, this.y + y);
    }

    /**
     * @param pos the position to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D sub(Pos2D pos) {
        return new Pos2D(x - pos.x, y - pos.y);
    }

    /**
     * @param x the x coordinate to subtract
     * @param y the y coordinate to subtract
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D sub(double x, double y) {
        return new Pos2D(this.x - x, this.y - y);
    }

    public Pos2D multiply(Pos2D pos) {
        return new Pos2D(x * pos.x, y * pos.y);
    }

    /**
     * @param x
     * @param y
     * @return
     * @since 1.6.3
     */
    public Pos2D multiply(double x, double y) {
        return new Pos2D(this.x * x, this.y * y);
    }

    /**
     * @param pos the position to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D divide(Pos2D pos) {
        return new Pos2D(x / pos.x, y / pos.y);
    }

    /**
     * @param x the x coordinate to divide by
     * @param y the y coordinate to divide by
     * @return the new position.
     * @since 1.8.4
     */
    public Pos2D divide(double x, double y) {
        return new Pos2D(this.x / x, this.y / y);
    }

    /**
     * @param scale
     * @return
     * @since 1.6.3
     */
    public Pos2D scale(double scale) {
        return new Pos2D(x * scale, y * scale);
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
     * @param start_pos
     * @return
     * @since 1.6.4
     */
    public Vec2D toVector(Pos2D start_pos) {
        return new Vec2D(start_pos, this);
    }

    /**
     * @param start_x
     * @param start_y
     * @return
     * @since 1.6.4
     */
    public Vec2D toVector(double start_x, double start_y) {
        return new Vec2D(start_x, start_y, this.x, this.y);
    }

    /**
     * @return
     * @since 1.6.4
     */
    public Vec2D toReverseVector() {
        return new Vec2D(this, ZERO);
    }

    /**
     * @param end_pos
     * @return
     * @since 1.6.4
     */
    public Vec2D toReverseVector(Pos2D end_pos) {
        return new Vec2D(this, end_pos);
    }

    /**
     * @param end_x
     * @param end_y
     * @return
     * @since 1.6.4
     */
    public Vec2D toReverseVector(double end_x, double end_y) {
        return new Vec2D(this, new Pos2D(end_x, end_y));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pos2D pos2D = (Pos2D) o;
        return Double.compare(x, pos2D.x) == 0 && Double.compare(y, pos2D.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int compareTo(@NotNull Pos2D o) {
        int i = Double.compare(x, o.x);
        if (i == 0) {
            i = Double.compare(y, o.y);
        }
        return i;
    }
}
