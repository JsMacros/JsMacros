package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * @author Etheradon
 * @since 1.8.4
 */
public class FakeServerCommandSource extends ServerCommandSource {

    private final ClientCommandSource source;

    public FakeServerCommandSource(ClientCommandSource source, ClientPlayerEntity player) {
        super(null, player.getPos(), player.getRotationClient(), null, 100, player.getName().getString(), player.getDisplayName(), null, player);
        this.source = source;
    }

    @Override
    public Collection<String> getEntitySuggestions() {
        return source.getEntitySuggestions();
    }

    @Override
    public Collection<String> getChatSuggestions() {
        return source.getChatSuggestions();
    }

    @Override
    public Collection<String> getPlayerNames() {
        return source.getPlayerNames();
    }

    @Override
    public Collection<String> getTeamNames() {
        return source.getTeamNames();
    }

    @Override
    public Stream<Identifier> getSoundIds() {
        return source.getSoundIds();
    }

    @Override
    public Stream<Identifier> getRecipeIds() {
        return source.getRecipeIds();
    }

    @Override
    public CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
        return source.getCompletions(context);
    }

    @Override
    public Collection<RelativePosition> getBlockPositionSuggestions() {
        return source.getBlockPositionSuggestions();
    }

    @Override
    public Collection<RelativePosition> getPositionSuggestions() {
        return source.getPositionSuggestions();
    }

    @Override
    public Set<RegistryKey<World>> getWorldKeys() {
        return source.getWorldKeys();
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return source.getRegistryManager();
    }

    @Override
    public void sendFeedback(Text message, boolean broadcastToOps) {
        MinecraftClient.getInstance().player.sendMessage(message);
    }

}