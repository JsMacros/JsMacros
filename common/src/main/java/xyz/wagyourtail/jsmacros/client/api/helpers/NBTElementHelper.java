package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.nbt.*;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.Set;
import java.util.UUID;

/**
 * @since 1.5.1
 */
public class NBTElementHelper<T extends Tag> extends BaseHelper<T> {

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
    public static NBTElementHelper<?> resolve(Tag element) {
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
                return new NBTNumberHelper((AbstractNumberTag) element);
            case 7: //Tag.BYTE_ARRAY_TYPE
            case 9: //Tag.LIST_TYPE
            case 11: //Tag.INT_ARRAY_TYPE
            case 12: //Tag.LONG_ARRAY_TYPE
                return new NBTListHelper((AbstractListTag<?>) element);
            case 10: //NbtElement.COMPOUND_TYPE
                return new NBTCompoundHelper((CompoundTag) element);
            case 8: //NbtElement.STRING_TYPE
        }
        return new NBTElementHelper<>(element);
    }

    /**
     * @since 1.5.1
     */
    public static class NBTNumberHelper extends NBTElementHelper<AbstractNumberTag> {

        private NBTNumberHelper(AbstractNumberTag base) {
            super(base);
        }


        /**
         * @since 1.5.1
         */
        public long asLong() {
            return base.getLong();
        }


        /**
         * @since 1.5.1
         */
        public int asInt() {
            return base.getInt();
        }


        /**
         * @since 1.5.1
         */
        public short asShort() {
            return base.getShort();
        }


        /**
         * @since 1.5.1
         */
        public byte asByte() {
            return base.getByte();
        }


        /**
         * @since 1.5.1
         */
        public float asFloat() {
            return base.getFloat();
        }


        /**
         * @since 1.5.1
         */
        public double asDouble() {
            return base.getDouble();
        }


        /**
         * @since 1.5.1
         */
        public Number asNumber() {
            return base.getNumber();
        }
    }

    /**
     * @since 1.5.1
     */
    public static class NBTListHelper extends NBTElementHelper<AbstractListTag<?>> {

        private NBTListHelper(AbstractListTag<?> base) {
            super(base);
        }

        /**
         * @since 1.8.3
         * @return
         */
        public boolean isPossiblyUUID() {
            return base.getType() == 11 && base.size() == 4;
        }

        /**
         * @since 1.8.3
         * @return
         */
        public UUID asUUID() {
            if (!isPossiblyUUID()) return null;
            return NbtHelper.toUuid(base);
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
            return base.getElementType();
        }
    }

    /**
     * @since 1.5.1
     */
    public static class NBTCompoundHelper extends NBTElementHelper<CompoundTag> {

        private NBTCompoundHelper(CompoundTag base) {
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
