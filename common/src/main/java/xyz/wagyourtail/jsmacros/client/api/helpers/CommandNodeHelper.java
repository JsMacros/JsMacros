package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.ICommand;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

public class CommandNodeHelper extends BaseHelper<ICommand> {
    public boolean client;

    public CommandNodeHelper(ICommand base, boolean client) {
        super(base);
        this.client = client;
    }
}
