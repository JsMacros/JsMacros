package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.Command;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.jsmacros.client.access.CommandNodeAccessor;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommandBuilderForge extends CommandBuilder {
    public static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final LiteralArgumentBuilder<CommandSource> head;
    private final Stack<ArgumentBuilder<CommandSource, ?>> pointer = new Stack<>();

    public CommandBuilderForge(String name) {
        head = LiteralArgumentBuilder.literal(name);
        pointer.push(head);
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<CommandSource, ?> arg = RequiredArgumentBuilder.argument(name, type.get());

        pointer.push(arg);
    }

    @Override
    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<CommandSource, ?> arg = LiteralArgumentBuilder.literal(name);

        pointer.push(arg);
        return this;
    }

    @Override
    protected <S> void suggests(SuggestionProvider<S> suggestionProvider) {
        ((RequiredArgumentBuilder)pointer.peek()).suggests(suggestionProvider);
    }

    @Override
    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        pointer.peek().executes((ctx) -> internalExecutes(ctx, callback));
        return this;
    }

    @Override
    public CommandBuilder or() {
        if (pointer.size() > 1) {
            ArgumentBuilder<CommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argumentLevel) {
        argumentLevel = Math.max(1, argumentLevel);
        while (pointer.size() > argumentLevel) {
            ArgumentBuilder<CommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public void register() {or(1);
        LiteralCommandNode<CommandSource> node = dispatcher.register(head);
        ClientCommandHandler.instance.registerCommand(new Command() {
            @Override
            public int compareTo(@NotNull Command o) {
                return 0;
            }

            @Override
            public String getCommandName() {
                return node.getName();
            }

            @Override
            public String getUsageTranslationKey(CommandSource sender) {
                return node.getUsageText();
            }

            @Override
            public List<String> getAliases() {
                return new ArrayList<>();
            }

            @Override
            public void method_3279(MinecraftServer minecraftServer, CommandSource sender, String[] args) throws CommandException {
                try {
                    dispatcher.execute(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
                } catch (CommandSyntaxException e) {
                    throw new CommandException(e.getMessage());
                }
            }

            @Override
            public boolean method_3278(MinecraftServer server, CommandSource source) {
                return true;
            }

            @Override
            public List<String> method_10738(MinecraftServer server, CommandSource sender, String[] args, @Nullable BlockPos pos) {
                ParseResults<CommandSource> pr = dispatcher.parse(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
                try {
                    return dispatcher.getCompletionSuggestions(pr).get().getList().stream().map(Suggestion::getText).collect(
                        Collectors.toList());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    throw new RuntimeException("error");
                }
            }

            @Override
            public boolean isUsernameAtIndex(String[] args, int index) {
                return false;
            }
        });
    }

    @Override
    public void unregister() throws IllegalAccessException {
        CommandNodeAccessor.remove(dispatcher.getRoot(), head.getLiteral());
        CommandNodeAccessor.removeCommand(ClientCommandHandler.instance, head.getLiteral());
    }
}
