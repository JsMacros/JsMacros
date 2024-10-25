package xyz.wagyourtail.jsmacros.client.api.classes.render;

import net.minecraft.client.gui.DrawContext;
import xyz.wagyourtail.doclet.DocletIgnore;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.*;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.function.Consumer;

/**
 * @param <T>
 * @author Wagyourtail
 * @since 1.2.7
 */
public interface IDraw2D<T> {

    /**
     * @return screen width
     * @since 1.2.7
     */
    int getWidth();

    /**
     * @return screen height
     * @since 1.2.7
     */
    int getHeight();

    /**
     * @return text elements
     * @since 1.2.7
     */
    @Deprecated
    List<Text> getTexts();

    /**
     * @return rect elements
     * @since 1.2.7
     */
    @Deprecated
    List<Rect> getRects();

    /**
     * @return all registered line elements.
     * @since 1.8.4
     */
    List<Line> getLines();

    /**
     * @return item elements
     * @since 1.2.7
     */
    @Deprecated
    List<Item> getItems();

    /**
     * @return image elements
     * @since 1.2.7
     */
    @Deprecated
    List<Image> getImages();

    /**
     * @return all registered draw2d elements.
     * @since 1.8.4
     */
    List<Draw2DElement> getDraw2Ds();

    /**
     * @return a read only copy of the list of all elements added by scripts.
     * @since 1.2.9
     */
    List<RenderElement> getElements();

    /**
     * removes any element regardless of type.
     *
     * @return self for chaining
     * @since 1.2.9
     */
    T removeElement(RenderElement e);

    /**
     * re-add an element you removed with {@link #removeElement(RenderElement)}
     *
     * @return self for chaining
     * @since 1.2.9
     */
    <T extends RenderElement> T reAddElement(T e);

    /**
     * @param text
     * @param x      screen x
     * @param y      screen y
     * @param color  text color
     * @param shadow include shadow layer
     * @return added text
     * @since 1.2.7
     */
    Text addText(String text, int x, int y, int color, boolean shadow);

    /**
     * @param text
     * @param x      screen x
     * @param y      screen y
     * @param color  text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @return added text
     * @since 1.4.0
     */
    Text addText(String text, int x, int y, int color, int zIndex, boolean shadow);

