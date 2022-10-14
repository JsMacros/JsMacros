package xyz.wagyourtail.jsmacros.client.access;


import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CommandNodeAccessor {
    static Field children;
    static Field literals;
    static Field arguments;

    static Field commands;

    static {
        Field uf = null;
        try {
            children = CommandNode.class.getDeclaredField("children");
            children.setAccessible(true);
            literals = CommandNode.class.getDeclaredField("literals");
            literals.setAccessible(true);
            arguments = CommandNode.class.getDeclaredField("arguments");
            arguments.setAccessible(true);
        } catch (SecurityException ex) {
            throw new RuntimeException("I knew I should've just used unsafe...", ex);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static <S> CommandNode<S> remove(CommandNode<S> parent, String name) throws IllegalAccessException {
        Map<String, CommandNode> c = (Map<String, CommandNode>) children.get(parent);
        CommandNode<S> node = c.remove(name);
        if (node == null) return null;
        if (node instanceof LiteralCommandNode) {
            Map<String, LiteralCommandNode<S>> l = (Map<String, LiteralCommandNode<S>>) literals.get(parent);
            l.remove(name);
        } else if (node instanceof ArgumentCommandNode) {
            Map<String, ArgumentCommandNode<S, ?>> a = (Map<String, ArgumentCommandNode<S, ?>>) arguments.get(parent);
            a.remove(name);
        }
        return node;
    }

    private static Field getCommandsField() {
        if (commands == null) {
            commands = Arrays.stream(CommandHandler.class.getDeclaredFields())
                .filter(e -> e.getType().equals(Set.class))
                .findFirst()
                .orElseThrow(NullPointerException::new);
        }
        return commands;
    }

    public static ICommand removeCommand(CommandHandler h, String commandName) throws IllegalAccessException {
        Map<String, ICommand> commandMap = h.getCommandMap();
        Set<ICommand> commands = (Set<ICommand>) getCommandsField().get(h);
        ICommand p_registerCommand_1_ = commandMap.get(commandName);

        if (p_registerCommand_1_ == null) {
            return null;
        }

        commandMap.remove(p_registerCommand_1_.getCommandName());
        commands.remove(p_registerCommand_1_);
        Iterator i$ = p_registerCommand_1_.getAliases().iterator();

        while(true) {
            String s;
            ICommand icommand;
            do {
                if (!i$.hasNext()) {
                    return p_registerCommand_1_;
                }

                s = (String)i$.next();
                icommand = (ICommand)commandMap.get(s);
            } while(icommand != null && icommand.getCommandName().equals(s));

            commandMap.remove(s);
        }
    }
}
