package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.AngleArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.PosArgument;
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
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @since 1.4.2
 */
 @Event("CommandContext")
 @SuppressWarnings("unused")
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
            arg = new TextHelper((Text) arg);
        } else if (arg instanceof Formatting) {
            arg = new FormattingHelper((Formatting) arg);
        } else if (arg instanceof AngleArgumentType.Angle) {
            arg = ((AngleArgumentType.Angle) arg).getAngle(fakeServerSource);
        } else if (arg instanceof ItemPredicateArgumentType.ItemStackPredicateArgument) {
            ItemPredicateArgumentType.ItemStackPredicateArgument itemPredicate = (ItemPredicateArgumentType.ItemStackPredicateArgument) arg;
            arg = (Predicate<ItemStackHelper>) item -> itemPredicate.test(item.getRaw());
        } else if (arg instanceof BlockPredicateArgumentType.BlockPredicate) {
            BlockPredicateArgumentType.BlockPredicate blockPredicate = (BlockPredicateArgumentType.BlockPredicate) arg;
            arg = (Predicate<BlockPosHelper>) block -> blockPredicate.test(new CachedBlockPosition(MinecraftClient.getInstance().world, block.getRaw(), false));
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