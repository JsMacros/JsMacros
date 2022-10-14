package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.BuiltInExceptions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.SuggestionsBuilderHelper;
import xyz.wagyourtail.jsmacros.core.EventLockWatchdog;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.config.CoreConfigV2;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @since 1.4.2
 */
@SuppressWarnings("unused")
public abstract class CommandBuilder {

    private static final BuiltInExceptions exception = new BuiltInExceptions();

    protected abstract void argument(String name, Supplier<ArgumentType<?>> type);

    public abstract CommandBuilder literalArg(String name);

    public CommandBuilder angleArg(String name) {
        throw new NullPointerException("does not exist in 1.16.1");
    }

    public CommandBuilder blockArg(String name) {
        argument(name, BlockArgumentType::new);
        return this;
    }

    public CommandBuilder booleanArg(String name) {
        argument(name, BoolArgumentType::bool);
        return this;
    }

    public CommandBuilder colorArg(String name) {
        argument(name, ColorArgumentType::new);
        return this;
    }

    public CommandBuilder doubleArg(String name) {
        argument(name, DoubleArgumentType::doubleArg);
        return this;
    }

    public CommandBuilder doubleArg(String name, double min, double max) {
        argument(name, () -> DoubleArgumentType.doubleArg(min, max));
        return this;
    }

    public CommandBuilder floatRangeArg(String name) {
        argument(name, NumberRangeArgumentType.FloatRangeArgumentType::new);
        return this;
    }

    public CommandBuilder longArg(String name) {
        argument(name, LongArgumentType::longArg);
        return this;
    }

    public CommandBuilder longArg(String name, long min, long max) {
        argument(name, () -> LongArgumentType.longArg(min, max));
        return this;
    }

    public CommandBuilder identifierArg(String name) {
        argument(name, IdentifierArgumentType::new);
        return this;
    }

    public CommandBuilder intArg(String name) {
        argument(name, IntegerArgumentType::integer);
        return this;
    }

    public CommandBuilder intArg(String name, int min, int max) {

        argument(name, () -> IntegerArgumentType.integer(min, max));
        return this;
    }

    public CommandBuilder intRangeArg(String name) {
        argument(name, NumberRangeArgumentType.IntegerRangeArgumentType::new);
        return this;
    }

    public CommandBuilder itemArg(String name) {
        argument(name, ItemArgumentType::new);
        return this;
    }

    public CommandBuilder nbtArg(String name) {
        argument(name, NBTArgumentType::new);
        return this;
    }

    public CommandBuilder greedyStringArg(String name) {
        argument(name, StringArgumentType::greedyString);
        return this;
    }

    public CommandBuilder quotedStringArg(String name) {
        argument(name, StringArgumentType::string);
        return this;
    }

    public CommandBuilder wordArg(String name) {
        argument(name, StringArgumentType::word);
        return this;
    }

    public CommandBuilder textArgType(String name) {
        argument(name, TextArgumentType::new);
        return this;
    }

    public CommandBuilder uuidArgType(String name) {
        throw new NullPointerException("does not exist in 1.15.2");
    }

    public CommandBuilder regexArgType(String name, String regex, String flags) {
        int fg = 0;
        for (int i = 0; i < flags.length(); ++i) {
            switch (flags.charAt(i)) {
                case 'i':
                    fg += Pattern.CASE_INSENSITIVE;
                    break;
                case 's':
                    fg += Pattern.DOTALL;
                    break;
                case 'u':
                    fg += Pattern.UNICODE_CHARACTER_CLASS;
                    break;
            }
        }
        int finalFg = fg;
        argument(name, () -> new RegexArgType(regex, finalFg));
        return this;
    }

