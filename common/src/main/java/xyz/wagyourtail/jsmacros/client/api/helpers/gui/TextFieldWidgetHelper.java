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

/**
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
     * @return {@code true} if the text field is editable, {@code false} otherwise.
     * @since 1.8.4
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
     * @return the selected text.
     *
     * @since 1.8.4
     */
    public String getSelectedText() {
        return base.getSelectedText();
    }

    /**
     * @param suggestion the suggestion to set
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setSuggestion(String suggestion) {
        base.setSuggestion(suggestion);
        return this;
    }

    /**
     * @return the maximum length of this text field.
     *
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public int getMaxLength() {
        return ((ITextFieldWidget) base).jsmacros_getMaxLength();
    }

    /**
     * @param length the new maximum length
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setMaxLength(int length) {
        base.setMaxLength(length);
        return this;
    }

    public TextFieldWidgetHelper setSelection(int start, int end) {
        base.setSelectionStart(start);
        base.setSelectionEnd(end);
        return this;
    }

    /**
     * @param predicate the text filter
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setTextPredicate(MethodWrapper<String, ?, ?, ?> predicate) {
        base.setTextPredicate(predicate);
        return this;
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper resetTextPredicate() {
        base.setTextPredicate(Objects::nonNull);
        return this;
    }

    /**
     * @param position the cursor position
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorPosition(int position) {
        base.setCursor(position);
        return this;
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorToStart() {
        base.setCursorToStart();
        return this;
    }

    /**
     * @return self for chaining.
     *
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorToEnd() {
        base.setCursorToEnd();
        return this;
    }

    @Override
    public String toString() {
        return String.format("TextFieldWidgetHelper:{\"text\": \"%s\"}", base.getText());
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
