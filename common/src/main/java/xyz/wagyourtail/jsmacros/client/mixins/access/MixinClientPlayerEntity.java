package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow protected abstract void sendChatMessageInternal(String message, @Nullable Text preview);

    @Shadow protected abstract void sendCommandInternal(String command, @Nullable Text preview);

    @Shadow @Final protected MinecraftClient client;

    @Shadow protected abstract int getPermissionLevel();

    @Override
    public void jsmacros_sendChatMessageBypass(String message) {
        if (message.startsWith("/")) {
            sendCommandInternal(message.substring(1), null);
        } else {
            sendChatMessageInternal(message, null);
        }
    }

    @Override
    public int jsmacros_getPermissionLevel() {
        return getPermissionLevel();
    }
}
