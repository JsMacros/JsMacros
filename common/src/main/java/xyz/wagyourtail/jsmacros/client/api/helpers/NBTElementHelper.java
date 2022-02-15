package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.nbt.AbstractNbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Set;

/**
 * @since 1.5.1
 */
public class NBTElementHelper<T extends NbtElement> extends BaseHelper<T> {

    private NBTElementHelper(T base) {
        super(base);
    }

    /**
     * @since 1.5.1
     */
    public int getType() {
        return base.getType();
    }

    /**
     * @since 1.5.1
     */
    public boolean isNull() {
        return base.getType() == 0;
    }


    /**
     * @since 1.5.1
     */
    public boolean isNumber() {
        return base.getType() != 0 && base.getType() < 7;
    }


    /**
     * @since 1.5.1
     */
    public boolean isString() {
        return base.getType() == 8;
    }


    /**
     * @since 1.5.1
     */
    public boolean isList() {
        return base.getType() == 7 || base.getType() == 9 || base.getType() == 11 || base.getType() == 12;
    }


    /**
     * @since 1.5.1
     */
    public boolean isCompound() {
        return base.getType() == 10;
    }


    /**
     * if element is a string, returns value.
     * otherwise returns toString representation.
     * @since 1.5.1
     */
    public String asString() {
        return base.asString();
    }


    /**
     * check with {@link #isNumber()} first
     * @since 1.5.1
     */
    public NBTNumberHelper asNumberHelper() {
        return (NBTNumberHelper) this;
    }


    /**
     * check with {@link #isList()} first
     * @since 1.5.1
     */
    public NBTListHelper asListHelper() {
        return (NBTListHelper) this;
    }


    /**
     * check with {@link #isCompound()} first
     * @since 1.5.1
     */
    public NBTCompoundHelper asCompoundHelper() {
        return (NBTCompoundHelper) this;
    }

    public String toString() {
        return String.format("NBTElement:%s", base.toString());
    }

    /**
     * @since 1.5.1
     */
    public static NBTElementHelper<?> resolve(NbtElement element) {
        if (element == null) return null;
        switch (element.getType()) {
            case NbtElement.NULL_TYPE: //0
                return new NBTElementHelper<>(element);
            case NbtElement.BYTE_TYPE: //1
            case NbtElement.SHORT_TYPE: //2
            case NbtElement.INT_TYPE: //3
            case NbtElement.LONG_TYPE: //4
            case NbtElement.FLOAT_TYPE: //5
            case NbtElement.DOUBLE_TYPE: //6
                return new NBTNumberHelper((AbstractNbtNumber) element);
            case NbtElement.BYTE_ARRAY_TYPE: //7
            case NbtElement.LIST_TYPE: //9
            case NbtElement.INT_ARRAY_TYPE: //11
            case NbtElement.LONG_ARRAY_TYPE: //12
                return new NBTListHelper((AbstractNbtList<?>) element);
            case NbtElement.COMPOUND_TYPE: //10
                return new NBTCompoundHelper((NbtCompound) element);
            case NbtElement.STRING_TYPE: //8
        }
        return new NBTElementHelper<>(element);
    }

    /**
     * @since 1.5.1
     */
    public static class NBTNumberHelper extends NBTElementHelper<AbstractNbtNumber> {

        private NBTNumberHelper(AbstractNbtNumber base) {
            super(base);
        }


        /**
         * @since 1.5.1
         */
        public long asLong() {
            return base.longValue();
        }


        /**
         * @since 1.5.1
         */
        public int asInt() {
            return base.intValue();
        }


        /**
         * @since 1.5.1
         */
        public short asShort() {
            return base.shortValue();
        }


        /**
         * @since 1.5.1
         */
        public byte asByte() {
            return base.byteValue();
        }


        /**
         * @since 1.5.1
         */
        public float asFloat() {
            return base.floatValue();
        }


        /**
         * @since 1.5.1
         */
        public double asDouble() {
            return base.doubleValue();
        }


        /**
         * @since 1.5.1
         */
        public Number asNumber() {
            return base.numberValue();
        }
    }

    /**
     * @since 1.5.1
     */
    public static class NBTListHelper extends NBTElementHelper<AbstractNbtList<?>> {

        private NBTListHelper(AbstractNbtList<?> base) {
            super(base);
        }

        /**
         * @since 1.5.1
         * @return
         */
        public int length() {
            return base.size();
        }

        /**
         * @since 1.5.1
         */
        public NBTElementHelper<?> get(int index) {
            return resolve(base.get(index));
        }


        /**
         * @since 1.5.1
         */
        public int getHeldType() {
            return base.getHeldType();
        }
    }

    /**
     * @since 1.5.1
     */
    public static class NBTCompoundHelper extends NBTElementHelper<NbtCompound> {

        private NBTCompoundHelper(NbtCompound base) {
            super(base);
        }


        /**
         * @since 1.6.0
         * @return
         */
        public Set<String> getKeys() {
            return base.getKeys();
        }

        /**
         * @since 1.5.1
         */
        public int getType(String key) {
            return base.getType(key);
        }


        /**
         * @since 1.5.1
         */
        public boolean has(String key) {
            return base.contains(key);
        }


        /**
         * @since 1.5.1
         */
        public NBTElementHelper<?> get(String key) {
            return resolve(base.get(key));
        }


        /**
         * @since 1.5.1
         */
        public String asString(String key) {
            return base.get(key).asString();
        }

    }
}
