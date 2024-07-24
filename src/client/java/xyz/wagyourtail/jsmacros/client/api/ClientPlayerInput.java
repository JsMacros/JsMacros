package xyz.wagyourtail.jsmacros.client.api;

import net.minecraft.client.input.Input;
import xyz.wagyourtail.jsmacros.api.PlayerInput;

public class ClientPlayerInput extends PlayerInput {

    public ClientPlayerInput() {
        super();
    }

    public ClientPlayerInput(float movementForward, float movementSideways, float yaw) {
        super(movementForward, movementSideways, yaw);
    }

    public ClientPlayerInput(float movementForward, float yaw, boolean jumping, boolean sprinting) {
        super(movementForward, yaw, jumping, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object from a minecraft input with the missing values extra
     *
     * @param input     Minecraft Input to be converted.
     * @param yaw       yaw of the player
     * @param pitch     pitch of the player
     * @param sprinting sprint input
     * @since 1.4.0
     */
    public ClientPlayerInput(Input input, float yaw, float pitch, boolean sprinting) {
        this(input.movementForward, input.movementSideways, yaw, pitch, input.jumping, input.sneaking, sprinting);
    }

    public ClientPlayerInput(double movementForward, double movementSideways, double yaw, double pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        super(movementForward, movementSideways, yaw, pitch, jumping, sneaking, sprinting);
    }

    public ClientPlayerInput(float movementForward, float movementSideways, float yaw, float pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        super(movementForward, movementSideways, yaw, pitch, jumping, sneaking, sprinting);
    }

    public ClientPlayerInput(PlayerInput input) {
        super(input);
    }

}
