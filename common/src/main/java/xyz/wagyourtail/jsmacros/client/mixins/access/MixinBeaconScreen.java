package xyz.wagyourtail.jsmacros.client.mixins.access;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IBeaconScreen;

import java.util.Arrays;

@Mixin(GuiBeacon.class)
public abstract class MixinBeaconScreen extends GuiContainer implements IBeaconScreen {

    @Shadow private IInventory beaconInventory;

    @Override
    public Potion jsmacros_getPrimaryEffect() {
        int id = this.beaconInventory.getProperty(1);
        return Arrays.stream(TileEntityBeacon.field_146009_a).flatMap(Arrays::stream).filter(e -> e.id == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setPrimaryEffect(Potion effect) {
        this.beaconInventory.setProperty(1, effect.id);
    }

    @Override
    public Potion jsmacros_getSecondaryEffect() {
        int id = this.beaconInventory.getProperty(2);
        return Arrays.stream(TileEntityBeacon.field_146009_a).flatMap(Arrays::stream).filter(e -> e.id == id).findFirst().orElse(null);
    }

    @Override
    public void jsmacros_setSecondaryEffect(Potion effect) {
        this.beaconInventory.setProperty(2, effect.id);
    }

    @Override
    public int jsmacros_getLevel() {
        return this.beaconInventory.getProperty(0);
    }

    @Override
    public boolean jsmacros_sendBeaconPacket() {
        if (this.beaconInventory.getInvStack(0) != null && beaconInventory.getProperty(1) > 0) {
            String s = "MC|Beacon";
            PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeInt(this.beaconInventory.getProperty(1));
            packetbuffer.writeInt(this.beaconInventory.getProperty(2));
            this.client.getNetworkHandler().sendPacket(new C17PacketCustomPayload(s, packetbuffer));
            this.client.openScreen(null);
            return true;
        }
        return false;
    }

    //IGNORE
    public MixinBeaconScreen(Container inventorySlotsIn) {
        super(inventorySlotsIn);
    }
}