package xyz.wagyourtail.jsmacros.client.mixin.access;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.access.CustomClickEvent;
import xyz.wagyourtail.jsmacros.api.math.Pos2D;
import xyz.wagyourtail.jsmacros.api.math.Vec2D;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.classes.render.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.render.components.*;
import xyz.wagyourtail.jsmacros.client.api.helper.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helper.screen.*;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.wagyourgui.elements.Slider;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Screen.class)
@Implements(@Interface(iface = IScreen.class, prefix = "soft$"))
public abstract class MixinScreen extends AbstractParentElement implements IScreen, IScreenInternal {
    @Unique
    private final Set<RenderElement> elements = new LinkedHashSet<>();
    @Unique
    @Nullable
    private MethodWrapper<Pos2D, Integer, Object, ?> onMouseDown;
    @Unique
    @Nullable
    private MethodWrapper<Vec2D, Integer, Object, ?> onMouseDrag;
    @Unique
    @Nullable
    private MethodWrapper<Pos2D, Integer, Object, ?> onMouseUp;
    @Unique
    @Nullable
    private MethodWrapper<Pos2D, Pos2D, Object, ?> onScroll;
    @Unique
    @Nullable
    private MethodWrapper<Integer, Integer, Object, ?> onKeyPressed;
    @Unique
    @Nullable
    private MethodWrapper<Character, Integer, Object, ?> onCharTyped;
    @Unique
    @Nullable
    private MethodWrapper<IScreen, Object, Object, ?> onInit;
    @Unique
    @Nullable
    private MethodWrapper<String, Object, Object, ?> catchInit;
    @Unique
    @Nullable
    private MethodWrapper<IScreen, Object, Object, ?> onClose;

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    @Final
    protected Text title;
    @Shadow
    public MinecraftClient client;
    @Shadow
    public TextRenderer textRenderer;

    @Inject(method = "handleTextClick", at = @At("HEAD"), cancellable = true)
    private void onHandleTextClick(Style style, CallbackInfoReturnable<Boolean> cir) {
        handleCustomClickEvent(style, cir);
    }

    @Shadow(aliases = {"method_37063", "m_142416_"})
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Shadow(aliases = {"close", "method_25419", "m_7379_"})
    public abstract void onClose();

    @Shadow
    protected abstract void init();

    @Shadow
    public abstract void tick();

    @Shadow
    @Final
    private List<Element> children;

