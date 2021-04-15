package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.input.Input;

public class PlayerInput {
    public float movementSideways;
    public float movementForward;
    public float pitch;
    public float yaw;
    public boolean jumping;
    public boolean sneaking;
    public boolean sprinting;

    public PlayerInput() {
        this(0.0F, 0.0F, 0.0F, 0.0F, false, false, false);
    }

    public PlayerInput(float movementForward, float movementSideways, float yaw) {
        this(movementForward, movementSideways, yaw, 0.0F, false, false, false);
    }

    public PlayerInput(float movementForward, float yaw, boolean jumping, boolean sprinting) {
        this(movementForward, 0.0F, yaw, 0.0F, jumping, false, sprinting);
    }

    public PlayerInput(Input input, float yaw, float pitch, boolean sprinting) {
        this(input.movementForward, input.movementSideways, yaw, pitch, input.jumping, input.sneaking, sprinting);
    }

    public PlayerInput(float movementForward, float movementSideways, float yaw, float pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        this.movementForward = movementForward;
        this.movementSideways = movementSideways;
        this.pitch = pitch;
        this.yaw = yaw;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sprinting = sprinting;
    }
}
