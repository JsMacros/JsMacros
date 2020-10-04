package xyz.wagyourtail.jsmacros.api.functions;

import java.io.File;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.api.Functions;
import xyz.wagyourtail.jsmacros.api.MethodWrappers;
import xyz.wagyourtail.jsmacros.api.classes.Inventory;
import xyz.wagyourtail.jsmacros.api.helpers.BlockDataHelper;
import xyz.wagyourtail.jsmacros.api.helpers.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.api.helpers.TextHelper;

/**
 * 
 * Functions for getting and modifying the player's state.
 * 
 * An instance of this class is passed to scripts as the {@code player} variable.
 * 
 * @author Wagyourtail
 *
 */
public class FPlayer extends Functions {

    public FPlayer(String libName) {
        super(libName);
    }

    public FPlayer(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }

    /**
     * @see xyz.wagyourtail.jsmacros.api.classes.Inventory
     * 
     * @return the Inventory handler
     */
    public Inventory openInventory() {
        if (mc.player.inventory == null) return null;
        return new Inventory();
    }

    /**
     * @see xyz.wagyourtail.jsmacros.api.helpers.ClientPlayerEntityHelper
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
     * @see xyz.wagyourtail.jsmacros.api.helpers.BlockDataHelper
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
     * @see xyz.wagyourtail.jsmacros.api.helpers.EntityHelper
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
     * @see FPlayer#takeScreenshot(String, String, Consumer)
     * 
     * @since 1.2.6
     * 
     * @param folder
     * @param callback
     */
    public void takeScreenshot(String folder, MethodWrappers.Consumer<TextHelper> callback) {
        ScreenshotUtils.saveScreenshot(new File(jsMacros.config.macroFolder, folder), mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
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
     * @param callback
     */
    public void takeScreenshot(String folder, String file, MethodWrappers.Consumer<TextHelper> callback) {
        ScreenshotUtils.saveScreenshot(new File(jsMacros.config.macroFolder, folder), file, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight(),
            mc.getFramebuffer(), (text) -> {
                if (callback != null) callback.accept(new TextHelper(text));
            });
    }
}
