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
        if (base.getSource() instanceof ClientCommandSource clientCommandSource) {
            fakeServerSource = new FakeServerCommandSource(clientCommandSource, MinecraftClient.getInstance().player);
        }
        if (arg instanceof BlockStateArgument blockStateArgument) {
            arg = new BlockStateHelper(blockStateArgument.getBlockState());
        } else if (arg instanceof Identifier identifier) {
            arg = identifier.toString();
        } else if (arg instanceof ItemStackArgument itemStackArgument) {
            arg = new ItemStackHelper(itemStackArgument.createStack(1, false));
        } else if (arg instanceof NbtElement nbtElement) {
            arg = NBTElementHelper.resolve(nbtElement);
        } else if (arg instanceof Text text) {
            arg = new TextHelper(text);
        } else if (arg instanceof Formatting formatting) {
            arg = new FormattingHelper(formatting);
        } else if (arg instanceof AngleArgumentType.Angle angle) {
            arg = angle.getAngle(fakeServerSource);
        } else if (arg instanceof ItemPredicateArgumentType.ItemStackPredicateArgument itemStackPredicateArgument) {
            Predicate<ItemStackHelper> predicate = item -> itemStackPredicateArgument.test(item.getRaw());
            arg = predicate;
        } else if (arg instanceof BlockPredicateArgumentType.BlockPredicate blockPredicate) {
            Predicate<BlockPosHelper> predicate = block -> blockPredicate.test(new CachedBlockPosition(MinecraftClient.getInstance().world, block.getRaw(), false));
            arg = predicate;
        } else if (arg instanceof PosArgument posArgument) {
            arg = new BlockPosHelper(posArgument.toAbsoluteBlockPos(fakeServerSource));
        } else if (arg instanceof Enchantment enchantment) {
            arg = new EnchantmentHelper(enchantment);
        } else if (arg instanceof EntitySelector entitySelector) {
            arg = entitySelector.getEntities(fakeServerSource).stream().map(EntityHelper::create).toList();
        } else if (arg instanceof ParticleEffect particleEffect) {
            arg = Registry.PARTICLE_TYPE.getId(particleEffect.getType()).toString();
        } else if (arg instanceof StatusEffect statusEffect) {
            arg = Registry.STATUS_EFFECT.getId(statusEffect).toString();
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