    /**
     *
     * it is recomended to use {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#runScript(String, BaseEvent)}
     * in the callback if you expect to actually do anything complicated with waits.
     *
     * the {@link CommandContextHelper} arg is an {@link BaseEvent}
     * so you can pass it directly to {@link xyz.wagyourtail.jsmacros.core.library.impl.FJsMacros#runScript(String, BaseEvent)}.
     *
     * make sure your callback returns a boolean success = true.
     *
     * @param callback
     *
     * @return
     */
    public abstract CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback);

    protected abstract <S> void suggests(SuggestionProvider<S> suggestionProvider);

    /**
     * @since 1.6.5
     * @param suggestions
     *
     * @return
     */
    public CommandBuilder suggestMatching(String... suggestions) {
        suggests((ctx, builder) -> suggestMatching(Arrays.asList(suggestions), builder));
        return this;
    }

    static CompletableFuture<Suggestions> suggestMatching(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

        for(String string2 : iterable) {
            if (method_27136(string, string2.toLowerCase(Locale.ROOT))) {
                suggestionsBuilder.suggest(string2);
            }
        }

        return suggestionsBuilder.buildFuture();
    }

    static boolean method_27136(String string, String string2) {
        for(int i = 0; !string2.startsWith(string, i); ++i) {
            i = string2.indexOf(95, i);
            if (i < 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * @since 1.6.5
     * @param suggestions
     *
     * @return
     */
    public CommandBuilder suggestIdentifier(String... suggestions) {
        suggests((ctx, builder) -> suggestIdentifiers(Arrays.stream(suggestions).map(ResourceLocation::new)::iterator, builder));
        return this;
    }


    static CompletableFuture<Suggestions> suggestIdentifiers(Iterable<ResourceLocation> candidates, SuggestionsBuilder builder) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        forEachMatching(candidates, string, identifier -> identifier, identifier -> builder.suggest(identifier.toString()));
        return builder.buildFuture();
    }

    static <T> void forEachMatching(Iterable<T> candidates, String string, Function<T, ResourceLocation> identifier, Consumer<T> action) {
        boolean bl = string.indexOf(58) > -1;

        for(T object : candidates) {
            ResourceLocation identifier2 = identifier.apply(object);
            if (bl) {
                String string2 = identifier2.toString();
                if (method_27136(string, string2)) {
                    action.accept(object);
                }
            } else if (method_27136(string, identifier2.getNamespace())
                || identifier2.getNamespace().equals("minecraft") && method_27136(string, identifier2.getPath())) {
                action.accept(object);
            }
        }

    }

    /**
     * @since 1.6.5
     * @param callback
     *
     * @return
     */
    public CommandBuilder suggest(MethodWrapper<CommandContextHelper, SuggestionsBuilderHelper, Object, ?> callback) {
        suggests((ctx, builder) -> {
            callback.accept(new CommandContextHelper(ctx), new SuggestionsBuilderHelper(builder));
            return builder.buildFuture();
        });
        return this;
    }

    protected <S> int internalExecutes(CommandContext<S> context, MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        EventContainer<?> lock = new EventContainer<>(callback.getCtx());
        lock.setLockThread(Thread.currentThread());
        EventLockWatchdog.startWatchdog(lock, new IEventListener() {
            @Override
            public EventContainer<?> trigger(BaseEvent event) {
                return null;
            }

            @Override
            public String toString() {
                return "CommandBuilder{\"called_by\": " + callback.getCtx().getTriggeringEvent().toString() + "}";
            }
        }, Core.getInstance().config.getOptions(CoreConfigV2.class).maxLockTime);
        try {
            callback.accept(new CommandContextHelper(context));
        } finally {
            lock.releaseLock();
        }
        return 1;
    }


    public abstract CommandBuilder or();

    /**
     * name overload for {@link #or()} to work around language keyword restrictions
     * @since 1.5.2
     * @return
     */
    public CommandBuilder otherwise() {
        or();
        return this;
    }

    public abstract CommandBuilder or(int argumentLevel);

    /**
     * name overload for {@link #or(int)} to work around language keyword restrictions
     * @since 1.5.2
     * @param argLevel
     * @return
     */
    public CommandBuilder otherwise(int argLevel) {
        or(argLevel);
        return this;
    }

    public abstract void register();

    private class RegexArgType implements ArgumentType<String[]> {

        Pattern pattern;

        public RegexArgType(String regex, int flags) {
            this.pattern = Pattern.compile(regex, flags);
        }

        @Override
        public String[] parse(StringReader reader) throws CommandSyntaxException {
            int i = reader.getCursor();
            Matcher m = pattern.matcher(reader.getRemaining());
            if (m.find() && m.start() == 0) {
                String[] args = new String[m.groupCount() + 1];
                for (int j = 0; j < args.length; ++j) {
                    args[j] = m.group(j);
                }
                reader.setCursor(i + m.group(0).length());
                return args;
            } else {
                throw new SimpleCommandExceptionType(new ChatComponentTranslation("jsmacros.commandfailedregex", "/" + pattern.pattern() + "/")::getString).createWithContext(reader);
            }
        }
    }

    private class BlockArgumentType implements ArgumentType<Block> {



        @Override
        public Block parse(StringReader reader) throws CommandSyntaxException {
            try {
                return CommandBase.getBlock(null, reader.readStringUntil(' '));
            } catch (NumberInvalidException e) {
                throw exception.readerInvalidInt().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.func_175762_a(new String[] {builder.getRemaining()}, Block.REGISTRY.keySet()).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }
    }

    public class ColorArgumentType implements ArgumentType<EnumChatFormatting> {

        @Override
        public EnumChatFormatting parse(StringReader reader) throws CommandSyntaxException {
            try {
                return EnumChatFormatting.byName(reader.readStringUntil(' '));
            } catch (IllegalArgumentException e) {
                throw exception.readerExpectedSymbol().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.func_175762_a(new String[] {builder.getRemaining()}, Arrays.stream(EnumChatFormatting.values()).map(EnumChatFormatting::getName).collect(Collectors.toList())).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }

    }

    public class NBTArgumentType implements ArgumentType<NBTTagCompound> {

        @Override
        public NBTTagCompound parse(StringReader reader) throws CommandSyntaxException {
            try {
                int cursor = reader.getCursor();
                String s = reader.getRemaining();
                if (!s.startsWith("{")) throw exception.readerExpectedSymbol().create("{");
                Matcher m = Pattern.compile("[{}]").matcher(s);
                int i = 0;
                while (m.find()) {
                    if (m.group().equals("{")) ++i;
                    else if (--i == 0) {
                        break;
                    }
                }
                reader.setCursor(cursor + m.end());
                return JsonToNBT.parse(s.substring(0, m.end()));
            } catch (NBTException e) {
                throw exception.readerExpectedSymbol().create(e.getStackTrace());
            }
        }
    }

    public class TextArgumentType implements ArgumentType<IChatComponent> {

        @Override
        public IChatComponent parse(StringReader reader) throws CommandSyntaxException {
            int cursor = reader.getCursor();
            String s = reader.getRemaining();
            if (s.startsWith("\"")) {
                return new ChatComponentText(reader.readQuotedString());
            }
            if (!s.startsWith("{") && !s.startsWith("[")) throw exception.readerExpectedSymbol().create("{");
            Matcher m = Pattern.compile("[\\[\\]{}]").matcher(s);
            int i = 0;
            while (m.find()) {
                if (m.group().equals("{") || m.group().equals("[")) ++i;
                else if (--i == 0) {
                    break;
                }
            }
            reader.setCursor(cursor + m.end());
            try {
                return IChatComponent.Serializer.deserialize(s.substring(0, m.end()));
            } catch (JsonSyntaxException e) {
                throw exception.readerExpectedSymbol().create(e.getMessage());
            }
        }
    }

    public class ItemArgumentType implements ArgumentType<Item> {

        @Override
        public Item parse(StringReader reader) throws CommandSyntaxException {
            try {
                return CommandBase.getItem(null, reader.readStringUntil(' '));
            } catch (NumberInvalidException e) {
                throw exception.readerInvalidInt().create(e.getMessage());
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return CompletableFuture.supplyAsync(() -> {
                List<Suggestion> sugs = CommandBase.func_175762_a(new String[] {builder.getRemaining()}, Item.REGISTRY.keySet()).stream().map(e -> new Suggestion(null, e)).collect(Collectors.toList());
                return new Suggestions(null, sugs);
            });
        }

    }

    public class IdentifierArgumentType implements ArgumentType<ResourceLocation> {

        @Override
        public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
            return new ResourceLocation(reader.readStringUntil(' '));
        }

    }

    public interface NumberRangeArgumentType<T extends NumberRangeArgumentType.NumberRange<?>> extends ArgumentType<T> {


        class FloatRangeArgumentType implements NumberRangeArgumentType<NumberRange.FloatRange> {

            @Override
            public NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
                return NumberRange.FloatRange.parse(reader);
            }

        }

        class IntegerRangeArgumentType implements NumberRangeArgumentType<NumberRange.IntRange> {

            @Override
            public NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
                return NumberRange.IntRange.parse(reader);
            }

        }


        abstract class NumberRange<U> {
            protected final U min;
            private final U max;

            protected NumberRange(U min, U max) {
                this.min = min;
                this.max = max;
            }

            public U getMin() {
                return min;
            }

            public U getMax() {
                return max;
            }

            public boolean isDummy() {
                return this.min == null && this.max == null;
            }

            protected static <T extends Number, R extends NumberRange<T>> R parse(StringReader commandReader, CommandFactory<T, R> commandFactory, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier, Function<T, T> mapper) throws CommandSyntaxException {
                if (!commandReader.canRead()) {
                    throw exception.dispatcherUnknownArgument().create();
                } else {
                    int i = commandReader.getCursor();

                    try {
                        T number = map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
                        T number3;
                        if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
                            commandReader.skip();
                            commandReader.skip();
                            number3 = map(fromStringReader(commandReader, converter, exceptionTypeSupplier), mapper);
                            if (number == null && number3 == null) {
                                throw exception.dispatcherParseException().create("ended early");
                            }
                        } else {
                            number3 = number;
                        }

                        if (number == null && number3 == null) {
                            throw exception.dispatcherParseException().create("ended early");
                        } else {
                            return commandFactory.create(commandReader, number, number3);
                        }
                    } catch (CommandSyntaxException var8) {
                        commandReader.setCursor(i);
                        throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
                    }
                }
            }

            @Nullable
            private static <T extends Number> T fromStringReader(StringReader reader, Function<String, T> converter, Supplier<DynamicCommandExceptionType> exceptionTypeSupplier) throws CommandSyntaxException {
                int i = reader.getCursor();

                while(reader.canRead() && isNextCharValid(reader)) {
                    reader.skip();
                }

                String string = reader.getString().substring(i, reader.getCursor());
                if (string.isEmpty()) {
                    return null;
                } else {
                    try {
                        return converter.apply(string);
                    } catch (NumberFormatException var6) {
                        throw exceptionTypeSupplier.get().createWithContext(reader, string);
                    }
                }
            }

            private static boolean isNextCharValid(StringReader reader) {
                char c = reader.peek();
                if ((c < '0' || c > '9') && c != '-') {
                    if (c != '.') {
                        return false;
                    } else {
                        return !reader.canRead(2) || reader.peek(1) != '.';
                    }
                } else {
                    return true;
                }
            }

            @Nullable
            private static <T> T map(@Nullable T object, Function<T, T> function) {
                return object == null ? null : function.apply(object);
            }



            public static class FloatRange extends NumberRange<Float> {

                protected FloatRange(Float min, Float max) {
                    super(min, max);
                }

                private static NumberRange.FloatRange create(StringReader reader, @Nullable Float min, @Nullable Float max) throws CommandSyntaxException {
                    if (min != null && max != null && min > max) {
                        throw exception.readerInvalidFloat().create(max);
                    } else {
                        return new NumberRange.FloatRange(min, max);
                    }
                }

                public static NumberRange.FloatRange parse(StringReader reader) throws CommandSyntaxException {
                    return parse(reader, (float_) -> float_);
                }

                public static NumberRange.FloatRange parse(StringReader reader, Function<Float, Float> mapper) throws CommandSyntaxException {
                    return parse(reader, FloatRange::create, Float::parseFloat, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidFloat, mapper);
                }
            }

            public static class IntRange extends NumberRange<Integer> {

                protected IntRange(Integer min, Integer max) {
                    super(min, max);
                }

                private static NumberRange.IntRange parse(StringReader reader, @Nullable Integer min, @Nullable Integer max) throws CommandSyntaxException {
                    if (min != null && max != null && min > max) {
                        throw exception.readerInvalidInt().create(max);
                    } else {
                        return new NumberRange.IntRange(min, max);
                    }
                }

                public static NumberRange.IntRange parse(StringReader reader) throws CommandSyntaxException {
                    return fromStringReader(reader, (integer) -> integer);
                }

                public static NumberRange.IntRange fromStringReader(StringReader reader, Function<Integer, Integer> converter) throws CommandSyntaxException {
                    //this is not redundant, it fails to compile without -_-
                    return NumberRange.<Integer, IntRange>parse(reader, IntRange::parse, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, converter);
                }

            }
        }

        @FunctionalInterface
        interface CommandFactory<T extends Number, R extends NumberRange<T>> {
            R create(StringReader reader, @Nullable T min, @Nullable T max) throws CommandSyntaxException;
        }
    }

    /**
     * @since 1.6.5
     * removes this command
     */
    public abstract void unregister() throws IllegalAccessException;
}
