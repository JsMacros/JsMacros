package xyz.wagyourtail.jsmacros.client.api.functions;

import xyz.wagyourtail.jsmacros.core.Core;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;

import java.io.File;
import java.util.function.Consumer;

/**
 * 
 * Functions for getting and modifying the player's state.
 * 
 * An instance of this class is passed to scripts as the {@code player} variable.
 * 
 * @author Wagyourtail
 */
 @Library("player")
public class FPlayer extends BaseLibrary {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    /**
     * @see xyz.wagyourtail.jsmacros.client.api.classes.Inventory
     * 
     * @return the Inventory handler
     */
    public Inventory openInventory() {
        if (mc.player.inventory == null) return null;
        return new Inventory();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ClientPlayerEntityHelper
     * 
     * @since 1.0.3
     * 
     * @return the player entity wrapper.
     */
    public ClientPlayerEntityHelper getPlayer() {
        return new ClientPlayerEntityHelper(mc.player);
    }

    /**
     * @since 1.0.9
     * 
     * @return the player's current gamemode.
     */
    public String getGameMode() {
        return mc.interactionManager.getCurrentGameMode().toString();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.BlockDataHelper
     * 
     * @since 1.0.5
     * 
     * @param distance
     * @param fluid
     * @return the block/liquid the player is currently looking at.
     */
    public BlockDataHelper rayTraceBlock(double distance, boolean fluid) {
        BlockHitResult h = (BlockHitResult) mc.player.raycast(distance, 0, fluid);
        if (h.getType() == HitResult.Type.MISS) return null;
        BlockState b = mc.world.getBlockState(h.getBlockPos());
        BlockEntity t = mc.world.getBlockEntity(h.getBlockPos());
        if (b.getBlock().equals(Blocks.VOID_AIR)) return null;
        return new BlockDataHelper(b, t, h.getBlockPos());
    }

    /**
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper
     * 
     * @since 1.0.5
     * 
     * @return the entity the player is currently looking at.
     */
    public EntityHelper rayTraceEntity() {
        if (mc.targetedEntity != null) return new EntityHelper(mc.targetedEntity);
        else return null;
    }

    /**
     * Write to a sign screen if a sign screen is currently open.
     * 
     * @since 1.2.2
     * 
     * @param l1
     * @param l2
     * @param l3
     * @param l4
     * @return {@link java.lang.Boolean boolean} of success.
     */
    public boolean writeSign(String l1, String l2, String l3, String l4) {
        if (mc.currentScreen instanceof SignEditScreen) {
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(0, l1);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(1, l2);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(2, l3);
            ((ISignEditScreen) mc.currentScreen).jsmacros_setLine(3, l4);
            return true;
        }
        return false;
    }

    /**
     * @see FPlayer#takeScreenshot(String, String, MethodWrapper)
     * 
     * @since 1.2.6
     * 
     * @param folder
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, MethodWrapper<TextHelper, Object, Object> callback) {
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
    
    /**
     * Take a screenshot and save to a file.
     * 
     * @since 1.2.6
     * 
     * @param folder
     * @param file
     * @param callback calls your method as a {@link Consumer}&lt;{@link TextHelper}&gt;
     */
    public void takeScreenshot(String folder, String file, MethodWrapper<TextHelper, Object, Object> callback) {
        ScreenshotUtils.saveScreenshot(new File(Core.instance.config.macroFolder, folder), file, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
}
