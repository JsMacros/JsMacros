package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandRegistryAccess;
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

public class CommandBuilderFabric extends CommandBuilder {
    private static final Map<String, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>> commands = new HashMap<>();

    private final String name;
    private final Stack<Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>>> pointer = new Stack<>();

    public CommandBuilderFabric(String name) {
        Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> head = (a) -> ClientCommandManager.literal(name);
        this.name = name;
        pointer.push(new Pair<>(false, head));
    }

    @Override
    protected void argument(String name, Supplier<ArgumentType<?>> type) {
        pointer.push(new Pair<>(true, (e) -> ClientCommandManager.argument(name, type.get())));
    }

    @Override
    protected void argument(String name, Function<CommandRegistryAccess, ArgumentType<?>> type) {
        pointer.push(new Pair<>(true, (e) -> ClientCommandManager.argument(name, type.apply(e))));
    }

    @Override
    public CommandBuilder literalArg(String name) {
        pointer.push(new Pair<>(false, (e) -> ClientCommandManager.literal(name)));
        return this;
    }

    @Override
    public CommandBuilder executes(MethodWrapper<CommandContextHelper, Object, Object, ?> callback) {
        Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>> arg = pointer.pop();
        pointer.push(new Pair<>(arg.getT(), arg.getU().andThen((e) -> e.executes((ctx) -> internalExecutes(ctx, callback)))));
        return this;
    }

    @Override
    protected <S> void suggests(SuggestionProvider<S> suggestionProvider) {
        Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>> arg = pointer.pop();
        if (!arg.getT()) {
            throw new AssertionError("SuggestionProvider can only be used on non-literal arguments");
        }
        pointer.push(new Pair<>(true, arg.getU().andThen((e) -> ((RequiredArgumentBuilder) e).suggests(suggestionProvider))));
    }

    @Override
    public CommandBuilder or() {
        if (pointer.size() > 1) {
            Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>> arg = pointer.pop();
            Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), (ctx) -> u.andThen((e) -> e.then(oldarg.apply(ctx))).apply(ctx)));
        }
        return this;
    }

    @Override
    public CommandBuilder or(int argLevel) {
        argLevel = Math.max(1, argLevel);
        while (pointer.size() > argLevel) {
            Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> oldarg = pointer.pop().getU();
            Pair<Boolean, Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>>> arg = pointer.pop();
            Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> u = arg.getU();
            pointer.push(new Pair<>(arg.getT(), (ctx) -> u.andThen((e) -> e.then(oldarg.apply(ctx))).apply(ctx)));
        }
        return this;
    }

    @Override
    public void register() {
        or(1);
        CommandDispatcher<FabricClientCommandSource> dispatcher = ClientCommandManager.getActiveDispatcher();
        Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> head = pointer.pop().getU();
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
        CommandNodeAccessor.remove(ClientCommandManager.getActiveDispatcher().getRoot(), name);
        ClientPlayNetworkHandler p = MinecraftClient.getInstance().getNetworkHandler();
        if (p != null) {
            CommandDispatcher<?> cd = p.getCommandDispatcher();
            CommandNodeAccessor.remove(cd.getRoot(), name);
        }
        commands.remove(name);
    }

    public static void registerEvent() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (Function<CommandRegistryAccess, ArgumentBuilder<FabricClientCommandSource, ?>> command : commands.values()) {
                dispatcher.register((LiteralArgumentBuilder<FabricClientCommandSource>) command.apply(registryAccess));
            }
        });
    }

}
