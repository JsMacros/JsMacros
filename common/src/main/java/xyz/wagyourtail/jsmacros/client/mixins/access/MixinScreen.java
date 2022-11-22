package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IGuiTextField;
import xyz.wagyourtail.jsmacros.client.access.IScreenInternal;
import xyz.wagyourtail.jsmacros.client.api.helpers.ButtonWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextFieldWidgetHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon.Pos2D;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.RenderCommon;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.client.gui.elements.Drawable;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.client.access.CustomClickEvent;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Mixin(GuiScreen.class)
@Implements(@Interface(iface = IScreen.class, prefix = "soft$"))
public abstract class MixinScreen extends Gui implements IScreen, IScreenInternal {
    @Unique private final Set<RenderCommon.RenderElement> elements = new LinkedHashSet<>();
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseDown;
    @Unique private MethodWrapper<PositionCommon.Vec2D, Integer, Object, ?> onMouseDrag;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Integer, Object, ?> onMouseUp;
    @Unique private MethodWrapper<PositionCommon.Pos2D, Double, Object, ?> onScroll;
    @Unique private MethodWrapper<Integer, Integer, Object, ?> onKeyPressed;
    @Unique private MethodWrapper<IScreen, Object, Object, ?> onInit;
    @Unique private MethodWrapper<String, Object, Object, ?> catchInit;
    @Unique private MethodWrapper<IScreen, Object, Object, ?> onClose;

    @Unique
    private Map<GuiButton, Consumer<GuiButton>> customButtons = new HashMap<>();
    @Unique
    private Set<GuiTextField> customTextFields = new HashSet<>();

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected Minecraft client;
    @Shadow
    protected FontRenderer textRenderer;
    @Shadow
    private GuiButton prevClickedButton;

    @Shadow
    public abstract void removed();

    @Shadow
    protected abstract void init();
    @Shadow public abstract void tick();

    @Shadow
    private static boolean hasShiftDown() {
        return false;
    }

    @Shadow
    private static boolean hasControlDown() {
        return false;
    }

    @Shadow
    private static boolean hasAltDown() {
        return false;
    }


    @Shadow
    protected abstract void buttonClicked(GuiButton button) throws IOException;

    @Shadow
    protected List<GuiButton> buttons;

    @Shadow protected abstract void renderTextHoverEffect(IChatComponent component, int x, int y);

