package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
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
        if (arg instanceof Block) {
            arg = Block.REGISTRY.getIdentifier((Block) arg).toString();
        } else if (arg instanceof Identifier) {
            arg = arg.toString();
        } else if (arg instanceof Item) {
            arg = new ItemStackHelper(new ItemStack((Item) arg, 1));
        } else if (arg instanceof NbtElement) {
            arg = NBTElementHelper.resolve((NbtElement) arg);
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
