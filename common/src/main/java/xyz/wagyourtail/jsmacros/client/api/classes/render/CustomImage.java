package xyz.wagyourtail.jsmacros.client.api.classes.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.core.Core;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
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
        this.texture = createTexture(image);
        identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(PREFIX + name, texture);
        update();
        currentId++;
        IMAGES.put(identifier.toString(), this);
    }

    public String getName() {
        return name;
    }

    public BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(JsMacros.core.config.configFolder.toPath().resolve(path).toFile());
        } catch (IOException e) {
            Core.getInstance().profile.logError(e);
        }
        return null;
    }

    public BufferedImage loadImage(String path, int x, int y, int width, int height) {
        BufferedImage image = loadImage(path);
        if (image != null) {
            image = image.getSubimage(x, y, width, height);
        }
        return image;
    }

    public CustomImage update() {
        try {
            final Semaphore semaphore = new Semaphore(0);
            MinecraftClient.getInstance().execute(() -> {
                texture.bindTexture();
                updateTexture();
                semaphore.release();
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            Core.getInstance().profile.logError(e);
        }
        return this;
    }

    private void updateTexture() {
        NativeImage ni = texture.getImage();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                ni.setColor(x, y, nativeARGBFlip(image.getRGB(x, y)));
            }
        }
        texture.upload();
    }

    public void disposeGraphics() {
        graphics.dispose();
    }

    public CustomImage saveImage(String path, String fileName) {
        try {
            File file = JsMacros.core.config.configFolder.toPath().resolve(path).resolve(fileName + ".png").toFile();
            if (!file.exists()) {
                file.mkdirs();
                file.createNewFile();
            }
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            Core.getInstance().profile.logError(e);
        }
        return this;
    }

    public String getIdentifier() {
        return identifier.toString();
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }

    public CustomImage setPixel(int x, int y, int rgba) {
        image.setRGB(x, y, rgba);
        return this;
    }

    public CustomImage drawImage(Image img, int x, int y, int width, int height) {
        graphics.drawImage(img, x, y, width, height, null);
        return this;
    }

    public CustomImage drawImage(Image img, int x, int y, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight) {
        graphics.drawImage(image, x, y, width, height, sourceX, sourceY, sourceWidth, sourceHeight, null);
        return this;
    }

    public int getColor() {
        return graphics.getColor().getRGB();
    }

    public CustomImage setColor(int color) {
        graphics.setColor(new Color(color));
        return this;
    }

    public CustomImage translate(int x, int y) {
        graphics.translate(x, y);
        return this;
    }

    public CustomImage clipRect(int x, int y, int width, int height) {
        graphics.clipRect(x, y, width, height);
        return this;
    }

    public CustomImage setPaintMode() {
        graphics.setPaintMode();
        return this;
    }

    public CustomImage setXorMode(int color) {
        graphics.setXORMode(new Color(color));
        return this;
    }

    public CustomImage setFont(String path, float fontSize) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, JsMacros.core.config.configFolder.toPath().resolve(path).toFile()).deriveFont(fontSize);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            graphics.setFont(font);
        } catch (FontFormatException | IOException e) {
            Core.getInstance().profile.logError(e);
        }
        return this;
    }

    public Font getFont() {
        return graphics.getFont();
    }

    public CustomImage setFont(String path) {
        return setFont(path, graphics.getFont().getSize2D());
    }

    public int[] getBounds() {
        Rectangle rect = graphics.getClipBounds();
        int[] bounds = new int[4];
        bounds[0] = rect.x;
        bounds[1] = rect.y;
        bounds[2] = rect.x + rect.width;
        bounds[3] = rect.y + rect.height;
        return bounds;
    }

    public CustomImage setClip(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        graphics.setClip(rect);
        return this;
    }

    public CustomImage copyArea(int x, int y, int width, int height, int dx, int dy) {
        graphics.copyArea(x, y, width, height, dx, dy);
        return this;
    }

    public CustomImage drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
        return this;
    }

    public CustomImage drawRect(int x1, int y1, int x2, int y2) {
        graphics.drawRect(x1, y1, x2, y2);
        return this;
    }

    public CustomImage fillRect(int x1, int y1, int x2, int y2) {
        graphics.fillRect(x1, y1, x2, y2);
        return this;
    }

    public CustomImage clearRect(int x1, int y1, int x2, int y2) {
        graphics.clearRect(x1, y1, x2, y2);
        return this;
    }

    public CustomImage clearRect(int x1, int y1, int x2, int y2, int color) {
        Color cached = graphics.getBackground();
        graphics.setBackground(new Color(color));
        graphics.clearRect(x1, y1, x2, y2);
        graphics.setBackground(cached);
        return this;
    }

    public CustomImage drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    public CustomImage fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        return this;
    }

    public CustomImage fillRoundRect(int x, int y, int width, int height, boolean raised) {
        graphics.draw3DRect(x, y, width, height, raised);
        return this;
    }

    public CustomImage fill3DRect(int x, int y, int width, int height, boolean raised) {
        graphics.fill3DRect(x, y, width, height, raised);
        return this;
    }

    public CustomImage drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
        return this;
    }

    public CustomImage fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
        return this;
    }

    public CustomImage drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.drawArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public CustomImage fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle);
        return this;
    }

    public CustomImage drawPolygonLine(int[] pointsX, int[] pointsY) {
        graphics.drawPolyline(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    public CustomImage drawPolygon(int[] pointsX, int[] pointsY) {
        graphics.drawPolygon(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    public CustomImage fillPolygon(int[] pointsX, int[] pointsY) {
        graphics.fillPolygon(pointsX, pointsY, Math.min(pointsX.length, pointsY.length));
        return this;
    }

    public CustomImage drawString(int x, int y, String text) {
        graphics.drawString(text, x, y);
        return this;
    }

    public int getStringWidth(String toAnalyze) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(toAnalyze);
    }

    private static NativeImageBackedTexture createTexture(BufferedImage image) {
        AtomicReference<NativeImageBackedTexture> texture = new AtomicReference<>();
        try {
            final Semaphore semaphore = new Semaphore(0);
            MinecraftClient.getInstance().execute(() -> {
                texture.set(new NativeImageBackedTexture(image.getWidth(), image.getHeight(), true));
                semaphore.release();
            });
            semaphore.acquire();
        } catch (InterruptedException e) {
            Core.getInstance().profile.logError(e);
        }
        return texture.get();
    }

    public static CustomImage createWidget(int width, int height, String name) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return new CustomImage(img, name);
    }

    public static CustomImage createWidget(String path, String name) {
        try {
            File file = JsMacros.core.config.configFolder.toPath().resolve(path).toFile();
            return new CustomImage(ImageIO.read(file), name);
        } catch (IOException e) {
            Core.getInstance().profile.logError(e);
        }
        return null;
    }

    public static int nativeARGBFlip(int color) {
        return ((color & 0x000000FF) << 16) | ((color & (0x00FF0000)) >> 16) | (color & 0xFF00FF00);
    }

}