    /**
     * @param text
     * @param x        screen x
     * @param y        screen y
     * @param color    text color
     * @param shadow   include shadow layer
     * @param scale    text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     * @since 1.2.7
     */
    Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation);

    /**
     * @param text
     * @param x        screen x
     * @param y        screen y
     * @param color    text color
     * @param zIndex   z-index
     * @param shadow   include shadow layer
     * @param scale    text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     * @since 1.4.0
     */
    Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);

    /**
     * @param text
     * @param x      screen x
     * @param y      screen y
     * @param color  text color
     * @param shadow include shadow layer
     * @return added text
     * @since 1.2.7
     */
    Text addText(TextHelper text, int x, int y, int color, boolean shadow);

    /**
     * @param text
     * @param x      screen x
     * @param y      screen y
     * @param color  text color
     * @param zIndex z-index
     * @param shadow include shadow layer
     * @return added text
     * @since 1.4.0
     */
    Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow);

    /**
     * @param text
     * @param x        screen x
     * @param y        screen y
     * @param color    text color
     * @param shadow   include shadow layer
     * @param scale    text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     * @since 1.2.7
     */
    Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation);

    /**
     * @param text
     * @param x        screen x
     * @param y        screen y
     * @param color    text color
     * @param zIndex   z-index
     * @param shadow   include shadow layer
     * @param scale    text scale (as double)
     * @param rotation text rotation (as degrees)
     * @return added text
     * @since 1.4.0
     */
    Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation);

    /**
     * @param t
     * @return self for chaining
     * @since 1.2.7
     */
    @Deprecated
    T removeText(Text t);

    /**
     * @param x             screen x, top left corner
     * @param y             screen y, top left corner
     * @param width         width on screen
     * @param height        height on screen
     * @param id            image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX        the left-most coordinate of the texture region
     * @param imageY        the top-most coordinate of the texture region
     * @param regionWidth   the width the texture region
     * @param regionHeight  the height the texture region
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     * @since 1.2.7
     */
    Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);

    /**
     * @param x             screen x, top left corner
     * @param y             screen y, top left corner
     * @param width         width on screen
     * @param height        height on screen
     * @param zIndex        z-index
     * @param id            image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX        the left-most coordinate of the texture region
     * @param imageY        the top-most coordinate of the texture region
     * @param regionWidth   the width the texture region
     * @param regionHeight  the height the texture region
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @return added image
     * @since 1.4.0
     */
    Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight);

    /**
     * @param x             screen x, top left corner
     * @param y             screen y, top left corner
     * @param width         width on screen
     * @param height        height on screen
     * @param id            image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX        the left-most coordinate of the texture region
     * @param imageY        the top-most coordinate of the texture region
     * @param regionWidth   the width the texture region
     * @param regionHeight  the height the texture region
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation      the rotation (clockwise) of the texture (as degrees)
     * @return added image
     * @since 1.2.7
     */
    Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

    /**
     * @param x             screen x, top left corner
     * @param y             screen y, top left corner
     * @param width         width on screen
     * @param height        height on screen
     * @param zIndex        z-index
     * @param id            image id, in the form {@code minecraft:textures} path'd as found in texture packs, ie {@code assets/minecraft/textures/gui/recipe_book.png} becomes {@code minecraft:textures/gui/recipe_book.png}
     * @param imageX        the left-most coordinate of the texture region
     * @param imageY        the top-most coordinate of the texture region
     * @param regionWidth   the width the texture region
     * @param regionHeight  the height the texture region
     * @param textureWidth  the width of the entire texture
     * @param textureHeight the height of the entire texture
     * @param rotation      the rotation (clockwise) of the texture (as degrees)
     * @return added image
     * @since 1.4.0
     */
    Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

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
     * @return
     * @since 1.6.5
     */
    Image addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

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
     * @return
     * @since 1.6.5
     */
    Image addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation);

    /**
     * @param i
     * @return self for chaining
     * @since 1.2.7
     */
    @Deprecated
    T removeImage(Image i);

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex, with alpha channel
     * @return added rect
     * @since 1.2.7
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color);

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color as hex
     * @param alpha alpha channel 0-255
     * @return added rect
     * @since 1.2.7
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha);

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color    as hex
     * @param alpha    alpha channel 0-255
     * @param rotation as degrees
     * @return added rect
     * @since 1.2.7
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation);

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param color    as hex
     * @param alpha    alpha channel 0-255
     * @param rotation as degrees
     * @param zIndex   z-index
     * @return added rect
     * @since 1.4.0
     */
    Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex);

    /**
     * @param r
     * @return self for chaining
     * @since 1.2.7
     */
    @Deprecated
    T removeRect(Rect r);

    /**
     * @param x1    the x position of the start
     * @param y1    the y position of the start
     * @param x2    the x position of the end
     * @param y2    the y position of the end
     * @param color the color of the line, can include alpha value
     * @return the added line.
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color);

    /**
     * @param x1     the x position of the start
     * @param y1     the y position of the start
     * @param x2     the x position of the end
     * @param y2     the y position of the end
     * @param color  the color of the line, can include alpha value
     * @param zIndex the z-index of the line
     * @return the added line.
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex);

    /**
     * @param x1    the x position of the start
     * @param y1    the y position of the start
     * @param x2    the x position of the end
     * @param y2    the y position of the end
     * @param color the color of the line, can include alpha value
     * @param width the width of the line
     * @return the added line.
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color, double width);

    /**
     * @param x1     the x position of the start
     * @param y1     the y position of the start
     * @param x2     the x position of the end
     * @param y2     the y position of the end
     * @param color  the color of the line, can include alpha value
     * @param zIndex the z-index of the line
     * @param width  the width of the line
     * @return the added line.
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width);

    /**
     * @param x1       the x position of the start
     * @param y1       the y position of the start
     * @param x2       the x position of the end
     * @param y2       the y position of the end
     * @param color    the color of the line, can include alpha value
     * @param width    the width of the line
     * @param rotation the rotation (clockwise) of the line (as degrees)
     * @return the added line.
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color, double width, double rotation);

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
     * @since 1.8.4
     */
    Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width, double rotation);

    /**
     * @param l the line to remove
     * @return self chaining.
     * @since 1.8.4
     */
    T removeLine(Line l);

    /**
     * @param x  left most corner
     * @param y  top most corner
     * @param id item id
     * @return added item
     * @since 1.2.7
     */
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>")
    Item addItem(int x, int y, String id);

    /**
     * @param x      left most corner
     * @param y      top most corner
     * @param zIndex z-index
     * @param id     item id
     * @return added item
     * @since 1.4.0
     */
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>")
    Item addItem(int x, int y, int zIndex, String id);

    /**
     * @param x       left most corner
     * @param y       top most corner
     * @param id      item id
     * @param overlay should include overlay health and count
     * @return added item
     * @since 1.2.7
     */
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>, overlay: boolean")
    Item addItem(int x, int y, String id, boolean overlay);

    /**
     * @param x       left most corner
     * @param y       top most corner
     * @param zIndex  z-index
     * @param id      item id
     * @param overlay should include overlay health and count
     * @return added item
     * @since 1.4.0
     */
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>, overlay: boolean")
    Item addItem(int x, int y, int zIndex, String id, boolean overlay);

    /**
     * @param x        left most corner
     * @param y        top most corner
     * @param id       item id
     * @param overlay  should include overlay health and count
     * @param scale    scale of item
     * @param rotation rotation of item
     * @return added item
     * @since 1.2.7
     */
    @DocletReplaceParams("x: int, y: int, id: CanOmitNamespace<ItemId>, overlay: boolean, scale: double, rotation: double")
    Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation);

    /**
     * @param x        left most corner
     * @param y        top most corner
     * @param zIndex   z-index
     * @param id       item id
     * @param overlay  should include overlay health and count
     * @param scale    scale of item
     * @param rotation rotation of item
     * @return added item
     * @since 1.4.0
     */
    @DocletReplaceParams("x: int, y: int, zIndex: int, id: CanOmitNamespace<ItemId>, overlay: boolean, scale: double, rotation: double")
    Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation);

    /**
     * @param x    left most corner
     * @param y    top most corner
     * @param item from inventory as helper
     * @return added item
     * @since 1.2.7
     */
    Item addItem(int x, int y, ItemStackHelper item);

    /**
     * @param x      left most corner
     * @param y      top most corner
     * @param zIndex z-index
     * @param item   from inventory as helper
     * @return added item
     * @since 1.4.0
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item);

    /**
     * @param x       left most corner
     * @param y       top most corner
     * @param item    from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     * @since 1.2.7
     */
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay);

    /**
     * @param x       left most corner
     * @param y       top most corner
     * @param zIndex  z-index
     * @param item    from inventory as helper
     * @param overlay should include overlay health and count
     * @return added item
     * @since 1.4.0
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay);

    /**
     * @param x        left most corner
     * @param y        top most corner
     * @param item     from inventory as helper
     * @param overlay  should include overlay health and count
     * @param scale    scale of item
     * @param rotation rotation of item
     * @return added item
     * @since 1.2.7
     */
    Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation);

    /**
     * @param x        left most corner
     * @param y        top most corner
     * @param zIndex   z-index
     * @param item     from inventory as helper
     * @param overlay  should include overlay health and count
     * @param scale    scale of item
     * @param rotation rotation of item
     * @return added item
     * @since 1.4.0
     */
    Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation);

    /**
     * @param i
     * @return self for chaining
     * @since 1.2.7
     */
    @Deprecated
    T removeItem(Item i);

    /**
     * Tries to add the given draw2d as a child. Fails if cyclic dependencies are detected.
     *
     * @param draw2D the draw2d to add
     * @param x      the x position on this draw2d
     * @param y      the y position on this draw2d
     * @param width  the width of the given draw2d
     * @param height the height of the given draw2d
     * @return a wrapper for the draw2d.
     * @since 1.8.4
     */
    Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height);

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
     * @since 1.8.4
     */
    Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex);

    /**
     * @param draw2D the draw2d to remove
     * @return self chaining.
     * @since 1.8.4
     */
    T removeDraw2D(Draw2DElement draw2D);

    /**
     * @return a builder for an {@link Item}.
     * @since 1.8.4
     */
    default Item.Builder itemBuilder() {
        return new Item.Builder(this);
    }

    /**
     * @param item the item to use
     * @return a builder for an {@link Item}.
     * @since 1.8.4
     */
    default Item.Builder itemBuilder(ItemStackHelper item) {
        return new Item.Builder(this).item(item);
    }

    /**
     * @return a builder for an {@link Image}.
     * @since 1.8.4
     */
    default Image.Builder imageBuilder() {
        return new Image.Builder(this);
    }

    /**
     * @param id the id of the image
     * @return a builder for an {@link Image}.
     * @since 1.8.4
     */
    default Image.Builder imageBuilder(String id) {
        return new Image.Builder(this).identifier(id);
    }

    /**
     * @return a builder for a {@link Rect}.
     * @since 1.8.4
     */
    default Rect.Builder rectBuilder() {
        return new Rect.Builder(this);
    }

    /**
     * @param x      the x position of the rectangle
     * @param y      the y position of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return a builder for a {@link Rect}.
     * @since 1.8.4
     */
    default Rect.Builder rectBuilder(int x, int y, int width, int height) {
        return new Rect.Builder(this).size(width, height).pos1(x, y);
    }

    /**
     * @return a builder for a {@link Line}.
     * @since 1.8.4
     */
    default Line.Builder lineBuilder() {
        return new Line.Builder(this);
    }

    /**
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return a builder for a {@link Line}.
     * @since 1.8.4
     */
    default Line.Builder lineBuilder(int x1, int y1, int x2, int y2) {
        return new Line.Builder(this).pos(x1, y1, x2, y2);
    }

    /**
     * @return a builder for a {@link Text}.
     * @since 1.8.4
     */
    default Text.Builder textBuilder() {
        return new Text.Builder(this);
    }

    /**
     * @param text the text to display
     * @return a builder for a {@link Text}.
     * @since 1.8.4
     */
    default Text.Builder textBuilder(String text) {
        return new Text.Builder(this).text(text);
    }

    /**
     * @param text the text to display
     * @return a builder for a {@link Text}.
     * @since 1.8.4
     */
    default Text.Builder textBuilder(TextHelper text) {
        return new Text.Builder(this).text(text);
    }

    /**
     * @param draw2D the draw2d to add
     * @return a builder for a {@link Draw2D}.
     * @since 1.8.4
     */
    default Draw2DElement.Builder draw2DBuilder(Draw2D draw2D) {
        return new Draw2DElement.Builder(this, draw2D);
    }

    /**
     * @param onInit calls your method as a {@link Consumer}&lt;{@link T}&gt;
     * @return self for chaining
     * @since 1.2.7
     */
    T setOnInit(MethodWrapper<T, Object, Object, ?> onInit);

    /**
     * @param catchInit calls your method as a {@link Consumer}&lt;{@link String}&gt;
     * @return self for chaining
     * @since 1.2.7
     */
    T setOnFailInit(MethodWrapper<String, Object, Object, ?> catchInit);

    /**
     * internal
     *
     * @param drawContext
     */
    @DocletIgnore
    void render(DrawContext drawContext);

    /**
     * @param zIndex
     * @since 1.8.4
     */
    void setZIndex(int zIndex);

    /**
     * @return
     * @since 1.8.4
     */
    int getZIndex();

}
