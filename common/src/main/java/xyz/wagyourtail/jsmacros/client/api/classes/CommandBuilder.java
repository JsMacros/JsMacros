package xyz.wagyourtail.jsmacros.client.api.classes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.4.2
 */
 @SuppressWarnings("unused")
public abstract class CommandBuilder {

    protected abstract void argument(String name, Supplier<ArgumentType<?>> type);

    public abstract CommandBuilder literalArg(String name);

    public CommandBuilder angleArg(String name) {
        argument(name, AngleArgumentType::angle);
        return this;
    }

    public CommandBuilder blockArg(String name) {
        argument(name, BlockStateArgumentType::blockState);
        return this;
    }

    public CommandBuilder booleanArg(String name) {
        argument(name, BoolArgumentType::bool);
        return this;
    }

    public CommandBuilder colorArg(String name) {
        argument(name, ColorArgumentType::color);
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
        argument(name, NumberRangeArgumentType::floatRange);
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
        argument(name, IdentifierArgumentType::identifier);
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
        argument(name, NumberRangeArgumentType::intRange);
        return this;
    }

    public CommandBuilder itemArg(String name) {
        argument(name, ItemStackArgumentType::itemStack);
        return this;
    }

    public CommandBuilder nbtArg(String name) {
        argument(name, NbtCompoundArgumentType::nbtCompound);
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
        argument(name, TextArgumentType::text);
        return this;
    }

    public CommandBuilder uuidArgType(String name) {
        argument(name, UuidArgumentType::uuid);
        return this;
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
        suggests((ctx, builder) -> CommandSource.suggestMatching(Arrays.asList(suggestions), builder));
        return this;
    }

    /**
     * @since 1.6.5
     * @param suggestions
     *
     * @return
     */
    public CommandBuilder suggestIdentifier(String... suggestions) {
        suggests((ctx, builder) -> CommandSource.suggestIdentifiers(Arrays.stream(suggestions).map(Identifier::new), builder));
        return this;
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

    private static class RegexArgType implements ArgumentType<String[]> {

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
                throw new SimpleCommandExceptionType(new TranslatableText("jsmacros.commandfailedregex", "/" + pattern.pattern() + "/")).createWithContext(reader);
            }
        }
    }

    public abstract void register();

    /**
     * @since 1.6.5
     * removes this command
     */
    public abstract void unregister() throws IllegalAccessException;
}
