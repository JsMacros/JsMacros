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
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.ClientCommandHandler;
import org.jetbrains.annotations.NotNull;
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
    private static final CommandDispatcher<ICommandSender> dispatcher = new CommandDispatcher<>();
    private final LiteralArgumentBuilder<ICommandSender> head;
    private final Stack<ArgumentBuilder<ICommandSender, ?>> pointer = new Stack<>();

    public CommandBuilderForge(String name) {
        head = LiteralArgumentBuilder.literal(name);
        pointer.push(head);
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<ICommandSender, ?> arg = RequiredArgumentBuilder.argument(name, type.get());

        pointer.push(arg);
    }

    @Override
    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<ICommandSender, ?> arg = LiteralArgumentBuilder.literal(name);

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
            ArgumentBuilder<ICommandSender, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argumentLevel) {
        argumentLevel = Math.max(1, argumentLevel);
        while (pointer.size() > argumentLevel) {
            ArgumentBuilder<ICommandSender, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public void register() {or(1);
        LiteralCommandNode<ICommandSender> node = dispatcher.register(head);
        ClientCommandHandler.instance.registerCommand(new ICommand() {
            @Override
            public String getCommandName() {
                return node.getName();
            }

            @Override
            public String getUsageTranslationKey(ICommandSender sender) {
                return node.getUsageText();
            }

            @Override
            public List<String> getAliases() {
                return new ArrayList<>();
            }

            @Override
            public void execute(ICommandSender sender, String[] args) throws CommandException {
                try {
                    dispatcher.execute(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
                } catch (CommandSyntaxException e) {
                    throw new CommandException(e.getMessage());
                }
            }

            @Override
            public boolean isAccessible(ICommandSender sender) {
                return true;
            }

            @Override
            public List<String> getAutoCompleteHints(ICommandSender sender, String[] args, BlockPos pos) {
                ParseResults<ICommandSender> pr = dispatcher.parse(getCommandName() + (args.length > 0 ? " " + String.join(" ", args) : ""), sender);
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

            @Override
            public int compareTo(@NotNull ICommand o) {
                return getCommandName().compareTo(o.getCommandName());
            }
        });
    }

    @Override
    public void unregister() throws IllegalAccessException {
        CommandNodeAccessor.remove(dispatcher.getRoot(), head.getLiteral());
        CommandNodeAccessor.removeCommand(ClientCommandHandler.instance, head.getLiteral());
    }
}
