package xyz.wagyourtail.jsmacros.client.movement;

import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;

import java.util.ArrayList;
import java.util.List;

public class MovementDummy extends LivingEntity {

    public List<Vec3d> coordsHistory = new ArrayList<>();
    private PlayerInput currentInput;
    private int jumpingCooldown;

    public MovementDummy(ClientPlayerEntity player) {
        this(player.getEntityWorld(), player.getPos(), player.getVelocity(), player.getBoundingBox(), player.isOnGround(), player.isSprinting(), player.isSneaking());
    }

    public MovementDummy(World world, Vec3d pos, Vec3d velocity, Box hitBox, boolean onGround, boolean isSprinting, boolean isSneaking) {
        super(EntityType.PLAYER, world);
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setVelocity(velocity);
        this.setBoundingBox(hitBox);
        this.setSprinting(isSprinting);
        this.setSneaking(isSneaking);
        this.stepHeight = 0.6F;
        this.onGround = onGround;
        this.coordsHistory.add(this.getPos());

    }

    public Vec3d applyInput(PlayerInput input) {
        this.currentInput = input.clone();
        this.yaw = currentInput.yaw;

        Vec3d velocity = this.getVelocity();
        double velX = velocity.x;
        double velY = velocity.y;
        double velZ = velocity.z;
        if (Math.abs(velocity.x) < 0.003D) {
            velX = 0.0D;
        }

        if (Math.abs(velocity.y) < 0.003D) {
            velY = 0.0D;
        }

        if (Math.abs(velocity.z) < 0.003D) {
            velZ = 0.0D;
        }
        this.setVelocity(velX, velY, velZ);

        /** Sneaking start **/
        if (this.isSneaking() && this.wouldPoseNotCollide(EntityPose.CROUCHING)) {
            // Yeah this looks dumb, but that is the way minecraft does it
            this.currentInput.movementSideways = (float) ((double) this.currentInput.movementSideways * 0.3D);
            this.currentInput.movementForward = (float) ((double) this.currentInput.movementForward * 0.3D);
        }
        this.setSneaking(this.currentInput.sneaking);
        /** Sneaking end **/

        /** Sprinting start **/
        boolean hasHungerToSprint = true;
        if (!this.isSprinting() && !this.currentInput.sneaking && hasHungerToSprint && !this.hasStatusEffect(StatusEffects.BLINDNESS) && this.currentInput.sprinting) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (this.currentInput.movementForward <= 1.0E-5F || this.horizontalCollision)) {
            this.setSprinting(false);
        }
        /** Sprinting end **/

        /** Jumping start **/
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (this.currentInput.jumping) {
            if (this.onGround && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        /** Juming END **/

        this.travel(new Vec3d(this.currentInput.movementSideways * 0.98, 0.0, this.currentInput.movementForward * 0.98));

        /* flyingSpeed only gets set after travel */
        this.flyingSpeed = this.isSprinting() ? 0.026F : 0.02F;

        return this.getPos();
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
    @Override
    public Vec3d method_26318(Vec3d movementInput, float f) {
        if (this.isClimbing() && this.getVelocity().getY() < 0.0D && !this.getBlockState().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder()) {
            this.setVelocity(this.getVelocity().getX(), 0, this.getVelocity().getZ());
        }
        return super.method_26318(movementInput, f);
    }

    @Override
    protected boolean canClimb() {
        return !this.onGround || !this.isSneaking();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    @Override
    public ItemStack getMainHandStack() {
        return new ItemStack(Items.AIR);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.setMovementSpeed(sprinting ? 0.13F : 0.1F);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return null;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        return null;
    }

}
