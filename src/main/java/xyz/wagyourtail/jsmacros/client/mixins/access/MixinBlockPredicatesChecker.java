package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.predicate.BlockPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BlockPredicatesChecker.class)
public interface MixinBlockPredicatesChecker {

    @Accessor
    List<BlockPredicate> getPredicates();

}
