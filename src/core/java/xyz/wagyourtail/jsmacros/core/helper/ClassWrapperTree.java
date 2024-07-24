package xyz.wagyourtail.jsmacros.core.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class ClassWrapperTree<K, V> {
    final List<ClassWrapperTree<? super K, ? super V>> subTypes = new ArrayList<>();
    final Class<K> type;
    Function<K, V> wrapper;

    public ClassWrapperTree(Class<K> baseType, Function<K, V> baseTypeWrapper) {
        type = baseType;
        wrapper = baseTypeWrapper;
    }

    public <T extends K, U extends V> void registerType(Class<T> type, Function<T, U> wrapper) {
        if (type.equals(this.type)) {
            this.wrapper = (Function) wrapper;
            return;
        }
        for (ClassWrapperTree<? super K, ? super V> wrap : subTypes) {
            if (wrap.type.equals(type)) {
                wrap.wrapper = (Function) wrapper;
                return;
            }
            if (wrap.type.isAssignableFrom(type)) {
                wrap.registerType(type, wrapper);
                return;
            }
        }
        ClassWrapperTree<T, U> newWrapper = new ClassWrapperTree<>(type, wrapper);
        Iterator<ClassWrapperTree<? super K, ? super V>> iter = subTypes.iterator();
        while (iter.hasNext()) {
            ClassWrapperTree<? super K, ? super V> wrap = iter.next();
            if (type.isAssignableFrom(wrap.type)) {
                newWrapper.subTypes.add(wrap);
                iter.remove();
            }
        }
        this.subTypes.add((ClassWrapperTree) newWrapper);
    }

    public <T extends K> V wrap(T inputType) {
        Class<T> inputClass = (Class) inputType.getClass();
        return getSubtypeWrapper(inputClass).wrapper.apply(inputType);
    }

    public <T extends K, U extends V> ClassWrapperTree<T, U> getSubtypeWrapper(Class<T> type) {
        for (ClassWrapperTree<? super K, ? super V> wrap : subTypes) {
            if (wrap.type.isAssignableFrom(type)) {
                return wrap.getSubtypeWrapper(type);
            }
        }
        return (ClassWrapperTree<T, U>) this;
    }

}
