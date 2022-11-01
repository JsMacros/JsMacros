package xyz.wagyourtail.jsmacros.client.movement;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("EntityConstructor")
public class MovementDummy extends LivingEntity {

    private List<Vec3d> coordsHistory = new ArrayList<>();
    private List<PlayerInput> inputs = new ArrayList<>();

    private PlayerInput currentInput;
    private int jumpingCooldown;
    private ItemStack heldItem = null;
    private final List<ItemStack> armorStack = new ArrayList<>(4);
    private float walkSpeed;

    public MovementDummy(MovementDummy player) {
        this(player.world, player.getPos(), new Vec3d(player.velocityX, player.velocityY, player.velocityZ), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());
        this.inputs = new ArrayList<>(player.getInputs());
        this.coordsHistory = new ArrayList<>(player.getCoordsHistory());
        this.jumpingCooldown = player.jumpingCooldown;
        this.armorStack.addAll(player.armorStack);
    }

    public MovementDummy(ClientPlayerEntity player) {
        this(player.world, player.getPos(), new Vec3d(player.velocityX, player.velocityY, player.velocityZ), player.getBoundingBox(), player.onGround, player.isSprinting(), player.isSneaking());
        this.walkSpeed = player.abilities.getWalkSpeed();
        this.armorStack.addAll(StreamSupport.stream(player.getArmorItems().spliterator(), false).map(ItemStack::copy).collect(Collectors.toList()));
    }

    public MovementDummy(World world, Vec3d pos, Vec3d velocity, Box hitBox, boolean onGround, boolean isSprinting, boolean isSneaking) {
        super(world);
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
        this.velocityX = velocity.x;
        this.velocityY = velocity.y;
        this.velocityZ = velocity.z;
        this.setBoundingBox(hitBox);
        this.setSprinting(isSprinting);
        this.setSneaking(isSneaking);
        this.stepHeight = 0.6F;
        this.onGround = onGround;
        this.coordsHistory.add(this.getPos());
    }

    public List<Vec3d> getCoordsHistory() {
        return coordsHistory;
    }

    public List<PlayerInput> getInputs() {
        return inputs;
    }

    public Vec3d applyInput(PlayerInput input) {
        inputs.add(input); // We use this and not the clone, since the clone may be modified?
        PlayerInput currentInput = input.clone();
        this.yaw = currentInput.yaw;

        Vec3d velocity = new Vec3d(this.velocityX, this.velocityY, this.velocityZ);
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
        this.velocityX = velX;
        this.velocityY = velY;
        this.velocityZ = velZ;

        /** Sneaking start **/
        if (this.isSneaking()) {
            // Yeah this looks dumb, but that is the way minecraft does it
            currentInput.movementSideways = (float) ((double) currentInput.movementSideways * 0.3D);
            currentInput.movementForward = (float) ((double) currentInput.movementForward * 0.3D);
        }
        this.setSneaking(currentInput.sneaking);
        /** Sneaking end **/

        /** Sprinting start **/
        boolean hasHungerToSprint = true;
        if (!this.isSprinting() && !currentInput.sneaking && hasHungerToSprint && !this.hasStatusEffect(StatusEffects.BLINDNESS) && currentInput.sprinting) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (currentInput.movementForward <= 1.0E-5F || this.horizontalCollision)) {
            this.setSprinting(false);
        }
        /** Sprinting end **/

        /** Jumping start **/
        if (this.jumpingCooldown > 0) {
            --this.jumpingCooldown;
        }

        if (currentInput.jumping) {
            if (this.onGround && this.jumpingCooldown == 0) {
                this.jump();
                this.jumpingCooldown = 10;
            }
        } else {
            this.jumpingCooldown = 0;
        }
        /** Juming END **/

//        this.travel(new Vec3(currentInput.movementSideways * 0.98, 0.0, currentInput.movementForward * 0.98));

        /* flyingSpeed only gets set after travel */
//        this.flyingSpeed = this.isSprinting() ? 0.026F : 0.02F;

        return this.getPos();
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
    @Override
    public boolean method_13071(double x, double y, double z) {
        if (this.isClimbing() && y < 0.0D && this.isSneaking()) {
            this.velocityX = x;
            this.velocityY = 0.0D;
            this.velocityZ = z;
        }
        return super.method_13071(x, y, z);
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
    public HandOption method_13060() {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return armorStack;
    }

    @Override
    public ItemStack method_13043(EquipmentSlot equipmentSlot) {
        if (equipmentSlot.getType() == EquipmentSlot.Type.HAND) {
            return ItemStack.EMPTY;
        }
        return armorStack.get(equipmentSlot.method_13032());
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {

    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.setMovementSpeed(sprinting ? 0.13F : 0.1F);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public MovementDummy clone() {
        return new MovementDummy(this);
    }
}