    @Shadow public abstract boolean handleTextClick(IChatComponent component);

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public List<RenderCommon.Text> getTexts() {
        List<RenderCommon.Text> list = new LinkedList<>();
        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof RenderCommon.Text) list.add((RenderCommon.Text) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Rect> getRects() {
        List<RenderCommon.Rect> list = new LinkedList<>();
        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof RenderCommon.Rect) list.add((RenderCommon.Rect) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Item> getItems() {
        List<RenderCommon.Item> list = new LinkedList<>();
        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof RenderCommon.Item) list.add((RenderCommon.Item) e);
            }
        }
        return list;
    }

    @Override
    public List<RenderCommon.Image> getImages() {
        List<RenderCommon.Image> list = new LinkedList<>();
        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof RenderCommon.Image) list.add((RenderCommon.Image) e);
            }
        }
        return list;
    }

    @Override
    public List<TextFieldWidgetHelper> getTextFields() {
        Map<GuiTextField, TextFieldWidgetHelper> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof TextFieldWidgetHelper) {
                btns.put(((TextFieldWidgetHelper) el).getRaw(), (TextFieldWidgetHelper) el);
            }
        }
        Arrays.stream(this.getClass().getDeclaredFields())
            .filter(e -> e.getType().equals(GuiTextField.class))
            .map(e -> {
                try {
                    e.setAccessible(true);
                    return (GuiTextField) e.get(this);
                } catch (IllegalAccessException illegalAccessException) {
                    throw new RuntimeException(illegalAccessException);
                }
            })
            .forEach(e -> btns.put(e, new TextFieldWidgetHelper(e)));
        return new ArrayList<>(btns.values());
    }

    @Override
    public List<ButtonWidgetHelper<?>> getButtonWidgets() {
        Map<GuiButton, ButtonWidgetHelper<?>> btns = new LinkedHashMap<>();
        for (RenderCommon.RenderElement el : elements) {
            if (el instanceof ButtonWidgetHelper) {
                btns.put(((ButtonWidgetHelper<?>) el).getRaw(), (ButtonWidgetHelper<?>) el);
            }
        }
        synchronized (buttons) {
            for (GuiButton e : buttons) {
                if (!btns.containsKey(e)) {
                    btns.put(e, new ButtonWidgetHelper<>(e));
                }
            }
            for (GuiButton e : customButtons.keySet()) {
                if (!btns.containsKey(e)) {
                    btns.put(e, new ButtonWidgetHelper<>(e));
                }
            }
        }
        return new ArrayList<>(btns.values());
    }

    @Override
    public List<RenderCommon.RenderElement> getElements() {
        return new ArrayList<>(elements);
    }

    @Override
    public IScreen removeElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.remove(e);
            if (e instanceof ButtonWidgetHelper)
                buttons.remove(((ButtonWidgetHelper<?>) e).getRaw());
        }
        return this;
    }

    @Override
    public RenderCommon.RenderElement reAddElement(RenderCommon.RenderElement e) {
        synchronized (elements) {
            elements.add(e);
            if (e instanceof ButtonWidgetHelper)
                buttons.add(((ButtonWidgetHelper<?>) e).getRaw());
        }
        return e;
    }

    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }

    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public RenderCommon.Text addText(String text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }


    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow) {
        return addText(text, x, y, color, 0, shadow, 1, 0);
    }

    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow) {
        return addText(text, x, y, color, zIndex, shadow, 1, 0);
    }

    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, boolean shadow, double scale, double rotation) {
        return addText(text, x, y, color, 0, shadow, scale, rotation);
    }

    @Override
    public RenderCommon.Text addText(TextHelper text, int x, int y, int color, int zIndex, boolean shadow, double scale, double rotation) {
        RenderCommon.Text t = new RenderCommon.Text(text, x, y, color, zIndex, shadow, scale, (float) rotation);
        synchronized (elements) {
            elements.add(t);
        }
        return t;
    }

    @Override
    public IScreen removeText(RenderCommon.Text t) {
        synchronized (elements) {
            elements.remove(t);
        }
        return this;
    }

    @Override
    public RenderCommon.Image addImage(
        int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
        int regionHeight, int textureWidth, int textureHeight
    ) {
        return addImage(
            x,
            y,
            width,
            height,
            0,
            id,
            imageX,
            imageY,
            regionWidth,
            regionHeight,
            textureWidth,
            textureHeight,
            0
        );
    }

    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        return addImage(
            x,
            y,
            width,
            height,
            zIndex,
            id,
            imageX,
            imageY,
            regionWidth,
            regionHeight,
            textureWidth,
            textureHeight,
            0
        );
    }

    @Override
    public RenderCommon.Image addImage(
        int x, int y, int width, int height, String id, int imageX, int imageY, int regionWidth,
        int regionHeight, int textureWidth, int textureHeight, double rotation
    ) {
        return addImage(
            x,
            y,
            width,
            height,
            0,
            id,
            imageX,
            imageY,
            regionWidth,
            regionHeight,
            textureWidth,
            textureHeight,
            rotation
        );
    }


    /**
     * @since 1.4.0
     * @see IDraw2D#addImage(int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        return addImage(x, y, width, height, zIndex, 0xFFFFFFFF, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, rotation);
    }

    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        RenderCommon.Image i = new RenderCommon.Image(x, y, width, height, zIndex, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    /**
     * @since 1.6.5
     * @see IDraw2D#addImage(int, int, int, int, int, int, int, String, int, int, int, int, int, int, double)
     */
    @Override
    public RenderCommon.Image addImage(int x, int y, int width, int height, int zIndex, int alpha, int color, String id, int imageX, int imageY, int regionWidth, int regionHeight, int textureWidth, int textureHeight, double rotation) {
        RenderCommon.Image i = new RenderCommon.Image(x, y, width, height, zIndex, alpha, color, id, imageX, imageY, regionWidth, regionHeight, textureWidth, textureHeight, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }
    
    @Override
    public IScreen removeImage(RenderCommon.Image i) {
        synchronized (elements) {
            elements.remove(i);
        }
        return this;
    }

    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, 0F, 0);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha) {
        return addRect(x1, y1, x2, y2, color, alpha, 0, 0);
    }


    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation) {
        return addRect(x1, y1, x2, y2, color, alpha, rotation, 0);
    }

    @Override
    public RenderCommon.Rect addRect(int x1, int y1, int x2, int y2, int color, int alpha, double rotation, int zIndex) {
        RenderCommon.Rect r = new RenderCommon.Rect(x1, y1, x2, y2, color, alpha, (float) rotation, zIndex);
        synchronized (elements) {
            elements.add(r);
        }
        return r;
    }

    @Override
    public IScreen removeRect(RenderCommon.Rect r) {
        synchronized (elements) {
            elements.remove(r);
        }
        return this;
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, String id) {
        return addItem(x, y, 0, id, true, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id) {
        return addItem(x, y, zIndex, id, true, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay) {
        return addItem(x, y, 0, id, overlay, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay) {
        return addItem(x, y, zIndex, id, overlay, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, String id, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, id, overlay, scale, rotation);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, String id, boolean overlay, double scale, double rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, zIndex, id, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item) {
        return addItem(x, y, 0, item, true, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item) {
        return addItem(x, y, zIndex, item, true, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, 0, item, overlay, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay) {
        return addItem(x, y, zIndex, item, overlay, 1, 0);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        return addItem(x, y, 0, item, overlay, scale, rotation);
    }

    @Override
    public RenderCommon.Item addItem(int x, int y, int zIndex, ItemStackHelper item, boolean overlay, double scale, double rotation) {
        RenderCommon.Item i = new RenderCommon.Item(x, y, zIndex, item, overlay, scale, (float) rotation);
        synchronized (elements) {
            elements.add(i);
        }
        return i;
    }

    @Override
    public IScreen removeItem(RenderCommon.Item i) {
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
    public String getTitleText() {
        return null;
        //return title.getString();
    }

    @Override
    public ButtonWidgetHelper<?> addButton(
        int x, int y, int width, int height, String text,
        MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object, ?> callback
    ) {
        return addButton(x, y, width, height, 0, text, callback);
    }

    @Override
    public ButtonWidgetHelper<?> addButton(int x, int y, int width, int height, int zIndex, String text, MethodWrapper<ButtonWidgetHelper<?>, IScreen, Object, ?> callback) {
        AtomicReference<ButtonWidgetHelper<?>> b = new AtomicReference<>(null);
        GuiButton button = new GuiButton(-999, x, y, width, height, text);
        customButtons.put(button, (btn) -> {
            try {
                callback.accept(b.get(), this);
            } catch (Throwable e) {
                Core.getInstance().profile.logError(e);
            }
        });
        b.set(new ButtonWidgetHelper<>(button, zIndex));
        synchronized (elements) {
            elements.add(b.get());
        }
        return b.get();
    }

    @Override
    public IScreen removeButton(ButtonWidgetHelper<?> btn) {
        synchronized (elements) {
            elements.remove(btn);
            customButtons.remove(btn.getRaw());
        }
        return this;
    }

    @Override
    public TextFieldWidgetHelper addTextInput(
        int x, int y, int width, int height, String message,
        MethodWrapper<String, IScreen, Object, ?> onChange
    ) {
        return addTextInput(x, y, width, height, 0, message, onChange);
    }

    @Override
    public TextFieldWidgetHelper addTextInput(int x, int y, int width, int height, int zIndex, String message, MethodWrapper<String, IScreen, Object, ?> onChange) {
        GuiTextField field = new GuiTextField(-999, textRenderer, x, y, width, height);
        field.setText(message);
        if (onChange != null) {
            ((IGuiTextField) field).setOnChange(str -> {
                try {
                    onChange.accept(str, this);
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
        }
        TextFieldWidgetHelper w = new TextFieldWidgetHelper(field, zIndex);
        synchronized (elements) {
            elements.add(w);
            customTextFields.add(w.getRaw());
        }
        return w;
    }

    @Override
    public IScreen removeTextInput(TextFieldWidgetHelper inp) {
        synchronized (elements) {
            if (customTextFields.contains(inp.getRaw())) {
                elements.remove(inp);
                customTextFields.remove(inp);
            }
        }
        return this;
    }

    @Override
    public void close() {
        if ((Object) this instanceof BaseScreen) {
            ((BaseScreen) (Object) this).onClose();
        }
        removed();

    }

    @Override
    public IScreen setOnMouseDown(MethodWrapper<Pos2D, Integer, Object, ?> onMouseDown) {
        this.onMouseDown = onMouseDown;
        return this;
    }

    @Override
    public IScreen setOnMouseDrag(MethodWrapper<PositionCommon.Vec2D, Integer, Object, ?> onMouseDrag) {
        this.onMouseDrag = onMouseDrag;
        return this;
    }

    @Override
    public IScreen setOnMouseUp(MethodWrapper<Pos2D, Integer, Object, ?> onMouseUp) {
        this.onMouseUp = onMouseUp;
        return this;
    }

    @Override
    public IScreen setOnScroll(MethodWrapper<Pos2D, Double, Object, ?> onScroll) {
        this.onScroll = onScroll;
        return this;
    }

    @Override
    public IScreen setOnKeyPressed(MethodWrapper<Integer, Integer, Object, ?> onKeyPressed) {
        this.onKeyPressed = onKeyPressed;
        return this;
    }

    @Override
    public IScreen setOnInit(MethodWrapper<IScreen, Object, Object, ?> onInit) {
        this.onInit = onInit;
        return this;
    }

    @Override
    public IScreen setOnFailInit(MethodWrapper<String, Object, Object, ?> catchInit) {
        this.catchInit = catchInit;
        return this;
    }

    @Override
    public IScreen setOnClose(MethodWrapper<IScreen, Object, Object, ?> onClose) {
        this.onClose = onClose;
        return this;
    }

    @Override
    public IScreen reloadScreen() {
        client.execute(() -> client.openScreen((GuiScreen) (Object) this));
        return this;
    }

    @Override
    public void jsmacros_render(int mouseX, int mouseY, float delta) {

        synchronized (elements) {
            Iterator<RenderCommon.RenderElement> iter = elements.stream().sorted(Comparator.comparingInt(RenderCommon.RenderElement::getZIndex)).iterator();
            RenderCommon.Text hoverText = null;

            while (iter.hasNext()) {
                RenderCommon.RenderElement e = iter.next();
                e.render(mouseX, mouseY, delta);
                if (e instanceof RenderCommon.Text) {
                    RenderCommon.Text t = (RenderCommon.Text) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }

            if (hoverText != null) {
                renderTextHoverEffect(jsmacros_getTextComponentUnderMouse(hoverText.text, mouseX - hoverText.x), mouseX, mouseY);
            }
        }
    }


    @Unique
    public IChatComponent jsmacros_getTextComponentUnderMouse(IChatComponent message, int mouseX) {
        if (message == null) {
            return null;
        } else {
            int i = this.client.textRenderer.getStringWidth(message.asFormattedString());
            int j = this.width / 2 - i / 2;
            int k = this.width / 2 + i / 2;
            int l = j;
            if (mouseX >= j && mouseX <= k) {
                for(IChatComponent text : message) {
                    l += this.client.textRenderer.getStringWidth(GuiUtilRenderComponents.getRenderChatMessage(text.asString(), false));
                    if (l > mouseX) {
                        return text;
                    }
                }

                return null;
            } else {
                return null;
            }
        }
    }

    public void jsmacros_mouseClicked(double mouseX, double mouseY, int button) {

        if (onMouseDown != null) try {
            onMouseDown.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }

        RenderCommon.Text hoverText = null;

        synchronized (elements) {
            for (RenderCommon.RenderElement e : elements) {
                if (e instanceof RenderCommon.Text) {
                    RenderCommon.Text t = (RenderCommon.Text) e;
                    if (mouseX > t.x && mouseX < t.x + t.width && mouseY > t.y && mouseY < t.y + textRenderer.fontHeight) {
                        hoverText = t;
                    }
                }
            }
        }

        if (hoverText != null) {
            handleTextClick(jsmacros_getTextComponentUnderMouse(hoverText.text, (int) mouseX - hoverText.x));
        }
    }

    @Override
    public void jsmacros_mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (onMouseDrag != null) try {
            onMouseDrag.accept(new PositionCommon.Vec2D(mouseX, mouseY, deltaX, deltaY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Override
    public void jsmacros_mouseReleased(double mouseX, double mouseY, int button) {
        if (onMouseUp != null) try {
            onMouseUp.accept(new PositionCommon.Pos2D(mouseX, mouseY), button);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Override
    public void jsmacros_keyPressed(int keyCode, int scanCode, int modifiers) {
        if (onKeyPressed != null) try {
            onKeyPressed.accept(keyCode, modifiers);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Override
    public void jsmacros_mouseScrolled(double mouseX, double mouseY, double amount) {
        if (onScroll != null) try {
            onScroll.accept(new PositionCommon.Pos2D(mouseX, mouseY), amount);
        } catch (Throwable e) {
            Core.getInstance().profile.logError(e);
        }
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    protected void init(CallbackInfo info) {
        synchronized (elements) {
            elements.clear();
            customButtons.clear();
        }
        if (onInit != null) {
            try {
                onInit.accept(this);
            } catch (Throwable e) {
                try {
                    if (catchInit != null)
                        catchInit.accept(e.toString());
                    else
                        throw e;
                } catch (Exception f) {
                    f.printStackTrace();
                    Core.getInstance().profile.logError(f);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "mouseClicked")
    public void onMouseClick(int mouseX, int mouseY, int mouseButton, CallbackInfo ci) {
        if (mouseButton == 0) {
            for (GuiButton btn : customButtons.keySet()) {
                if (btn.isMouseOver(this.client, mouseX, mouseY)) {
                    prevClickedButton = btn;
                    customButtons.get(btn).accept(btn);
                }
            }
            for (GuiTextField field : customTextFields) {
                field.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        return false;
    }

    @Override
    public void clickBtn(GuiButton btn) throws IOException {
        GuiButton prev = prevClickedButton;
        if (buttons.contains(btn)) {
            prevClickedButton = btn;
            btn.playDownSound(this.client.getSoundManager());
            buttonClicked(btn);
            prevClickedButton = prev;
        }
    }

    @Override
    public GuiButton getFocused() {
        return prevClickedButton;
    }

    @Override
    public MethodWrapper<IScreen, Object, Object, ?> getOnClose() {
        return onClose;
    }

    //TODO: switch to enum extention with mixin 9.0 or whenever Mumfrey gets around to it
    @Inject(at = @At(value = "INVOKE",
        target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V",
        remap = false), method = "handleTextClick", cancellable = true)
    public void handleCustomClickEvent(IChatComponent t, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = t.getStyle().getClickEvent();
        if (clickEvent instanceof CustomClickEvent) {
            ((CustomClickEvent) clickEvent).getEvent().run();
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

}