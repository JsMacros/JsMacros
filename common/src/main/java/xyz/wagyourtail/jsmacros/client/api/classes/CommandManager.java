package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @since 1.7.0
 */
public abstract class CommandManager {
    public static CommandManager instance;

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
}
