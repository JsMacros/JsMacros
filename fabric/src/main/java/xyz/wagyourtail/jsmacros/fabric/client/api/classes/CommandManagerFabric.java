package xyz.wagyourtail.jsmacros.fabric.client.api.classes;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import xyz.wagyourtail.jsmacros.client.access.CommandNodeAccessor;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;

public class CommandManagerFabric extends CommandManager {


    @Override
    public CommandBuilder createCommandBuilder(String name) {
        return new CommandBuilderFabric(name);
    }

    @Override
    public CommandNodeHelper unregisterCommand(String command) throws IllegalAccessException {
        CommandNode<?> cnf = CommandNodeAccessor.remove(ClientCommandManager.DISPATCHER.getRoot(), command);
        CommandNode<?> cn = null;
        ClientPlayNetworkHandler p = MinecraftClient.getInstance().getNetworkHandler();
        if (p != null) {
            CommandDispatcher<?> cd = p.getCommandDispatcher();
            cn = CommandNodeAccessor.remove(cd.getRoot(), command);
        }
        return cn != null || cnf != null ? new CommandNodeHelper(cn, cnf) : null;
    }

    @Override
    public void reRegisterCommand(CommandNodeHelper node) {
        if (node.fabric != null) {
            ClientCommandManager.DISPATCHER.getRoot().addChild(node.fabric);
        }
        ClientPlayNetworkHandler nh = MinecraftClient.getInstance().getNetworkHandler();
        if (nh != null) {
            CommandDispatcher<?> cd = nh.getCommandDispatcher();
            if (node.getRaw() != null) {
                cd.getRoot().addChild((CommandNode) node.getRaw());
            }
        }
    }

}
