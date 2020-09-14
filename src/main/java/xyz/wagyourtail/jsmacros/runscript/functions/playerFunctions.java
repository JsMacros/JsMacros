package xyz.wagyourtail.jsmacros.runscript.functions;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.compat.interfaces.ISignEditScreen;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.EntityHelper;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;
import xyz.wagyourtail.jsmacros.runscript.classes.Inventory;

public class playerFunctions extends Functions {

    public playerFunctions(String libName) {
        super(libName);
    }

    public playerFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }

    public Inventory openInventory() {
        if (mc.player.inventory == null) return null;
        return new Inventory();
    }

    public ClientPlayerEntityHelper getPlayer() {
        return new ClientPlayerEntityHelper(mc.player);
    }

    public String getGameMode() {
        return mc.interactionManager.getCurrentGameMode().toString();
    }

    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        BlockHitResult h = (BlockHitResult) mc.player.raycast(distance, 0, fluid);
        if (h.getType() == HitResult.Type.MISS) return null;
        BlockState b = mc.world.getBlockState(h.getBlockPos());
        BlockEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    public EntityHelper rayTraceEntity() {
        if (mc.targetedEntity != null) return new EntityHelper(mc.targetedEntity);
        else return null;
    }

    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof SignEditScreen) {
            ((ISignEditScreen) mc.currentScreen).setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).setLine(3, l4);
            return true;
        }
        return false;
    }

    public void takeScreenshot(String folder, Consumer<TextHelper> callback) {
        ScreenshotUtils.saveScreenshot(new File(jsMacros.config.macroFolder, folder), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
    
    public void takeScreenshot(String folder, String file, Consumer<TextHelper> callback) {
        ScreenshotUtils.saveScreenshot(new File(jsMacros.config.macroFolder, folder), file, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
}
