package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Etheradon
 * @since 1.8.4
 */
@SuppressWarnings("unused")
public class CustomImage {

    public static final Map<String, CustomImage> IMAGES = new HashMap<>();

    private static final String PREFIX = "jsmimage/";
    private static int currentId = 0;

    private final BufferedImage image;
    private final Graphics2D graphics;
    private final String name;
    private final NativeImageBackedTexture texture;
    private final Identifier identifier;

    public CustomImage(BufferedImage image) {
        this(image, String.valueOf(currentId));
    }

    public CustomImage(BufferedImage image, String name) {
        this.image = image;
        this.graphics = image.createGraphics();
        this.name = name;
        this.texture = createTexture(image, PREFIX + name);
        identifier = Identifier.of(PREFIX + name);
        MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
        update();
        currentId++;
        IMAGES.put(identifier.toString(), this);
    }

    /**
     * @return the name of this image.
     * @since 1.8.4
     */
    public String getName() {
        return name;
    }

    /**
     * The image can be used with the drawImage methods to draw it onto this image.
     *
     * @param path the path to the image, relative to the jsMacros config folder
     * @return an image from the given path.
     * @see #drawImage(Image, int, int, int, int)
     * @see #drawImage(Image, int, int, int, int, int, int, int, int)
     * @since 1.8.4
     */
    @Nullable
    public BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(JsMacrosClient.clientCore.config.configFolder.toPath().resolve(path).toFile());
        } catch (IOException e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        return null;
    }

    /**
     * Loads the image from the given path and returns a subimage of it from the given positions.
     * The image can be used with the drawImage methods to draw it onto this image.
     *
     * @param path   the path to the image, relative to the jsMacros config folder
     * @param x      the x position to get the subimage from
     * @param y      the y position to get the subimage from
     * @param width  the width of the subimage
     * @param height the height of the subimage
     * @return the cropped image from the given path.
     * @see #drawImage(Image, int, int, int, int)
     * @see #drawImage(Image, int, int, int, int, int, int, int, int)
     * @since 1.8.4
     */
    public BufferedImage loadImage(String path, int x, int y, int width, int height) {
        BufferedImage image = loadImage(path);
        if (image != null) {
            image = image.getSubimage(x, y, width, height);
        }
        return image;
    }

    /**
     * Updates the texture to be drawn with the contents of this image. Any changes made to this
     * image will only be displayed after calling this method. The method must not be called after
     * each change, but rather when the image is finished being changed.
     *
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage update() {
        try {
            final Semaphore semaphore = new Semaphore(0);
            MinecraftClient.getInstance().execute(() -> {
                texture.upload();
                updateTexture();
                semaphore.release();
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        return this;
    }

    /**
     * @since 1.8.4 Copies every pixel of the internal BufferedImage to the
     * NativeImageBackedTexture.
     */
    private void updateTexture() {
        NativeImage ni = texture.getImage();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                ni.setColorArgb(x, y, image.getRGB(x, y));
            }
        }
        texture.upload();
    }

    /**
     * Saves this image to the given path. The file will be saved as a png.
     *
     * @param path     the path to the image, relative to the jsMacros config folder
     * @param fileName the file name of the image, without the extension
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage saveImage(String path, String fileName) {
        try {
            File file = JsMacrosClient.clientCore.config.configFolder.toPath().resolve(path).resolve(fileName + ".png").toFile();
            if (!file.exists()) {
                if (!file.mkdirs() && !file.createNewFile()) {
                    JsMacrosClient.clientCore.profile.logError(new RuntimeException("Could not create file: " + file.getAbsolutePath()));
                    return this;
                }
            }
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        return this;
    }

    /**
     * The identifier should be used with any buttons and textures in the draw2D and other classes,
     * which require an identifier.
     *
     * @return the identifier of this image.
     * @since 1.8.4
     */
    public String getIdentifier() {
        return identifier.toString();
    }

    /**
     * The width is a constant and will not change.
     *
     * @return the width of this image.
     * @since 1.8.4
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * The height is a constant and will not change.
     *
     * @return the height of this image.
     * @since 1.8.4
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * @return the internal BufferedImage of this image, which all updates are made to.
     * @since 1.8.4
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * The color is in the ARGB format.
     *
     * @param x the x position to get the color from
     * @param y the y position to get the color from
     * @return the color at the given position.
     * @since 1.8.4
     */
    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }

    /**
     * The color is in the ARGB format.
     *
     * @param x    the x position to set the color at
     * @param y    the y position to set the color at
     * @param argb the ARGB value to set the pixel to
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage setPixel(int x, int y, int argb) {
        image.setRGB(x, y, argb);
        return this;
    }

    /**
     * @param img    the image to draw onto this image
     * @param x      the x position to draw the image at
     * @param y      the y position to draw the image at
     * @param width  the width of the image to draw
     * @param height the height of the image to draw
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawImage(Image img, int x, int y, int width, int height) {
        graphics.drawImage(img, x, y, width, height, null);
        return this;
    }

    /**
     * @param img          the image to draw onto this image
     * @param x            the x position to draw the image at
     * @param y            the y position to draw the image at
     * @param width        the width of the image to draw
     * @param height       the height of the image to draw
     * @param sourceX      the x position of the subimage to draw
     * @param sourceY      the y position of the subimage to draw
     * @param sourceWidth  the width of the subimage to draw
     * @param sourceHeight the height of the subimage to draw
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawImage(Image img, int x, int y, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        graphics.drawImage(image, x, y, x + width, y + height, sourceX, sourceY, sourceX + sourceWidth, sourceY + sourceHeight, null);
        return this;
    }

    /**
     * The color is a rgb value which is used for draw and fill operations.
     *
     * @return the graphics current rgb color.
     * @since 1.8.4
     */
    public int getGraphicsColor() {
        return graphics.getColor().getRGB();
    }

    /**
     * The color is a rgb value which is used for draw and fill operations.
     *
     * @param color the rgb color to use for graphics operations
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage setGraphicsColor(int color) {
        graphics.setColor(new Color(color));
        return this;
    }

    /**
     * @param x the x position of the origin point
     * @param y the y position of the origin point
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage translate(int x, int y) {
        graphics.translate(x, y);
        return this;
    }

    /**
     * @param x      the x coordinate of the rectangle to intersect the clip with
     * @param y      the y coordinate of the rectangle to intersect the clip with
     * @param width  the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @return self for chaining.
     */
    public CustomImage clipRect(int x, int y, int width, int height) {
        graphics.clipRect(x, y, width, height);
        return this;
    }

    /**
     * @param x      the x coordinate of the new clip rectangle
     * @param y      the y coordinate of the new clip rectangle
     * @param width  the width of the new clip rectangle
     * @param height the height of the new clip rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage setClip(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        graphics.setClip(rect);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage setPaintMode() {
        graphics.setPaintMode();
        return this;
    }

    /**
     * @param color the color to use for the xor operation
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage setXorMode(int color) {
        graphics.setXORMode(new Color(color));
        return this;
    }

    /**
     * @return an array with the bounds of the current clip.
     * @since 1.8.4
     */
    public Rectangle getClipBounds() {
        return graphics.getClipBounds();
    }

    /**
     * @param x      the x position to copy from
     * @param y      the y position to copy from
     * @param width  the width of the area to copy
     * @param height the height of the area to copy
     * @param dx     the offset to the x position to copy to
     * @param dy     the offset to the y position to copy to
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage copyArea(int x, int y, int width, int height, int dx, int dy) {
        graphics.copyArea(x, y, width, height, dx, dy);
        return this;
    }

    /**
     * @param x1 the first x position of the line
     * @param y1 the first y position of the line
     * @param x2 the second x position of the line
     * @param y2 the second y position of the line
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
        return this;
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawRect(int x, int y, int width, int height) {
        graphics.drawRect(x, y, width, height);
        return this;
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fillRect(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);
        return this;
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage clearRect(int x, int y, int width, int height) {
        graphics.clearRect(x, y, width, height);
        return this;
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the rgb color to fill the rectangle with
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage clearRect(int x, int y, int width, int height, int color) {
        Color cached = graphics.getBackground();
        graphics.setBackground(new Color(color));
        graphics.clearRect(x, y, width, height);
        graphics.setBackground(cached);
        return this;
    }

    /**
     * @param x         the x position to draw the rectangle at
     * @param y         the y position to draw the rectangle at
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param arcWidth  the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    /**
     * @param x         the x position to draw the rectangle at
     * @param y         the y position to draw the rectangle at
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param arcWidth  the horizontal diameter of the arc at the four corners
     * @param arcHeight the vertical diameter of the arc at the four corners
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    /**
     * @param x      the x position to draw the 3D rectangle at
     * @param y      the y position to draw the 3D rectangle at
     * @param width  the width of the 3D rectangle
     * @param height the height of the 3D rectangle
     * @param raised whether the rectangle should be raised above the surface or etched into the
     *               surface
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage draw3DRect(int x, int y, int width, int height, boolean raised) {
        graphics.draw3DRect(x, y, width, height, raised);
        return this;
    }

    /**
     * @param x      the x position to draw the 3D rectangle at
     * @param y      the y position to draw the 3D rectangle at
     * @param width  the width of the 3D rectangle
     * @param height the height of the 3D rectangle
     * @param raised whether the rectangle should be raised above the surface or etched into the
     *               surface
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fill3DRect(int x, int y, int width, int height, boolean raised) {
        graphics.fill3DRect(x, y, width, height, raised);
        return this;
    }

    /**
     * @param x      the x position to draw the oval at
     * @param y      the y position to draw the oval at
     * @param width  the width of the oval
     * @param height the height of the oval
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
        return this;
    }

    /**
     * @param x      the x position to draw the oval at
     * @param y      the y position to draw the oval at
     * @param width  the width of the oval
     * @param height the height of the oval
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
        return this;
    }

    /**
     * @param x          the x position to draw the arc at
     * @param y          the y position to draw the arc at
     * @param width      the width of the arc
     * @param height     the height of the arc
     * @param startAngle the beginning angle
     * @param arcAngle   the angular extent of the arc, relative to the start angle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.drawArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    /**
     * @param x          the x position to draw the arc at
     * @param y          the y position to draw the arc at
     * @param width      the width of the arc
     * @param height     the height of the arc
     * @param startAngle the beginning angle
     * @param arcAngle   the angular extent of the arc, relative to the start angle
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    /**
     * The x and y array must have the same length and order for the points.
     *
     * @param pointsX an array of all x positions of the points in the polygon
     * @param pointsY an array of all y positions of the points in the polygon
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawPolygonLine(int[] pointsX, int[] pointsY) {
        graphics.drawPolyline(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    /**
     * The x and y array must have the same length and order for the points.
     *
     * @param pointsX an array of all x positions of the points in the polygon
     * @param pointsY an array of all y positions of the points in the polygon
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawPolygon(int[] pointsX, int[] pointsY) {
        graphics.drawPolygon(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    /**
     * The x and y array must have the same length and order for the points.
     *
     * @param pointsX an array of all x positions of the points in the polygon
     * @param pointsY an array of all y positions of the points in the polygon
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage fillPolygon(int[] pointsX, int[] pointsY) {
        graphics.fillPolygon(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    /**
     * @param x    the x position to draw the string at
     * @param y    the y position to draw the string at
     * @param text the text to draw
     * @return self for chaining.
     * @since 1.8.4
     */
    public CustomImage drawString(int x, int y, String text) {
        graphics.drawString(text, x, y);
        return this;
    }

    /**
     * @param toAnalyze the string to analyze
     * @return the width of the string for the current font in pixels
     * @since 1.8.4
     */
    public int getStringWidth(String toAnalyze) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(toAnalyze);
    }

    private static NativeImageBackedTexture createTexture(BufferedImage image, String name) {
        AtomicReference<NativeImageBackedTexture> texture = new AtomicReference<>();
        try {
            final Semaphore semaphore = new Semaphore(0);
            MinecraftClient.getInstance().execute(() -> {
                texture.set(new NativeImageBackedTexture(name, image.getWidth(), image.getHeight(), true));
                semaphore.release();
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        return texture.get();
    }

    public static CustomImage createWidget(int width, int height, String name) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return new CustomImage(img, name);
    }

    @Nullable
    public static CustomImage createWidget(String path, String name) {
        try {
            File file = JsMacrosClient.clientCore.config.configFolder.toPath().resolve(path).toFile();
            return new CustomImage(ImageIO.read(file), name);
        } catch (IOException e) {
            JsMacrosClient.clientCore.profile.logError(e);
        }
        return null;
    }

    /**
     * Minecraft textures use an ABGR format for some reason.
     *
     * @param argb the argb color to transform
     * @return the abgr argb for the given argb color.
     * @since 1.8.4
     */
    public static int nativeARGBFlip(int argb) {
        return ((argb & 0x000000FF) << 16) | ((argb & (0x00FF0000)) >> 16) | (argb & 0xFF00FF00);
    }

}
