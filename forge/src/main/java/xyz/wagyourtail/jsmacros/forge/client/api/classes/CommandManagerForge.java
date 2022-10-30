package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import net.minecraft.command.Command;
import net.minecraft.server.command.CommandRegistry;
import net.minecraftforge.client.ClientCommandHandler;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandManager;
import xyz.wagyourtail.jsmacros.client.api.helpers.CommandNodeHelper;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class CommandManagerForge extends CommandManager {

    @Override
    public CommandBuilder createCommandBuilder(String name) {
        return new CommandBuilderForge(name);
    }

    @Override
    public CommandNodeHelper unregisterCommand(String command) {
        Command c = ClientCommandHandler.instance.getCommandMap().get(command);
        if (c == null) {
            return null;
        }
        deleteCommand(c);
        return new CommandNodeHelper(c, true);
    }

    @Override
    public void reRegisterCommand(CommandNodeHelper node) {
        Command c = node.getRaw();
        if (c == null) {
            return;
        }
        ClientCommandHandler.instance.registerCommand(c);
    }

    private static Field commands;

    private static void deleteCommand(Command command) {
        if (commands == null) {
            boolean found = false;
            for (Field declaredField : CommandRegistry.class.getDeclaredFields()) {
                if (declaredField.getType() == Set.class) {
                    commands = declaredField;
                    commands.setAccessible(true);
                    found = true;
                    break;
                }
            }
            //else
            if (!found) {
                throw new RuntimeException("Could not find commands field");
            }
        }
        try {
            Set<Command> commands = (Set<Command>) CommandRegistry.class.getDeclaredFields()[0].get(null);
            commands.remove(command);
            Map<String, Command> map = ClientCommandHandler.instance.getCommandMap();
            map.remove(command.getCommandName());
            for (String alias : command.getAliases()) {
                map.remove(alias);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