    @Shadow
    public abstract boolean handleTextClick(@Nullable Style style);

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<Draw2DElement> getDraw2Ds() {
        List<Draw2DElement> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Draw2DElement) {
                    list.add((Draw2DElement) e);
                }
            }
        }
        return list;
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height) {
        return addDraw2D(draw2D, x, y, width, height, 0);
    }

    @Override
    public Draw2DElement addDraw2D(Draw2D draw2D, int x, int y, int width, int height, int zIndex) {
        if (draw2D == null) {
            return null;
        }
        Draw2DElement d = draw2DBuilder(draw2D).pos(x, y).size(width, height).zIndex(zIndex).build();
        synchronized (elements) {
            elements.add(d);
        }
        return d;
    }

    @Override
    public IScreen removeDraw2D(Draw2DElement draw2D) {
        synchronized (elements) {
            elements.remove(draw2D);
        }
        return this;
    }

    @Override
    public List<Line> getLines() {
        List<Line> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Line) {
                    list.add((Line) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text> getTexts() {
        List<xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) {
                    list.add((xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<Rect> getRects() {
        List<Rect> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Rect) {
                    list.add((Rect) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<Item> getItems() {
        List<Item> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Item) {
                    list.add((Item) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<Image> getImages() {
        List<Image> list = new LinkedList<>();
        synchronized (elements) {
            for (Drawable e : elements) {
                if (e instanceof Image) {
                    list.add((Image) e);
                }
            }
        }
        return list;
    }

    @Override
    public List<TextFieldWidgetHelper> getTextFields() {
        Map<TextFieldWidget, TextFieldWidgetHelper> btns = new LinkedHashMap<>();
        for (RenderElement el : elements) {
            if (el instanceof TextFieldWidgetHelper) {
                btns.put(((TextFieldWidgetHelper) el).getRaw(), (TextFieldWidgetHelper) el);
            }
        }
        synchronized (children) {
            for (Element e : children) {
                if (e instanceof TextFieldWidget && !btns.containsKey(e)) {
                    btns.put((TextFieldWidget) e, new TextFieldWidgetHelper((TextFieldWidget) e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }

    @Override
    public List<ClickableWidgetHelper<?, ?>> getButtonWidgets() {
        Map<ClickableWidget, ClickableWidgetHelper<?, ?>> btns = new LinkedHashMap<>();
        for (RenderElement el : elements) {
            if (el instanceof ClickableWidgetHelper) {
                btns.put(((ClickableWidgetHelper<?, ?>) el).getRaw(), (ClickableWidgetHelper<?, ?>) el);
            }
        }
        synchronized (children) {
            for (Element e : children) {
                if ((e instanceof ButtonWidget) && !btns.containsKey(e)) {
                    btns.put((ClickableWidget) e, new ClickableWidgetHelper<>((ClickableWidget) e));
                }
            }
        }
        return ImmutableList.copyOf(btns.values());
    }

    @Override
    public List<RenderElement> getElements() {
        return ImmutableList.copyOf(elements);
    }

    @Override
    public IScreen removeElement(RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
            if (e instanceof ClickableWidgetHelper) {
                children.remove(((ClickableWidgetHelper<?, ?>) e).getRaw());
            }
        }
        return this;
    }

    @Override
    public <T extends RenderElement> T reAddElement(T e) {
        synchronized (elements) {
            elements.add(e);
            if (e instanceof ClickableWidgetHelper) {
                children.add(((ClickableWidgetHelper<?, ?>) e).getRaw());
            }
        }
        return e;
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text t = new xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text t = new xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }

    @Override
    public IScreen removeText(xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    @Override
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
                          int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }

    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(x, y, width, height, zIndex, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, 0);
    }

    @Override
    public Image addImage(int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
                          int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, 0, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.4.0
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, zIndex, 0xFFFFFFFF, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.6.5
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        Image i = new Image(x, y, width, height, zIndex, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    /**
     * @see IDraw2D#addImage(int, int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     * @since 1.6.5
     */
    @Override
    public Image addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        Image i = new Image(x, y, width, height, zIndex, alpha, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public IScreen removeImage(Image i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color) {
        Rect r = new Rect(x1, y1, x2, y2, color, 0F, 0).setParent(this);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }

    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, rotation, 0);
    }

    @Override
    public Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        Rect r = new Rect(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex).setParent(this);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeRect(Rect r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color) {
        return addLine(x1, y1, x2, y2, color, 0);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex) {
        return addLine(x1, y1, x2, y2, color, zIndex, 1);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, double width) {
        return addLine(x1, y1, x2, y2, color, 0, width);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width) {
        return addLine(x1, y1, x2, y2, color, zIndex, width, 0);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, double width, double rotation) {
        return addLine(x1, y1, x2, y2, color, 0, width, rotation);
    }

    @Override
    public Line addLine(int x1, int y1, int x2, int y2, int color, int zIndex, double width, double rotation) {
        Line r = new Line(x1, y1, x2, y2, color, (float) rotation, (float) width, zIndex).setParent(this);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeLine(Line l) {
        synchronized (elements) {
            elements.remove(l);
        }
        return this;
    }

    @Override
    public Item addItem(int x, int y, String id) {
        return addItem(x, y, 0, id, true, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, String id) {
        return addItem(x, y, zIndex, id, true, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        Item i = new Item(x, y, zIndex, id, overlay, scale, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public Item addItem(int x, int y, ItemStackHelper item) {
        return addItem(x, y, 0, item, true, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return addItem(x, y, zIndex, item, true, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, 0, item, overlay, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }

    @Override
    public Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }

    @Override
    public Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        Item i = new Item(x, y, zIndex, item, overlay, scale, (float) rotation).setParent(this);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public IScreen removeItem(Item i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public String getScreenClassName() {
        return IScreen.super.getScreenClassName();
    }

    @Override
    public TextHelper getTitleText() {
        return TextHelper.wrap(title);
    }

    @Override
    public ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, String text,
                                                 MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback) {
        return addButton(x, y, width, height, 0, text, callback);
    }

    @Override
    public ClickableWidgetHelper<?, ?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ClickableWidgetHelper<?, ?>, IScreen, Object, ?> callback) {
        AtomicReference<ClickableWidgetHelper<?, ?>> b = new AtomicReference<>(null);
        ButtonWidget button = ButtonWidget.builder(Text.literal(text), (btn) -> {
            try {
                callback.accept(b.get(), this);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
            ClickableWidgetHelper.clickedOn(this);
        }).position(x, y).size(width, height).build();
        b.set(new ClickableWidgetHelper<>(button, zIndex));
        synchronized (elements) {
            elements.add(b.get());
            children.add(button);
        }
        return b.get();
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, 0, text, checked, showMessage, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, 0, text, checked, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        return addCheckbox(x, y, width, height, zIndex, text, checked, true, callback);
    }

    @Override
    public CheckBoxWidgetHelper addCheckbox(int x, int y, int width, int height, int zIndex, String text, boolean checked, boolean showMessage, MethodWrapper<CheckBoxWidgetHelper, IScreen, Object, ?> callback) {
        AtomicReference<CheckBoxWidgetHelper> ref = new AtomicReference<>(null);

        CheckboxWidget checkbox = CheckboxWidget.builder(Text.literal(text), textRenderer).callback((btn, value) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }).pos(x, y).checked(checked).build();

        checkbox.setWidth(width);
        checkbox.setHeight(height);

        ref.set(new CheckBoxWidgetHelper(checkbox, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(checkbox);
        }
        return ref.get();
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, int steps, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        return addSlider(x, y, width, height, 0, text, value, steps, callback);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        return addSlider(x, y, width, height, zIndex, text, value, Integer.MAX_VALUE, callback);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, String text, double value, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        return addSlider(x, y, width, height, 0, text, value, callback);
    }

    @Override
    public SliderWidgetHelper addSlider(int x, int y, int width, int height, int zIndex, String text, double value, int steps, MethodWrapper<SliderWidgetHelper, IScreen, Object, ?> callback) {
        AtomicReference<SliderWidgetHelper> ref = new AtomicReference<>(null);

        Slider slider = new Slider(x, y, width, height, net.minecraft.text.Text.literal(text), value, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }, steps);

        ref.set(new SliderWidgetHelper(slider, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(slider);
        }
        return ref.get();
    }

    @Override
    public LockButtonWidgetHelper addLockButton(int x, int y, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback) {
        return addLockButton(x, y, 0, callback);
    }

    @Override
    public LockButtonWidgetHelper addLockButton(int x, int y, int zIndex, MethodWrapper<LockButtonWidgetHelper, IScreen, Object, ?> callback) {
        AtomicReference<LockButtonWidgetHelper> ref = new AtomicReference<>(null);
        LockButtonWidget lockButton = new LockButtonWidget(x, y, (btn) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
            ClickableWidgetHelper.clickedOn(this);
        });
        ref.set(new LockButtonWidgetHelper(lockButton, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(lockButton);
        }
        return ref.get();
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, String[] values, String initial, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback) {
        return addCyclingButton(x, y, width, height, 0, values, initial, callback);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String initial, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback) {
        return addCyclingButton(x, y, width, height, 0, values, null, initial, null, callback);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback) {
        return addCyclingButton(x, y, width, height, 0, values, alternatives, initial, prefix, null, callback);
    }

    @Override
    public CyclingButtonWidgetHelper<?> addCyclingButton(int x, int y, int width, int height, int zIndex, String[] values, String[] alternatives, String initial, String prefix, MethodWrapper<?, ?, Boolean, ?> alternateToggle, MethodWrapper<CyclingButtonWidgetHelper<?>, IScreen, Object, ?> callback) {
        AtomicReference<CyclingButtonWidgetHelper<?>> ref = new AtomicReference<>(null);
        CyclingButtonWidget<String> cyclingButton;
        CyclingButtonWidget.Builder<String> builder = CyclingButtonWidget.builder(net.minecraft.text.Text::literal);
        if (alternatives != null) {
            BooleanSupplier supplier = alternateToggle == null ? Screen::hasAltDown : alternateToggle::get;
            builder.values(supplier, Arrays.asList(values), Arrays.asList(alternatives));
        } else {
            builder.values(values);
        }
        builder.initially(initial);

        if (prefix == null || StringUtils.isBlank(prefix)) {
            builder.omitKeyText();
        }

        cyclingButton = builder.build(x, y, width, height, net.minecraft.text.Text.literal(prefix), (btn, val) -> {
            try {
                callback.accept(ref.get(), this);
            } catch (Exception e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
            ClickableWidgetHelper.clickedOn(this);
        });
        ref.set(new CyclingButtonWidgetHelper<>(cyclingButton, zIndex));
        synchronized (elements) {
            elements.add(ref.get());
            children.add(cyclingButton);
        }
        return ref.get();
    }

    @Override
    public IScreen removeButton(ClickableWidgetHelper<?, ?> btn) {
        synchronized (elements) {
            elements.remove(btn);
            this.children.remove(btn.getRaw());
        }
        return this;
    }

    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, String message,
                                              MethodWrapper<String, IScreen, Object, ?> onChange) {
        return addTextInput(x, y, width, height, 0, message, onChange);
    }

    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object, ?> onChange) {
        TextFieldWidget field = new TextFieldWidget(this.textRenderer, x, y, width, height, net.minecraft.text.Text.literal(message));
        if (onChange != null) {
            field.setChangedListener(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Throwable e) {
                    JsMacrosClient.clientCore.profile.logError(e);
                }
            });
        }
        TextFieldWidgetHelper w = new TextFieldWidgetHelper(field, zIndex);
        synchronized (elements) {
            elements.add(w);
            children.add(field);
        }
        return w;
    }

    @Override
    public IScreen removeTextInput(TextFieldWidgetHelper inp) {
        synchronized (elements) {
            elements.remove(inp);
            children.remove(inp.getRaw());
        }
        return this;
    }

    @Intrinsic
    public void soft$close() {
        onClose();
    }

    @Override
    public IScreen setOnMouseDown(@Nullable MethodWrapper<Pos2D, Integer, Object, ?> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    @Override
    public IScreen setOnMouseDrag(@Nullable MethodWrapper<Vec2D, Integer, Object, ?> onMouseDrag) {
        this.onMouseDrag = onMouseDrag;
        return this;
    }

    @Override
    public IScreen setOnMouseUp(@Nullable MethodWrapper<Pos2D, Integer, Object, ?> onMouseUp) {
        this.onMouseUp = onMouseUp;
        return this;
    }

    @Override
    public IScreen setOnScroll(@Nullable MethodWrapper<Pos2D, Pos2D, Object, ?> onScroll) {
        this.onScroll = onScroll;
        return this;
    }

    @Override
    public IScreen setOnKeyPressed(@Nullable MethodWrapper<Integer, Integer, Object, ?> onKeyPressed) {
        this.onKeyPressed = onKeyPressed;
        return this;
    }

    @Override
    public IScreen setOnCharTyped(@Nullable MethodWrapper<Character, Integer, Object, ?> onCharTyped) {
        this.onCharTyped = onCharTyped;
        return this;
    }

    @Override
    public IScreen setOnInit(@Nullable MethodWrapper<IScreen, Object, Object, ?> onInit) {
        this.onInit = onInit;
        return this;
    }

    @Override
    public IScreen setOnFailInit(@Nullable MethodWrapper<String, Object, Object, ?> catchInit) {
        this.catchInit = catchInit;
        return this;
    }

    @Override
    public IScreen setOnClose(@Nullable MethodWrapper<IScreen, Object, Object, ?> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public IScreen reloadScreen() {
        client.execute(() -> client.setScreen((Screen) (Object) this));
        return this;
    }

    @Override
    public ButtonWidgetHelper.ButtonBuilder buttonBuilder() {
        return new ButtonWidgetHelper.ButtonBuilder(this);
    }

    @Override
    public CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder() {
        return new CheckBoxWidgetHelper.CheckBoxBuilder(this);
    }

    @Override
    public CheckBoxWidgetHelper.CheckBoxBuilder checkBoxBuilder(boolean checked) {
        return new CheckBoxWidgetHelper.CheckBoxBuilder(this).checked(checked);
    }

    @Override
    public CyclingButtonWidgetHelper.CyclicButtonBuilder<?> cyclicButtonBuilder(MethodWrapper<Object, ?, TextHelper, ?> valueToText) {
        return new CyclingButtonWidgetHelper.CyclicButtonBuilder<>(this, valueToText);
    }

    @Override
    public LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder() {
        return new LockButtonWidgetHelper.LockButtonBuilder(this);
    }

    @Override
    public LockButtonWidgetHelper.LockButtonBuilder lockButtonBuilder(boolean locked) {
        return new LockButtonWidgetHelper.LockButtonBuilder(this).locked(locked);
    }

    @Override
    public SliderWidgetHelper.SliderBuilder sliderBuilder() {
        return new SliderWidgetHelper.SliderBuilder(this);
    }

    @Override
    public TextFieldWidgetHelper.TextFieldBuilder textFieldBuilder() {
        return new TextFieldWidgetHelper.TextFieldBuilder(this, textRenderer);
    }

    @Override
    public ButtonWidgetHelper.TexturedButtonBuilder texturedButtonBuilder() {
        return new ButtonWidgetHelper.TexturedButtonBuilder(this);
    }

    @Override
    public void jsmacros_render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (drawContext == null) {
            return;
        }

        synchronized (elements) {
            Iterator<RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderElement::getZIndex)).iterator();
            xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text hoverText = null;

            while (iter.hasNext()) {
                RenderElement e = iter.next();
                e.render(drawContext, mouseX, mouseY, delta);
                if (e instanceof xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) {
                    xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text t = (xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }

            if (hoverText != null) {
                drawContext.drawHoverEvent(textRenderer, textRenderer.getTextHandler().getStyleAt(hoverText.text, mouseX - hoverText.x), mouseX, mouseY);
            }
        }
    }

    @Override
    public void jsmacros_mouseClicked(double mouseX, double mouseY, int button) {
        if (onMouseDown != null) {
            try {
                onMouseDown.accept(new Pos2D(mouseX, mouseY), button);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
        xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text hoverText = null;

        synchronized (elements) {
            for (RenderElement e : elements) {
                if (e instanceof xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) {
                    xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text t = (xyz.wagyourtail.jsmacros.client.api.classes.render.components.Text) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }
        }

        if (hoverText != null) {
            handleTextClick(textRenderer.getTextHandler().getStyleAt(hoverText.text, (int) mouseX - hoverText.x));
        }
    }

    @Override
    public void jsmacros_mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) {
            try {
                onMouseDrag.accept(new Vec2D(mouseX, mouseY, deltaX, deltaY), button);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
    }

    @Override
    public void jsmacros_mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) {
            try {
                onMouseUp.accept(new Pos2D(mouseX, mouseY), button);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
    }

    @Override
    public void jsmacros_keyPressed(int keyCode, int scanCode, int modifiers) {
        if (onKeyPressed != null) {
            try {
                onKeyPressed.accept(keyCode, modifiers);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
    }

    @Override
    public void jsmacros_charTyped(char chr, int modifiers) {
        if (onCharTyped != null) {
            try {
                onCharTyped.accept(chr, modifiers);
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
    }

    @Override
    public void jsmacros_mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        if (onScroll != null) {
            try {
                onScroll.accept(new Pos2D(mouseX, mouseY), new Pos2D(horiz, vert));
            } catch (Throwable e) {
                JsMacrosClient.clientCore.profile.logError(e);
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    protected void init(CallbackInfo info) {
        synchronized (elements) {
            elements.clear();
        }
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Throwable e) {
                try {
                    if (catchInit != null) {
                        catchInit.accept(e.toString());
                    } else {
                        throw e;
                    }
                } catch (Throwable f) {
                    JsMacrosClient.clientCore.profile.logError(f);
                }
            }
        }
        getDraw2Ds().forEach(e -> e.getDraw2D().init());
    }

    //TODO: switch to enum extension with mixin 9.0 or whenever Mumfrey gets around to it
    public void handleCustomClickEvent(Style style, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent instanceof CustomClickEvent) {
            ((CustomClickEvent) clickEvent).getEvent().run();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Override
    public MethodWrapper<IScreen, Object, Object, ?> getOnClose() {
        return onClose;
    }

}
