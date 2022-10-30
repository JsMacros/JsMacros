package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.command.Command;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

public class CommandNodeHelper extends BaseHelper<Command> {
    public boolean client;

    public CommandNodeHelper(Command base, boolean client) {
        super(base);
        this.client = client;
    }
}
