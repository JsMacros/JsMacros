package xyz.wagyourtail.jsmacros.client.api.classes.render.components;

import xyz.wagyourtail.jsmacros.client.api.classes.render.IDraw2D;

/**
 * @param <T> the type of the render element for this builder
 * @author Etheradon
 * @since 1.8.4
 */
public abstract class RenderElementBuilder<T extends RenderElement> {

    protected final IDraw2D<?> parent;

    protected RenderElementBuilder(IDraw2D<?> parent) {
        this.parent = parent;
    }

    /**
     * @return the newly created element.
     * @since 1.8.4
     */
    public T build() {
        return createElement();
    }

    /**
     * Builds and adds the element to the draw2D the builder was created from.
     *
     * @return the newly created element.
     * @since 1.8.4
     */
    public T buildAndAdd() {
        T element = createElement();
        parent.reAddElement(element);
        return element;
    }

    protected abstract T createElement();

}
