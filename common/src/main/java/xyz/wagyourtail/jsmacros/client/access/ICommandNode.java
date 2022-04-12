package xyz.wagyourtail.jsmacros.client.access;

import com.mojang.brigadier.tree.CommandNode;

public interface ICommandNode<S> {
    CommandNode<S> remove(String name);
}
