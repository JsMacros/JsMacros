package xyz.wagyourtail.jsmacros.client.api.helpers.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.BlockPosHelper;
import xyz.wagyourtail.jsmacros.client.api.helpers.world.DirectionHelper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * Helper for ClientPlayNetworkHandler
 * it accesses interaction manager from {@code mc} instead of {@code base}, to avoid issues
 * @author Quinntyx
 * @since 1.9.2
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NetworkHandlerHelper extends BaseHelper<ClientPlayNetworkHandler> {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * indicates if the helper should auto update the base manager, default is true<br>
     * when the base doesn't equal to the current manager,<br>
     *  if this is false, raise an error;<br>
     *  else if base is updated, the method works as usual;<br>
     *  else if the method don't need manager or network interaction, work as usual with old manager;<br>
     *  else the method does nothing
     */
    public boolean autoUpdateBase = true;

    public NetworkHandlerHelper(ClientPlayNetworkHandler base) {
        super(base);
    }

    /**
     * checks if the base matches the current manager
     * @param update true if the base should be updated. otherwise it'll raise an error if it's not up-to-date
     * @return true if base is available
     */
    public boolean checkBase(boolean update) {
        if (mc.getNetworkHandler() == base) return true;
        if (update) {
            if (mc.getNetworkHandler() != null) {
                base = mc.getNetworkHandler();
                return true;
            } else return false;
        } else {
            throw new RuntimeException("Wrapped network handler doesn't match the current one in client");
        }
    }

    /**
     * Sends a PlayerMoveC2SPacket.Full.
     * @param yaw
     * @param pitch
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerMove(Pos3D pos, double yaw, double pitch, boolean isOnGround) {
        checkBase(autoUpdateBase);
        base.sendPacket(new PlayerMoveC2SPacket.Full(pos.x, pos.y, pos.z, (float) yaw, (float) pitch, isOnGround));
        return this;
    }

    /**
     * Sends a PlayerMoveC2SPacket.LookAndOnGround.
     * @param yaw
     * @param pitch
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerLookAndOnGround(double yaw, double pitch, boolean isOnGround) {
        checkBase(autoUpdateBase);
        base.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround((float) yaw, (float) pitch, isOnGround));
        return this;
    }

    /**
     * Sends a PlayerActionC2SPacket. Available actions:
     * "START_DESTROY_BLOCK"
     * "ABORT_DESTROY_BLOCK"
     * "STOP_DESTROY_BLOCK"
     * "DROP_ALL_ITEMS"
     * "DROP_ITEM"
     * "RELEASE_USE_ITEM"
     * "SWAP_ITEM_WITH_OFFHAND"
     * @param action
     * @param pos
     * @param direction
     * @param sequence
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerAction(String action, BlockPosHelper pos, DirectionHelper direction, int sequence) {
        checkBase(autoUpdateBase);
        PlayerActionC2SPacket.Action packetAction = switch (action) {
            case "START_DESTROY_BLOCK" -> PlayerActionC2SPacket.Action.START_DESTROY_BLOCK;
            case "ABORT_DESTROY_BLOCK" -> PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK;
            case "STOP_DESTROY_BLOCK" -> PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK;
            case "DROP_ALL_ITEMS" -> PlayerActionC2SPacket.Action.DROP_ALL_ITEMS;
            case "RELEASE_USE_ITEM" -> PlayerActionC2SPacket.Action.RELEASE_USE_ITEM;
            case "SWAP_ITEM_WITH_OFFHAND" -> PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND;
            default -> throw new IllegalStateException("Invalid packet action: " + action);
        };

        base.sendPacket(new PlayerActionC2SPacket(packetAction, pos.getRaw(), direction.getRaw(), sequence));

        return this;
    }

    /**
     * Sends a PlayerActionC2SPacket. Available actions:
     * "START_DESTROY_BLOCK"
     * "ABORT_DESTROY_BLOCK"
     * "STOP_DESTROY_BLOCK"
     * "DROP_ALL_ITEMS"
     * "DROP_ITEM"
     * "RELEASE_USE_ITEM"
     * "SWAP_ITEM_WITH_OFFHAND"
     * @param action
     * @param pos
     * @param direction
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerAction(String action, BlockPosHelper pos, DirectionHelper direction) {
        return sendPlayerAction(action, pos, direction, 0);
    }

    /**
     * Sends a PlayerInputC2SPacket.
     * @param sideways
     * @param forward
     * @param jumping
     * @param sneaking
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInput(double sideways, double forward, boolean jumping, boolean sneaking) {
        checkBase(autoUpdateBase);
        PlayerInputC2SPacket packet = new PlayerInputC2SPacket((float) sideways, (float) forward, jumping, sneaking);
        base.sendPacket(packet);

        return this;
    }

    /**
     * Sends a PlayerInteractBlockC2SPacket.
     * @param pos
     * @param direction
     * @param blockPos
     * @param offhand
     * @param insideBlock
     * @param sequence
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInteractBlock(Pos3D pos, DirectionHelper direction, BlockPosHelper blockPos, boolean offhand, boolean insideBlock, int sequence) {
        checkBase(autoUpdateBase);
        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        base.sendPacket(new PlayerInteractBlockC2SPacket(
            hand,
            new BlockHitResult(pos.toMojangDoubleVector(), direction.getRaw(), blockPos.getRaw(), insideBlock),
            sequence
        ));

        return this;
    }

    /**
     * Sends a PlayerInteractBlockC2SPacket.
     * @param pos
     * @param direction
     * @param blockPos
     * @param offhand
     * @param insideBlock
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInteractBlock(Pos3D pos, DirectionHelper direction, BlockPosHelper blockPos, boolean offhand, boolean insideBlock) {
        return sendPlayerInteractBlock(pos, direction, blockPos, offhand, insideBlock, 0);
    }

//    spare me please
//    i'll do it later
//    TODO(quinntyx): implement sendPlayerInteractEntity
//    /**
//     * Sends a PlayerInteractEntityC2SPacket.
//     * @return self
//     * @since 1.9.2
//     */
//    public NetworkHandlerHelper sendPlayerInteractEntity(Pos3D pos, double yaw, double pitch, boolean isOnGround) {
//        checkBase(autoUpdateBase);
//        base.sendPacket(new PlayerInteractEntityC2SPacket());
//
//        return this;
//    }

    /**
     * Sends a PlayerInteractItemC2SPacket.
     * @param offhand
     * @param sequence
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInteractItem(boolean offhand, int sequence) {
        checkBase(autoUpdateBase);
        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        base.sendPacket(new PlayerInteractItemC2SPacket(hand, sequence));

        return this;
    }

    /**
     * Sends a PlayerInteractItemC2SPacket.
     * @param offhand
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInteractItem(boolean offhand) {
        return sendPlayerInteractItem(offhand, 0);
    }

    /**
     * Sends a PlayerInteractItemC2SPacket.
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendPlayerInteractItem() {
        return sendPlayerInteractItem(false);
    }

    /**
     * Sends a HandSwingC2SPacket.
     * @param offhand
     * @return self
     * @since 1.9.2
     */
    public NetworkHandlerHelper sendHandSwing(boolean offhand) {
        checkBase(autoUpdateBase);
        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        base.sendPacket(new HandSwingC2SPacket(hand));

        return this;
    }
}