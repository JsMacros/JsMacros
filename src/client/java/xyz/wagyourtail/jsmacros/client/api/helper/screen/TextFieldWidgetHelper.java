package xyz.wagyourtail.jsmacros.client.api.helper.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.JsMacrosClient;
import xyz.wagyourtail.jsmacros.client.api.classes.render.IScreen;
import xyz.wagyourtail.jsmacros.client.mixin.access.MixinTextFieldWidget;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Wagyourtail
 * @since 1.0.5
 */
@SuppressWarnings("unused")
public class TextFieldWidgetHelper extends ClickableWidgetHelper<TextFieldWidgetHelper, TextFieldWidget> {
    public TextFieldWidgetHelper(TextFieldWidget t) {
        super(t);
    }

    public TextFieldWidgetHelper(TextFieldWidget t, int zIndex) {
        super(t, zIndex);
    }

    /**
     * @return the currently entered {@link String String}.
     * @since 1.0.5
     */
    public String getText() {
        return base.getText();
    }

    /**
     * @param text
     * @return self for chaining.
     * @since 1.0.5
     */
    public TextFieldWidgetHelper setText(String text) throws InterruptedException {
        setText(text, true);
        return this;
    }

    /**
     * set the currently entered {@link String String}.
     *
     * @param text
     * @param await
     * @return self for chaining.
     * @throws InterruptedException
     * @since 1.3.1
     */
    public TextFieldWidgetHelper setText(String text, boolean await) throws InterruptedException {
        if (JsMacrosClient.clientCore.profile.checkJoinedThreadStack()) {
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
     * @param color
     * @return self for chaining.
     * @since 1.0.5
     */
    public TextFieldWidgetHelper setEditableColor(int color) {
        base.setEditableColor(color);
        return this;
    }

    /**
     * @param edit
     * @return self for chaining.
     * @since 1.0.5
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
        return ((MixinTextFieldWidget) base).getEditable();
    }

    /**
     * @param color
     * @return self for chaining.
     * @since 1.0.5
     */
    public TextFieldWidgetHelper setUneditableColor(int color) {
        base.setUneditableColor(color);
        return this;
    }

    /**
     * @return the selected text.
     * @since 1.8.4
     */
    public String getSelectedText() {
        return base.getSelectedText();
    }

    /**
     * @param suggestion the suggestion to set
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setSuggestion(String suggestion) {
        base.setSuggestion(suggestion);
        return this;
    }

    /**
     * @return the maximum length of this text field.
     * @since 1.8.4
     */
    public int getMaxLength() {
        return ((MixinTextFieldWidget) base).getMaxLength();
    }

    /**
     * @param length the new maximum length
     * @return self for chaining.
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
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setTextPredicate(MethodWrapper<String, ?, ?, ?> predicate) {
        base.setTextPredicate(predicate);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextFieldWidgetHelper resetTextPredicate() {
        base.setTextPredicate(Objects::nonNull);
        return this;
    }

    /**
     * @param position the cursor position
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorPosition(int position) {
        base.setCursor(position, false);
        return this;
    }

    /**
     * @return the cursor position.
     * @since 1.9.0
     */
    public TextFieldWidgetHelper setCursorPosition(int position, boolean shift) {
        base.setCursor(position, shift);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorToStart() {
        base.setCursorToStart(false);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.9.0
     */
    public TextFieldWidgetHelper setCursorToStart(boolean shift) {
        base.setCursorToStart(shift);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.8.4
     */
    public TextFieldWidgetHelper setCursorToEnd() {
        base.setCursorToEnd(false);
        return this;
    }

    /**
     * @return self for chaining.
     * @since 1.9.0
     */
    public TextFieldWidgetHelper setCursorToEnd(boolean shift) {
        base.setCursorToEnd(shift);
        return this;
    }

    @Override
    public String toString() {
        return String.format("TextFieldWidgetHelper:{\"text\": \"%s\"}", base.getText());
    }

    /**
     * @author Etheradon
     * @since 1.8.4
     */
    public static class TextFieldBuilder extends AbstractWidgetBuilder<TextFieldBuilder, TextFieldWidget, TextFieldWidgetHelper> {

        private String suggestion = "";
        @Nullable
        private MethodWrapper<String, IScreen, Object, ?> action;
        private final TextRenderer textRenderer;

        public TextFieldBuilder(IScreen screen, TextRenderer textRenderer) {
            super(screen);
            this.textRenderer = textRenderer;
        }

        /**
         * @return the callback for when the text is changed.
         * @since 1.8.4
         */
        @Nullable
        public MethodWrapper<String, IScreen, Object, ?> getAction() {
            return action;
        }

        /**
         * @param action the callback for when the text is changed
         * @return self for chaining.
         * @since 1.8.4
         */
        public TextFieldBuilder action(@Nullable MethodWrapper<String, IScreen, Object, ?> action) {
            this.action = action;
            return this;
        }

        /**
         * @return the current suggestion.
         * @since 1.8.4
         */
        public String getSuggestion() {
            return suggestion;
        }

        /**
         * @param suggestion the suggestion to use
         * @return self for chaining.
         * @since 1.8.4
         */
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
                    JsMacrosClient.clientCore.profile.logError(e);
                }
            });
            textField.setSuggestion(suggestion);
            b.set(new TextFieldWidgetHelper(textField, getZIndex()));
            return b.get();
        }

    }

}
