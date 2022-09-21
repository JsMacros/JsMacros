package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.tree.CommandNode;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

public class CommandNodeHelper extends BaseHelper<CommandNode<?>> {
    public final CommandNode fabric;

    public CommandNodeHelper(CommandNode base, CommandNode fabric) {
        super(base);
        this.fabric = fabric;
    }
}
