package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.BlockStateArgument;
import net.minecraft.command.arguments.ItemStackArgument;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @since 1.4.2
 */
 @Event("CommandContext")
public class CommandContextHelper extends BaseHelper<CommandContext<?>> implements BaseEvent {
    public CommandContextHelper(CommandContext<?> base) {
        super(base);
    }

    /**
     * @param name
     *
     * @return
     * @since 1.4.2
     * @throws CommandSyntaxException
     */
    public Object getArg(String name) throws CommandSyntaxException {
        Object arg = base.getArgument(name, Object.class);
        if (arg instanceof BlockStateArgument) {
            arg = new BlockStateHelper(((BlockStateArgument) arg).getBlockState());
        } else if (arg instanceof Identifier) {
            arg = arg.toString();
        } else if (arg instanceof ItemStackArgument) {
            arg = new ItemStackHelper(((ItemStackArgument) arg).createStack(1, false));
        } else if (arg instanceof Tag) {
            arg = NBTElementHelper.resolve((Tag) arg);
        } else if (arg instanceof Text) {
            arg = new TextHelper((Text) arg);
        }
        return arg;
    }

    public CommandContextHelper getChild() {
        return new CommandContextHelper(base.getChild());
    }

    public StringRange getRange() {
        return base.getRange();
    }

    public String getInput() {
        return base.getInput();
    }
}
