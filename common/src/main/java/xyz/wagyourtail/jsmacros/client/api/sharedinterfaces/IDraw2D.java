package xyz.wagyourtail.jsmacros.client.api.sharedinterfaces;

import net.minecraft.client.util.math.MatrixStack;

import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Wagyourtail
 * @since 1.2.7
 * @param <T>
 */
public interface IDraw2D<T> extends RenderCommon.RenderElement {

    @Override
    default void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        render(matrices);
    }
    
    /**
     * @since 1.2.7
     * @return screen width
     */
    int getWidth();
    
    /**
     * @since 1.2.7
     * @return screen height
     */
    int getHeight();
    
    /**
     * @since 1.2.7
     * @return text elements
     */
     @Deprecated
    List<RenderCommon.TextElement> getTexts();
    
    /**
     * @since 1.2.7
     * @return rect elements
     */
     @Deprecated
    List<RenderCommon.RectElement> getRects();

    /**
     * @return all registered line elements.
     *
     * @since 1.8.4
     */
    List<RenderCommon.LineElement> getLines();
    
    /**
     * @since 1.2.7
     * @return item elements
     */
     @Deprecated
    List<RenderCommon.ItemElement> getItems();
    
    /**
     * @since 1.2.7
     * @return image elements
     */
     @Deprecated
    List<RenderCommon.ImageElement> getImages();

    /**
     * @return all registered draw2d elements.
     *
     * @since 1.8.4
     */
    List<RenderCommon.Draw2DElement> getDraw2Ds();
     
    /**
    * @since 1.2.9
     * @return a read only copy of the list of all elements added by scripts.
     */
    List<RenderCommon.RenderElement> getElements();
    
    /**
    * removes any element regardless of type.
    * @since 1.2.9
     * @return self for chaining
     */
    T removeElement(RenderCommon.RenderElement e);
    
    /**
    * re-add an element you removed with {@link #removeElement(RenderCommon.RenderElement)}
    * @since 1.2.9
     * @return self for chaining
     */
    <T extends RenderCommon.RenderElement> T reAddElement(T e);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @return added text
     */
    RenderCommon.TextElement addText(String text, int x, int y, int color, boolean shadow);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     *
     * @return added text
     */
    RenderCommon.TextElement addText(String text, int x, int y, int color, int zIndex, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    RenderCommon.TextElement addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    RenderCommon.TextElement addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @return added text
     */
    RenderCommon.TextElement addText(TextHelper text, int x, int y, int color, boolean shadow);
    
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @return added text
     */
    RenderCommon.TextElement addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow);
    
    /**
     * @since 1.2.7
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    RenderCommon.TextElement addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param text
     * @param x screen x
     * @param y screen y
     * @param color text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @param scale text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     */
    RenderCommon.TextElement addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);
    
    
    /**
     * @since 1.2.7
     * @param t
     * @return self for chaining
     */
     @Deprecated
    T removeText(RenderCommon.TextElement t);
    
    /**
     * @since 1.2.7
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);
    
    /**
     * @since 1.4.0
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param zIndex z-index
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);
    
    /**
     * @since 1.2.7
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation the rotation (clockwise) of the texture (as degrees)
     * @return added image
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);
    
    /**
     * @since 1.4.0
     * @param x screen x, top left corner
     * @param y screen y, top left corner
     * @param width width on screen
     * @param height height on screen
     * @param zIndex z-index
     * @param id image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX the left-most coordinate of the texture region
     * @param imageY the top-most coordinate of the texture region
     * @param regionWidth the width the texture region
     * @param regionHeight the height the texture region
     * @param textureWidth the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation the rotation (clockwise) of the texture (as degrees)
     * @return added image
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param color
     * @param id
     * @param imageX
     * @param imageY
     * @param regionWidth
     * @param regionHeight
     * @param textureWidth
     * @param textureHeight
     * @param rotation
     *
     * @since 1.6.5
     * @return
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

    /**
     * @param x
     * @param y
     * @param width
     * @param height
     * @param zIndex
     * @param alpha
     * @param color
     * @param id
     * @param imageX
     * @param imageY
     * @param regionWidth
     * @param regionHeight
     * @param textureWidth
     * @param textureHeight
     * @param rotation
     *
     * @since 1.6.5
     * @return
     */
    RenderCommon.ImageElement addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

    /**
     * @since 1.2.7
     * @param i
     * @return self for chaining
     */
     @Deprecated
    T removeImage(RenderCommon.ImageElement i);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex, with alpha channel
     * @return added rect
     */
    RenderCommon.RectElement addRect(int x1, int y1, int x2, int y2, int color);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @return added rect
     */
    RenderCommon.RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha);
    
    /**
     * @since 1.2.7
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @param rotation as degrees
     * @return added rect
     */
    RenderCommon.RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation);
    
    /**
     * @since 1.4.0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @param rotation as degrees
     * @param zIndex z-index
     * @return added rect
     */
    RenderCommon.RectElement addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex);
    
    /**
     * @since 1.2.7
     * @param r
     * @return self for chaining
     */
     @Deprecated
    T removeRect(RenderCommon.RectElement r);

    /**
     * @param x1    the x position of the start
     * @param y1    the y position of the start
     * @param x2    the x position of the end
     * @param y2    the y position of the end
     * @param color the color of the line, can include alpha value
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color);

    /**
     * @param x1     the x position of the start
     * @param y1     the y position of the start
     * @param x2     the x position of the end
     * @param y2     the y position of the end
     * @param color  the color of the line, can include alpha value
     * @param zIndex the z-index of the line
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex);

    /**
     * @param x1    the x position of the start
     * @param y1    the y position of the start
     * @param x2    the x position of the end
     * @param y2    the y position of the end
     * @param color the color of the line, can include alpha value
     * @param width the width of the line
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color, double width);

    /**
     * @param x1     the x position of the start
     * @param y1     the y position of the start
     * @param x2     the x position of the end
     * @param y2     the y position of the end
     * @param color  the color of the line, can include alpha value
     * @param zIndex the z-index of the line
     * @param width  the width of the line
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width);

    /**
     * @param x1       the x position of the start
     * @param y1       the y position of the start
     * @param x2       the x position of the end
     * @param y2       the y position of the end
     * @param color    the color of the line, can include alpha value
     * @param width    the width of the line
     * @param rotation the rotation (clockwise) of the line (as degrees)
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color, double width, double rotation);

    /**
     * @param x1       the x position of the start
     * @param y1       the y position of the start
     * @param x2       the x position of the end
     * @param y2       the y position of the end
     * @param color    the color of the line, can include alpha value
     * @param zIndex   the z-index of the line
     * @param width    the width of the line
     * @param rotation the rotation (clockwise) of the line (as degrees)
     * @return the added line.
     *
     * @since 1.8.4
     */
    RenderCommon.LineElement addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width, double rotation);

    /**
     * @param l the line to remove
     * @return self chaining.
     *
     * @since 1.8.4
     */
    T removeLine(RenderCommon.LineElement l);
     
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, String id);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, String id);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @param overlay should include overlay health and count
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, String id, boolean overlay);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @param overlay should include overlay health and count
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, String id, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param id item id
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, String id, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param id item id
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, ItemStackHelper item);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay);
    
    /**
     * @since 1.2.7
     * @param x left most corner
     * @param y top most corner
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.4.0
     * @param x left most corner
     * @param y top most corner
     * @param zIndex z-index
     * @param item from inventory as helper
     * @param overlay should include overlay health and count
     * @param scale scale of item
     * @param rotation rotation of item
     * @return added item
     */
    RenderCommon.ItemElement addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation);
    
    /**
     * @since 1.2.7
     * @param i
     * @return self for chaining
     */
     @Deprecated
    T removeItem(RenderCommon.ItemElement i);

    /**
     * Tries to add the given draw2d as a child. Fails if cyclic dependencies are detected.
     *
     * @param draw2D the draw2d to add
     * @param x      the x position on this draw2d
     * @param y      the y position on this draw2d
     * @param width  the width of the given draw2d
     * @param height the height of the given draw2d
     * @return a wrapper for the draw2d.
     *
     * @since 1.8.4
     */
    RenderCommon.Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height);

    /**
     * Tries to add the given draw2d as a child. Fails if cyclic dependencies are detected.
     *
     * @param draw2D the draw2d to add
     * @param x      the x position on this draw2d
     * @param y      the y position on this draw2d
     * @param width  the width of the given draw2d
     * @param height the height of the given draw2d
     * @param zIndex the z-index for the draw2d
     * @return a wrapper for the draw2d.
     *
     * @since 1.8.4
     */
    RenderCommon.Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex);

    /**
     * @param draw2D the draw2d to remove
     * @return self chaining.
     *
     * @since 1.8.4
     */
    T removeDraw2D(RenderCommon.Draw2DElement draw2D);

    /**
     * @return a builder for an {@link RenderCommon.ItemElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.ItemElement.Builder itemBuilder() {
        return new RenderCommon.ItemElement.Builder(this);
    }

    /**
     * @param item the item to use
     * @return a builder for an {@link RenderCommon.ItemElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.ItemElement.Builder itemBuilder(ItemStackHelper item) {
        return new RenderCommon.ItemElement.Builder(this).item(item);
    }

    /**
     * @return a builder for an {@link RenderCommon.ImageElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.ImageElement.Builder imageBuilder() {
        return new RenderCommon.ImageElement.Builder(this);
    }

    /**
     * @param id the id of the image
     * @return a builder for an {@link RenderCommon.ImageElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.ImageElement.Builder imageBuilder(String id) {
        return new RenderCommon.ImageElement.Builder(this).identifier(id);
    }

    /**
     * @return a builder for a {@link RenderCommon.RectElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.RectElement.Builder rectBuilder() {
        return new RenderCommon.RectElement.Builder(this);
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return a builder for a {@link RenderCommon.RectElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.RectElement.Builder rectBuilder(int x, int y, int width, int height) {
        return new RenderCommon.RectElement.Builder(this).size(width, height).pos1(x, y);
    }

    /**
     * @return a builder for a {@link RenderCommon.LineElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.LineElement.Builder lineBuilder() {
        return new RenderCommon.LineElement.Builder(this);
    }

    /**
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return a builder for a {@link RenderCommon.LineElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.LineElement.Builder lineBuilder(int x1, int y1, int x2, int y2) {
        return new RenderCommon.LineElement.Builder(this).pos(x1, y1, x2, y2);
    }
    
    /**
     * @return a builder for a {@link RenderCommon.TextElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.TextElement.Builder textBuilder() {
        return new RenderCommon.TextElement.Builder(this);
    }

    /**
     * @param text the text to display
     * @return a builder for a {@link RenderCommon.TextElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.TextElement.Builder textBuilder(String text) {
        return new RenderCommon.TextElement.Builder(this).text(text);
    }

    /**
     * @param text the text to display
     * @return a builder for a {@link RenderCommon.TextElement}.
     *
     * @since 1.8.4
     */
    default RenderCommon.TextElement.Builder textBuilder(TextHelper text) {
        return new RenderCommon.TextElement.Builder(this).text(text);
    }

    /**
     * @param draw2D the draw2d to add
     * @return a builder for a {@link Draw2D}.
     *
     * @since 1.8.4
     */
    default RenderCommon.Draw2DElement.Builder draw2DBuilder(Draw2D draw2D) {
        return new RenderCommon.Draw2DElement.Builder(this, draw2D);
    }
    
    /**
     * @since 1.2.7
     * @param onInit calls your method as a {@link Consumer}&lt;{@link T}&gt;
     * @return self for chaining
     */
    T setOnInit(MethodWrapper<T, Object, Object, ?> onInit);
    
    /**
     * @since 1.2.7
     * @param catchInit calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return self for chaining
     */
    T setOnFailInit(MethodWrapper<String, Object, Object, ?> catchInit);

    /**
     * internal
     * @param matrixStack
     */
    void render(MatrixStack matrixStack);
}
