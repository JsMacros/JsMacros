package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.client.access.CommandNodeAccessor;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.language.EventContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;


public class CommandBuilderForge extends CommandBuilder {
    private static final Map<String, Supplier<ArgumentBuilder<ServerCommandSource, ?>>> commands = new HashMap<>();


    private final String name;

    private final Stack<Pair<Boolean, Supplier<ArgumentBuilder<ServerCommandSource, ?>>>> pointer = new Stack<>();


    public CommandBuilderForge(String name) {
        Supplier<ArgumentBuilder<ServerCommandSource, ?>> head = () -> LiteralArgumentBuilder.literal(name);
        this.name = name;
        pointer.push(new Pair<>(false, head));
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        pointer.push(new Pair<>(true, () -> RequiredArgumentBuilder.argument(name, type.get())));
    }

    @Override
    public CommandBuilder literalArg(String name) {
        pointer.push(new Pair<>(false, () -> LiteralArgumentBuilder.literal(name)));
        return this;
    }

    @Override
    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        Pair<Boolean, Supplier<ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
        Supplier<ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
        pointer.push(new Pair<>(arg.getT(), () -> u.get().executes((ctx) -> internalExecutes(ctx, callback))));
        return this;
    }

    @Override
    protected <S> void suggests(SuggestionProvider<S> suggestionProvider) {
        Pair<Boolean, Supplier<ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
        Supplier<ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
        if (!arg.getT()) throw new AssertionError("SuggestionProvider can only be used on non-literal arguments");
        pointer.push(new Pair<>(true, () -> ((RequiredArgumentBuilder)u.get()).suggests(suggestionProvider)));
    }

    @Override
    public CommandBuilder or() {
        if (pointer.size() > 1) {
            Supplier<ArgumentBuilder<ServerCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Supplier<ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
            Supplier<ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), () -> u.get().then(oldarg.get())));
        } else {
            throw new AssertionError("Can't use or() on the head of the command");
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argLevel) {
        argLevel = Math.max(1, argLevel);
        while (pointer.size() > argLevel) {
            Supplier<ArgumentBuilder<ServerCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Supplier<ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
            Supplier<ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), () -> u.get().then(oldarg.get())));
        }
        return this;
    }

    @Override
    public void register() {
        or(1);
        CommandDispatcher<ServerCommandSource> dispatcher = ClientCommandHandler.getDispatcher();
        Supplier<ArgumentBuilder<ServerCommandSource, ?>> head = pointer.pop().getU();
        if (dispatcher != null) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                LiteralArgumentBuilder lb = (LiteralArgumentBuilder) head.get();
                dispatcher.register(lb);
                networkHandler.getCommandDispatcher().register(lb);
            }
        }
        commands.put(name, head);
    }

    @Override
    public void unregister() throws IllegalAccessException {
        CommandNodeAccessor.remove(ClientCommandHandler.getDispatcher().getRoot(), name);
        ClientPlayNetworkHandler p = MinecraftClient.getInstance().getNetworkHandler();
        if (p != null) {
            CommandDispatcher<?> cd = p.getCommandDispatcher();
            CommandNodeAccessor.remove(cd.getRoot(), name);
        }
        commands.remove(name);
    }

    public static void onRegisterEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<ServerCommandSource> dispatcher = event.getDispatcher();
        for (Supplier<ArgumentBuilder<ServerCommandSource, ?>> command : commands.values()) {
            dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) command.get());
        }
    }

}