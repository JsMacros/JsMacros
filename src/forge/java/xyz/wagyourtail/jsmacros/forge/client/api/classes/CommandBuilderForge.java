package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.neoforged.neoforge.client.ClientCommandHandler;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.client.access.CommandNodeAccessor;
import xyz.wagyourtail.jsmacros.client.api.classes.inventory.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandContextHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommandBuilderForge extends CommandBuilder {
    private static final Map<String, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>> commands = new HashMap<>();

    private final String name;

    private final Stack<Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>>> pointer = new Stack<>();

    public CommandBuilderForge(String name) {
        Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> head = (a) -> LiteralArgumentBuilder.literal(name);
        this.name = name;
        pointer.push(new Pair<>(false, head));
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        pointer.push(new Pair<>(true, (e) -> RequiredArgumentBuilder.argument(name, type.get())));
    }

    @Override
    protected void argument(String name, Function<CommandRegistryAccess, ArgumentType<?>> type) {
        pointer.push(new Pair<>(true, (e) -> RequiredArgumentBuilder.argument(name, type.apply(e))));
    }

    @Override
    public CommandBuilder literalArg(String name) {
        pointer.push(new Pair<>(false, (e) -> LiteralArgumentBuilder.literal(name)));
        return this;
    }

    @Override
    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
        pointer.push(new Pair<>(arg.getT(), arg.getU().andThen((e) -> e.executes((ctx) -> internalExecutes(ctx, callback)))));
        return this;
    }

    @Override
    protected <S> void suggests(SuggestionProvider<S> suggestionProvider) {
        Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
        if (!arg.getT()) {
            throw new AssertionError("SuggestionProvider can only be used on non-literal arguments");
        }
        pointer.push(new Pair<>(true, arg.getU().andThen((e) -> ((RequiredArgumentBuilder) e).suggests(suggestionProvider))));
    }

    @Override
    public CommandBuilder or() {
        if (pointer.size() > 1) {
            Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
            Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), (ctx) -> u.andThen((e) -> e.then(oldarg.apply(ctx))).apply(ctx)));
        } else {
            throw new AssertionError("Can't use or() on the head of the command");
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argLevel) {
        argLevel = Math.max(1, argLevel);
        while (pointer.size() > argLevel) {
            Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>>> arg = pointer.pop();
            Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), (ctx) -> u.andThen((e) -> e.then(oldarg.apply(ctx))).apply(ctx)));
        }
        return this;
    }

    @Override
    public void register() {
        or(1);
        CommandDispatcher<ServerCommandSource> dispatcher = ClientCommandHandler.getDispatcher();
        Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> head = pointer.pop().getU();
        if (dispatcher != null) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler != null) {
                LiteralArgumentBuilder lb = (LiteralArgumentBuilder) head.apply(CommandRegistryAccess.of(networkHandler.getRegistryManager(), networkHandler.getEnabledFeatures()));
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
        MinecraftClient mc = MinecraftClient.getInstance();
        CommandRegistryAccess registryAccess = CommandRegistryAccess.of(mc.getNetworkHandler().getRegistryManager(), mc.getNetworkHandler().getEnabledFeatures());
        for (Function<CommandRegistryAccess, ArgumentBuilder<ServerCommandSource, ?>> command : commands.values()) {
            dispatcher.register((LiteralArgumentBuilder<ServerCommandSource>) command.apply(registryAccess));
        }
    }

}
