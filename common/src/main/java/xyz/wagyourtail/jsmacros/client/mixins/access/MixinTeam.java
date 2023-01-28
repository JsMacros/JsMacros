package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Team.class)
public interface MixinTeam {

    @Accessor
    Scoreboard getScoreboard();
}
