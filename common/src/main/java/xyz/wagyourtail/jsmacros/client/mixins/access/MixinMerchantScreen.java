package xyz.wagyourtail.jsmacros.client.mixins.access;

import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.VillagerTradingScreen;
import net.minecraft.entity.data.Trader;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TraderOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;

@Mixin(VillagerTradingScreen.class)
public abstract class MixinMerchantScreen extends HandledScreen implements IMerchantScreen {
    
    @Shadow private int page;
    @Shadow private Trader trader;

    @Override
    public void jsmacros_selectIndex(int index) {
        TraderOfferList merchantrecipelist = this.trader.getOffers(this.client.player);
        if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
            if (index < 0 || index >= merchantrecipelist.size()) {
                return;
            }
            page = index;
            ((VillagerScreenHandler) this.screenHandler).setRecipeIndex(this.page);
            PacketByteBuf packetbuffer = new PacketByteBuf(Unpooled.buffer());
            packetbuffer.writeInt(this.page);
            this.client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket("MC|TrSel", packetbuffer));
        }
    }


    // ignore
    public MixinMerchantScreen(ScreenHandler p_i1072_1_) {
        super(p_i1072_1_);
    }
    
}
