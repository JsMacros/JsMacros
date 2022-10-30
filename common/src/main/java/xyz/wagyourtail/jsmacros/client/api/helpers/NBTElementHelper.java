package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.nbt.*;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinAbstractNbtNumber;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Set;
import java.util.UUID;


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
        return base.getType() == 7 || base.getType() == 9 || base.getType() == 11;
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
        if (base instanceof NbtString)
            return ((NbtString)base).asString();
        return base.toString();
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
            case 0: //Tag.NULL_TYPE
                return new NBTElementHelper<>(element);
            case 1: //Tag.BYTE_TYPE
            case 2: //Tag.SHORT_TYPE
            case 3: //Tag.INT_TYPE
            case 4: //Tag.LONG_TYPE
            case 5: //Tag.FLOAT_TYPE
            case 6: //Tag.DOUBLE_TYPE
                return new NBTNumberHelper(element);
            case 7: //Tag.BYTE_ARRAY_TYPE
                return new NBTByteArrayHelper((NbtByteArray) element);
            case 9: //Tag.LIST_TYPE
                return new NBTTagListHelper((NbtList) element);
            case 11: //Tag.INT_ARRAY_TYPE
                return new NBTIntArrayHelper((NbtIntArray) element);
            case 10: //NbtElement.COMPOUND_TYPE
                return new NBTCompoundHelper((NbtCompound) element);
            case 8: //NbtElement.STRING_TYPE
        }
        return new NBTElementHelper<>(element);
    }

    /**
     * @since 1.5.1
     */
    public static class NBTNumberHelper extends NBTElementHelper<NbtElement> {

        private NBTNumberHelper(NbtElement base) {
            super(base);
        }


        /**
         * @since 1.5.1
         */
        public long asLong() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getLong();
        }


        /**
         * @since 1.5.1
         */
        public int asInt() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getInt();
        }


        /**
         * @since 1.5.1
         */
        public short asShort() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getShort();
        }


        /**
         * @since 1.5.1
         */
        public byte asByte() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getByte();
        }


        /**
         * @since 1.5.1
         */
        public float asFloat() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getFloat();
        }


        /**
         * @since 1.5.1
         */
        public double asDouble() {
            return ((MixinAbstractNbtNumber) base).jsmacros_getDouble();
        }


        /**
         * @since 1.5.1
         */
        public Number asNumber() {
            switch (base.getType()) {
                case 1: //Tag.BYTE_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getByte();
                case 2: //Tag.SHORT_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getShort();
                case 3: //Tag.INT_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getInt();
                case 4: //Tag.LONG_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getLong();
                case 5: //Tag.FLOAT_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getFloat();
                case 6: //Tag.DOUBLE_TYPE
                    return ((MixinAbstractNbtNumber) base).jsmacros_getDouble();
            }
            return null;
        }
    }

    /**
     * @since 1.5.1
     */
    public static abstract class NBTListHelper<T extends NbtElement> extends NBTElementHelper<T> {

        private NBTListHelper(T base) {
            super(base);
        }

        /**
         * @since 1.8.3
         * @return
         */
        public boolean isPossiblyUUID() {
            return false;
        }

        /**
         * @since 1.8.3
         * @return
         */
        public UUID asUUID() {
            return null;
        }

        /**
         * @since 1.5.1
         * @return
         */
        public abstract int length();

        /**
         * @since 1.5.1
         */
        public abstract NBTElementHelper<?> get(int index);


        /**
         * not exist on <=1.15
         * @since 1.5.1
         */
        public int getHeldType() {
            return -1;
        }
    }

    private static class NBTByteArrayHelper extends NBTListHelper<NbtByteArray> {

        private NBTByteArrayHelper(NbtByteArray base) {
            super(base);
        }

        @Override
        public int length() {
            return base.getArray().length;
        }

        @Override
        public NBTElementHelper<?> get(int index) {
            return new NBTNumberHelper(new NbtByte(base.getArray()[index]));
        }


    }

    private static class NBTTagListHelper extends NBTListHelper<NbtList> {

        private NBTTagListHelper(NbtList base) {
            super(base);
        }

        @Override
        public int length() {
            return base.size();
        }

        @Override
        public NBTElementHelper<?> get(int index) {
            return resolve(base.get(index));
        }

    }

    private static class NBTIntArrayHelper extends NBTListHelper<NbtIntArray> {

        private NBTIntArrayHelper(NbtIntArray base) {
            super(base);
        }

        @Override
        public int length() {
            return base.getIntArray().length;
        }

        @Override
        public NBTElementHelper<?> get(int index) {
            return new NBTNumberHelper(new NbtInt(base.getIntArray()[index]));
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
         * @since 1.8.3
         */
        public boolean isPossiblyUUID() {
            return base.contains("M") && base.contains("L");
        }

        /**
         * @since 1.8.3
         */
        public UUID asUUID() {
            if (isPossiblyUUID()) {
                return NbtHelper.toUuid(base);
            }
            return null;
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
            return base.getKeys().contains(key);
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
            return base.getString(key);
        }

    }
}
