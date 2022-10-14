package xyz.wagyourtail.jsmacros.forge.client.api.classes;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
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
        ICommand c = ClientCommandHandler.instance.getCommandMap().get(command);
        if (c == null) {
            return null;
        }
        deleteCommand(c);
        return new CommandNodeHelper(c, true);
    }

    @Override
    public void reRegisterCommand(CommandNodeHelper node) {
        ICommand c = node.getRaw();
        if (c == null) {
            return;
        }
        ClientCommandHandler.instance.registerCommand(c);
    }

    private static Field commands;

    private static void deleteCommand(ICommand command) {
        if (commands == null) {
            boolean found = false;
            for (Field declaredField : CommandHandler.class.getDeclaredFields()) {
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
            Set<ICommand> commands = (Set<ICommand>) CommandHandler.class.getDeclaredFields()[0].get(null);
            commands.remove(command);
            Map<String, ICommand> map = ClientCommandHandler.instance.getCommandMap();
            map.remove(command.getCommandName());
            for (String alias : command.getAliases()) {
                map.remove(alias);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
