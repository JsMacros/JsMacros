package net.fabricmc.fabric.mixin.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.impl.command.client.HelpCommandAccessor;
import net.minecraft.server.command.HelpCommand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HelpCommand.class)
public class HelpCommandMixin implements HelpCommandAccessor {


    @Shadow(aliases = "f_137785_") @Final private static SimpleCommandExceptionType FAILED_EXCEPTION;

    @Override
    public SimpleCommandExceptionType getFailedException() {
        return FAILED_EXCEPTION;
    }

}