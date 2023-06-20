package xyz.wagyourtail.jsmacros.client.access;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandNodeAccessor {
    static Field children;
    static Field literals;
    static Field arguments;

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
        if (node == null) {
            return null;
        }
        if (node instanceof LiteralCommandNode<S>) {
            Map<String, LiteralCommandNode<S>> l = (Map<String, LiteralCommandNode<S>>) literals.get(parent);
            l.remove(name);
        } else if (node instanceof ArgumentCommandNode<S, ?>) {
            Map<String, ArgumentCommandNode<S, ?>> a = (Map<String, ArgumentCommandNode<S, ?>>) arguments.get(parent);
            a.remove(name);
        }
        return node;
    }

}
