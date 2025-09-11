package xyz.wagyourtail.jsmacros.client.api.helper;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.*;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @since 1.5.1
 */
@SuppressWarnings("unused")
public class NBTElementHelper<T extends NbtElement> extends BaseHelper<T> {
    private static final LinkedHashMap<String, NbtPathArgumentType.NbtPath> nbtPaths = new LinkedHashMap<>(32, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, NbtPathArgumentType.NbtPath> eldest) {
            return size() > 24;
        }

    };

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
     * resolves the nbt path to elements, or null if not found.<br>
     * <a href="https://minecraft.wiki/w/NBT_path_format">NBT path format wiki</a>
     * @throws CommandSyntaxException if the path format is incorrect
     * @since 1.9.0
     */
    @Nullable
    public List<NBTElementHelper<?>> resolve(String nbtPath) throws CommandSyntaxException {
        NbtPathArgumentType.NbtPath path = nbtPaths.get(nbtPath);
        if (path == null) {
            path = NbtPathArgumentType.nbtPath().parse(new StringReader(nbtPath));
            nbtPaths.put(nbtPath, path);
        }
        try {
            return path.get(base).stream().map(NBTElementHelper::resolve).collect(Collectors.toList());
        } catch (CommandSyntaxException ignored) {}
        return null;
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
    @DocletReplaceReturn("this is NBTElementHelper$NBTNumberHelper")
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
    @DocletReplaceReturn("this is NBTElementHelper$NBTListHelper")
    public boolean isList() {
        return base.getType() == 7 || base.getType() == 9 || base.getType() == 11 || base.getType() == 12;
    }

    /**
     * @since 1.5.1
     */
    @DocletReplaceReturn("this is NBTElementHelper$NBTCompoundHelper")
    public boolean isCompound() {
        return base.getType() == 10;
    }

    /**
     * if element is a string, returns value.
     * otherwise returns toString representation.
     *
     * @since 1.5.1
     */
    public String asString() {
        return base.asString().orElseGet(base::toString);
    }

    /**
     * check with {@link #isNumber()} first
     *
     * @since 1.5.1
     */
    public NBTNumberHelper asNumberHelper() {
        return (NBTNumberHelper) this;
    }

    /**
     * check with {@link #isList()} first
     *
     * @since 1.5.1
     */
    public NBTListHelper asListHelper() {
        return (NBTListHelper) this;
    }

    /**
     * check with {@link #isCompound()} first
     *
     * @since 1.5.1
     */
    public NBTCompoundHelper asCompoundHelper() {
        return (NBTCompoundHelper) this;
    }

    public String toString() {
        return String.format("NBTElementHelper:{%s}", base.toString());
    }

    /**
     * pretty print text version
     *
     * @since 2.0.0
     */
    public TextHelper asText() {
        return TextHelper.wrap(NbtHelper.toPrettyPrintedText(base));
    }

    /**
     * @since 1.9.0
     */
    @Nullable
    public static NBTCompoundHelper wrapCompound(@Nullable NbtCompound compound) {
        return compound == null ? null : new NBTCompoundHelper(compound);
    }

    /**
     * @since 1.9.1
     */
    public static NBTElementHelper<?> wrap(@Nullable NbtElement element) {
        return element == null ? null : resolve(element);
    }

    /**
     * @since 1.5.1
     */
    @Nullable
    public static NBTElementHelper<?> resolve(@Nullable NbtElement element) {
        if (element == null) {
            return null;
        }
        switch (element.getType()) {
            case NbtElement.END_TYPE: //0
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
                return new NBTListHelper((AbstractNbtList) element);
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

        public NBTNumberHelper(AbstractNbtNumber base) {
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
    public static class NBTListHelper extends NBTElementHelper<AbstractNbtList> {

        public NBTListHelper(AbstractNbtList base) {
            super(base);
        }

        /**
         * @return
         * @since 1.8.3
         */
        public boolean isPossiblyUUID() {
            return base.getType() == NbtElement.INT_ARRAY_TYPE && base.size() == 4;
        }

        /**
         * @return
         * @since 1.8.3
         */
        @Nullable
        public UUID asUUID() {
            if (!isPossiblyUUID()) {
                return null;
            }
            return Uuids.toUuid(base.asIntArray().orElseThrow());
        }

        /**
         * @return
         * @since 1.5.1
         */
        public int length() {
            return base.size();
        }

        /**
         * @since 1.5.1
         */
        @Nullable
        public NBTElementHelper<?> get(int index) {
            return resolve(base.get(index));
        }

        /**
         * @since 1.5.1
         */
        public int getHeldType() {
            return switch (base.getType()) {
                case NbtElement.BYTE_ARRAY_TYPE -> NbtElement.BYTE_TYPE;
                case NbtElement.INT_ARRAY_TYPE -> NbtElement.INT_TYPE;
                case NbtElement.LONG_ARRAY_TYPE -> NbtElement.LONG_TYPE;
                default -> NbtElement.COMPOUND_TYPE;
            };
        }

    }

    /**
     * @since 1.5.1
     */
    public static class NBTCompoundHelper extends NBTElementHelper<NbtCompound> {

        public NBTCompoundHelper(NbtCompound base) {
            super(base);
        }

        /**
         * @return
         * @since 1.6.0
         */
        public Set<String> getKeys() {
            return base.getKeys();
        }

        /**
         * @since 1.5.1
         */
        public int getType(String key) {
            var child = base.get(key);
            return child == null ? -1 : child.getType();
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
        @Nullable
        public NBTElementHelper<?> get(String key) {
            return resolve(base.get(key));
        }

        /**
         * @since 1.5.1
         */
        public String asString(String key) {
            var child = base.get(key);
            return child == null ? null : child.asString().orElseGet(child::toString);
        }

    }

}
