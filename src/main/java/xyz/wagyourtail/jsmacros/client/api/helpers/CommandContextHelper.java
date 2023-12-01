package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.api.classes.FakeServerCommandSource;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.EnchantmentHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.inventory.ItemStackHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockStateHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.entity.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @since 1.4.2
 */
@Event("CommandContext")
@SuppressWarnings("unused")
public class CommandContextHelper extends BaseEvent {
    protected CommandContext<?> base;

    public CommandContextHelper(CommandContext<?> base) {
        this.base = base;
    }

    public CommandContext<?> getRaw() {
        return base;
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CommandContextHelper) {
            return base.equals(((CommandContextHelper) obj).base);
        }
        return base.equals(obj);
    }

    /**
     * @param name
     * @return
     * @throws CommandSyntaxException
     * @since 1.4.2
     */
    public Object getArg(String name) throws CommandSyntaxException {
        Object arg = base.getArgument(name, Object.class);
        ServerCommandSource fakeServerSource = null;
        if (base.getSource() instanceof ClientCommandSource) {
            fakeServerSource = new FakeServerCommandSource((ClientCommandSource) base.getSource(), MinecraftClient.getInstance().player);
        }
        if (arg instanceof BlockStateArgument) {
            arg = new BlockStateHelper(((BlockStateArgument) arg).getBlockState());
        } else if (arg instanceof Identifier) {
            arg = ((Identifier) arg).toString();
        } else if (arg instanceof ItemStackArgument) {
            arg = new ItemStackHelper(((ItemStackArgument) arg).createStack(1, false));
        } else if (arg instanceof NbtElement) {
            arg = NBTElementHelper.resolve((NbtElement) arg);
        } else if (arg instanceof Text) {
            arg = TextHelper.wrap((Text) arg);
        } else if (arg instanceof Formatting) {
            arg = new FormattingHelper((Formatting) arg);
        } else if (arg instanceof AngleArgumentType.Angle) {
            arg = ((AngleArgumentType.Angle) arg).getAngle(fakeServerSource);
        } else if (arg instanceof ItemPredicateArgumentType.ItemPredicateArgument) {
            ItemPredicateArgumentType.ItemPredicateArgument itemPredicate = (ItemPredicateArgumentType.ItemPredicateArgument) arg;
            arg = itemPredicate.create(null);
        } else if (arg instanceof BlockPredicateArgumentType.BlockPredicate) {
            BlockPredicateArgumentType.BlockPredicate blockPredicate = (BlockPredicateArgumentType.BlockPredicate) arg;
            arg = blockPredicate.create(Registry.BLOCK);
        } else if (arg instanceof PosArgument) {
            arg = new BlockPosHelper(((PosArgument) arg).toAbsoluteBlockPos(fakeServerSource));
        } else if (arg instanceof Enchantment) {
            arg = new EnchantmentHelper((Enchantment) arg);
        } else if (arg instanceof EntitySelector) {
            arg = ((EntitySelector) arg).getEntities(fakeServerSource).stream().map(EntityHelper::create).collect(Collectors.toList());
        } else if (arg instanceof ParticleEffect) {
            arg = Registry.PARTICLE_TYPE.getId(((ParticleEffect) arg).getType()).toString();
        } else if (arg instanceof StatusEffect) {
            arg = Registry.STATUS_EFFECT.getId(((StatusEffect) arg)).toString();
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
