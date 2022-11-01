package xyz.wagyourtail.jsmacros.client.mixins.access;

import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.screen.ingame.BeaconScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;

@Mixin(BeaconScreen.class)
public abstract class MixinBeaconScreen extends HandledScreen implements IBeaconScreen {

    @Shadow private Inventory beaconInventory;

    @Override
    public StatusEffect jsmacros_getPrimaryEffect() {
        int id = this.beaconInventory.getProperty(1);
        return Arrays.stream(BeaconBlockEntity.field_5017).flatMap(Arrays::stream).filter(e -> e.method_2444() == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setPrimaryEffect(StatusEffect effect) {
        this.beaconInventory.setProperty(1, effect.method_2444());
    }

    @Override
    public StatusEffect jsmacros_getSecondaryEffect() {
        int id = this.beaconInventory.getProperty(2);
        return Arrays.stream(BeaconBlockEntity.field_5017).flatMap(Arrays::stream).filter(e -> e.method_2444() == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setSecondaryEffect(StatusEffect effect) {
        this.beaconInventory.setProperty(2, effect.method_2444());
    }

    @Override
    public int jsmacros_getLevel() {
        return this.beaconInventory.getProperty(0);
    }

    @Override
    public boolean jsmacros_sendBeaconPacket() {
        if (this.beaconInventory.getInvStack(0) != null && beaconInventory.getProperty(1) > 0) {
            String s = "MC|Beacon";
            PacketByteBuf packetbuffer = new PacketByteBuf(Unpooled.buffer());
            packetbuffer.writeInt(this.beaconInventory.getProperty(1));
            packetbuffer.writeInt(this.beaconInventory.getProperty(2));
            this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(s, packetbuffer));
            this.client.openScreen(null);
            return true;
        }
        return false;
    }

    //IGNORE
    public MixinBeaconScreen(ScreenHandler inventorySlotsIn) {
        super(inventorySlotsIn);
    }
}