package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.server.command.CommandSource;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @since 1.7.0
 */
public abstract class CommandManager {
    public static CommandManager instance;
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * @since 1.7.0
     * @return list of commands
     */
    public List<String> getValidCommands() {
        ClientPlayNetworkHandler nh = MinecraftClient.getInstance().getNetworkHandler();
        if (nh == null) {
            return ImmutableList.of();
        }
        return nh.getCommandDispatcher().getRoot().getChildren().stream().map(CommandNode::getName).collect(Collectors.toList());
    }

    /**
     * @param name
     * @since 1.7.0
     * @return
     */
    public abstract CommandBuilder createCommandBuilder(String name);

    /**
     * @param command
     *
     * @return
     * @since 1.7.0
     * @throws IllegalAccessException
     */
    public abstract CommandNodeHelper unregisterCommand(String command) throws IllegalAccessException;

    /**
     * warning: this method is hacky
     * @since 1.7.0
     * @param node
     */
    public abstract void reRegisterCommand(CommandNodeHelper node);

    /**
     * @since 1.8.2
     * @param commandPart
     */
    public void getArgumentAutocompleteOptions(String commandPart, MethodWrapper<List<String>, Object, Object, ?> callback) {
        assert mc.player != null;
        CommandDispatcher<CommandSource> commandDispatcher = mc.player.networkHandler.getCommandDispatcher();
        ParseResults<CommandSource> parse = commandDispatcher.parse(commandPart, mc.player.networkHandler.getCommandSource());
        CompletableFuture<Suggestions> suggestions = commandDispatcher.getCompletionSuggestions(parse);
        suggestions.thenAccept(
                (s) -> {
                    List<String> list = s.getList().stream().map(Suggestion::getText).collect(Collectors.toList());
                    callback.accept(list);
                }
        );
    }
}
