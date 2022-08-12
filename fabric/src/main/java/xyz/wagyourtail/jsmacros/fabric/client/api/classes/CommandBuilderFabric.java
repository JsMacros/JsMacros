package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import xyz.wagyourtail.jsmacros.client.access.CommandNodeAccessor;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.Stack;
import java.util.function.Supplier;

public class CommandBuilderFabric extends CommandBuilder {
    private final LiteralArgumentBuilder<FabricClientCommandSource> head;
    private final Stack<ArgumentBuilder<FabricClientCommandSource, ?>> pointer = new Stack<>();
    public CommandBuilderFabric(String name) {
        head = ClientCommandManager.literal(name);
        pointer.push(head);
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.argument(name, type.get());

        pointer.push(arg);
    }

    @Override
    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.literal(name);

        pointer.push(arg);
        return this;
    }

    @Override
    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        pointer.peek().executes((ctx) -> internalExecutes(ctx, callback));
        return this;
    }

    @Override
    protected <S> void suggests(SuggestionProvider<S> suggestionProvider) {
        ((RequiredArgumentBuilder)pointer.peek()).suggests(suggestionProvider);
    }

    @Override
    public CommandBuilder or() {
        if (pointer.size() > 1) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argLevel) {
        argLevel = Math.max(1, argLevel);
        while (pointer.size() > argLevel) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    @Override
    public void register() {
        or(1);
        ClientCommandManager.DISPATCHER.register(head);
        ClientPlayNetworkHandler cpnh = MinecraftClient.getInstance().getNetworkHandler();
        if (cpnh != null) {
            ClientCommandInternals.addCommands((CommandDispatcher) cpnh.getCommandDispatcher(), (FabricClientCommandSource) cpnh.getCommandSource());
        }
    }

    @Override
    public void unregister() throws IllegalAccessException {
        CommandNodeAccessor.remove(ClientCommandManager.DISPATCHER.getRoot(), head.getLiteral());
        ClientPlayNetworkHandler p = MinecraftClient.getInstance().getNetworkHandler();
        if (p != null) {
            CommandDispatcher<?> cd = p.getCommandDispatcher();
            CommandNodeAccessor.remove(cd.getRoot(), head.getLiteral());
        }
    }
}
