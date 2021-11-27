package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.Stack;
import java.util.function.Supplier;

public class CommandBuilderFabric extends CommandBuilder {
    private final LiteralArgumentBuilder<FabricClientCommandSource> head;
    private final Stack<ArgumentBuilder<FabricClientCommandSource, ?>> pointer = new Stack<>();

    public CommandBuilderFabric(String name) {
        head = ClientCommandManager.literal(name);
        pointer.push(head);
    }

    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.argument(name, type.get());

        pointer.push(arg);
    }

    public CommandBuilder literalArg(String name) {
        ArgumentBuilder<FabricClientCommandSource, ?> arg = ClientCommandManager.literal(name);

        pointer.push(arg);
        return this;
    }

    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Boolean, ?> callback) {
        pointer.peek().executes((ctx) -> internalExecutes(ctx, callback));
        return this;
    }

    public CommandBuilder or() {
        if (pointer.size() > 1) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    public CommandBuilder or(int argLevel) {
        argLevel = Math.max(1, argLevel);
        while (pointer.size() > argLevel) {
            ArgumentBuilder<FabricClientCommandSource, ?> oldarg = pointer.pop();
            pointer.peek().then(oldarg);
        }
        return this;
    }

    public void register() {
        or(1);
        ClientCommandManager.DISPATCHER.register(head);
        ClientPlayNetworkHandler cpnh = MinecraftClient.getInstance().getNetworkHandler();
        if (cpnh != null) {
            ClientCommandInternals.addCommands((CommandDispatcher) cpnh.getCommandDispatcher(), (FabricClientCommandSource) cpnh.getCommandSource());
        }
    }
}
