package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ICommandNode;

import java.util.Map;

@Mixin(CommandNode.class)
public class MixinCommandNode<S> implements ICommandNode<S> {

    @Shadow @Final private Map<String, CommandNode<S>> children;

    @Shadow @Final private Map<String, ArgumentCommandNode<S, ?>> arguments;

    @Shadow @Final private Map<String, LiteralCommandNode<S>> literals;

    @Override
    public CommandNode<S> remove(String name) {
        CommandNode<S> child = children.remove(name);
        if (child instanceof ArgumentCommandNode<S,?>){
            arguments.remove(name);
        } else if (child instanceof LiteralCommandNode<S>) {
            literals.remove(name);
        }
        return child;
    }
}
