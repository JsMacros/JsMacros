package xyz.wagyourtail.jsmacros.client.api.helpers.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

import xyz.wagyourtail.jsmacros.client.access.ITextFieldWidget;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**F
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class TextFieldWidgetHelper extends ButtonWidgetHelper<TextFieldWidget> {
    public TextFieldWidgetHelper(TextFieldWidget t) {
        super(t);
    }
    
    public TextFieldWidgetHelper(TextFieldWidget t, int zIndex) {
        super(t, zIndex);
    }
    
    /**
     * @since 1.0.5
     * @return the currently entered {@link java.lang.String String}.
     */
    public String getText() {
        return base.getText();
    }
    
    /**
     *
     * @since 1.0.5
     * @param text
     * @return
     */
    public TextFieldWidgetHelper setText(String text) throws InterruptedException {
        setText(text, true);
        return this;
    }
    
    /**
     * set the currently entered {@link java.lang.String String}.
     *
     * @param text
     * @param await
     *
     * @return
     * @since 1.3.1
     *
     * @throws InterruptedException
     */
    public TextFieldWidgetHelper setText(String text, boolean await) throws InterruptedException {
        if (Core.getInstance().profile.checkJoinedThreadStack()) {
            base.setText(text);
        } else {
            final Semaphore waiter = new Semaphore(await ? 0 : 1);
            MinecraftClient.getInstance().execute(() -> {
                base.setText(text);
                waiter.release();
            });
            waiter.acquire();
        }
        return this;
    }
    
    
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        base.setEditableColor(color);
        return this;
    }
    
    /**
     * @since 1.0.5
     * @param edit
     * @return
     */
    public TextFieldWidgetHelper setEditable(boolean edit) {
        base.setEditable(edit);
        return this;
    }

    /**
     * @return
     * @since 1.9.0
     */
    public boolean isEditable() {
        return ((ITextFieldWidget) base).jsmacros_isEditable();
    }
    
    /**
     * @since 1.0.5
     * @param color
     * @return
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        base.setUneditableColor(color);
        return this;
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public String getSelectedText() {
        return base.getSelectedText();
    }

    /**
     * @param suggestion
     * @since 1.9.0
     */
    public void setSuggestion(String suggestion) {
        base.setSuggestion(suggestion);
    }

    /**
     * @return
     *
     * @since 1.9.0
     */
    public int getMaxLength() {
        return ((ITextFieldWidget) base).jsmacros_getMaxLength();
    }

    /**
     * @param length
     * @since 1.9.0
     */
    public void setMaxLength(int length) {
        base.setMaxLength(length);
    }

    public void setSelection(int start, int end) {
        base.setSelectionStart(start);
        base.setSelectionEnd(end);
    }

    /**
     * @param predicate
     * @since 1.9.0
     */
    public void setTextPredicate(MethodWrapper<String, ?, ?, ?> predicate) {
        base.setTextPredicate(predicate);
    }

    /**
     * @since 1.9.0
     */
    public void resetTextPredicate() {
        base.setTextPredicate(Objects::nonNull);
    }

    /**
     * @param position
     * @since 1.9.0
     */
    public void setCursorPosition(int position) {
        base.setCursor(position);
    }

    /**
     * @since 1.9.0
     */
    public void setCursorToStart() {
        base.setCursorToStart();
    }

    /**
     * @since 1.9.0
     */
    public void setCursorToEnd() {
        base.setCursorToEnd();
    }

    public static class TextFieldBuilder extends AbstractWidgetBuilder<TextFieldWidget, TextFieldWidgetHelper> {

        private String suggestion = "";
        private MethodWrapper<String, IScreen, Object, ?> action;
        private final TextRenderer textRenderer;

        public TextFieldBuilder(IScreen screen, TextRenderer textRenderer) {
            super(screen);
            this.textRenderer = textRenderer;
        }

        public MethodWrapper<String, IScreen, Object, ?> getAction() {
            return action;
        }

        public TextFieldBuilder action(MethodWrapper<String, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public TextFieldBuilder suggestion(String suggestion) {
            this.suggestion = suggestion;
            return this;
        }

        @Override
        public TextFieldWidgetHelper createWidget() {
            AtomicReference<TextFieldWidgetHelper> b = new AtomicReference<>(null);
            TextFieldWidget textField = new TextFieldWidget(textRenderer, getX(), getY(), getWidth(), getHeight(), getMessage().getRaw());
            textField.setChangedListener(text -> {
                try {
                    if (action != null) {
                        action.accept(text, screen);
                    }
                } catch (Throwable e) {
                    Core.getInstance().profile.logError(e);
                }
            });
            textField.setSuggestion(suggestion);
            b.set(new TextFieldWidgetHelper(textField, getZIndex()));
            return b.get();
        }
    }
    
}
