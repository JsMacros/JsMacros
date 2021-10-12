package xyz.wagyourtail.jsmacros.client.movement;

import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("EntityConstructor")
public class MovementDummy extends LivingEntity {

    private List<Vec3d> coordsHistory = new ArrayList<>();
    private List<PlayerInput> inputs = new ArrayList<>();

    // Is used for checking the depthstrider enchant
    private Map<EquipmentSlot, ItemStack> equippedStack = new HashMap<>(6);
    private int jumpingCooldown;

    public MovementDummy(MovementDummy player) {
        this(player.getEntityWorld(), player.getPos(), player.getVelocity(), player.getBoundingBox(), player.isOnGround(), player.isSprinting(), player.isSneaking());
        this.inputs = new ArrayList<>(player.getInputs());
        this.coordsHistory = new ArrayList<>(player.getCoordsHistory());
        this.jumpingCooldown = player.jumpingCooldown;
        this.equippedStack = player.equippedStack;
    }

    public MovementDummy(ClientPlayerEntity player) {
        this(player.getEntityWorld(), player.getPos(), player.getVelocity(), player.getBoundingBox(), player.isOnGround(), player.isSprinting(), player.isSneaking());
        for (EquipmentSlot value : EquipmentSlot.values()) {
            equippedStack.put(value, player.getEquippedStack(value).copy());
        }
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

        for (EquipmentSlot value : EquipmentSlot.values()) {
            equippedStack.put(value, new ItemStack(null));
        }
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
        this.setYaw(currentInput.yaw);

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
        /** Jumping END **/

        this.travel(new Vec3d(currentInput.movementSideways * 0.98, 0.0, currentInput.movementForward * 0.98));

        /* flyingSpeed only gets set after travel */
        this.flyingSpeed = this.isSprinting() ? 0.026F : 0.02F;

        return this.getPos();
    }

    @Override
    public void travel(Vec3d movementInput) {
        double d = 0.08D;
        boolean bl = this.getVelocity().y <= 0.0D;
        if (bl && this.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
            d = 0.01D;
            this.fallDistance = 0.0F;
        }

        FluidState fluidState = this.world.getFluidState(this.getBlockPos());
        float j;
        double e;
        if (this.isTouchingWater() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
            e = this.getY();
            j = this.isSprinting() ? 0.9F : this.getBaseMovementSpeedMultiplier();
            float g = 0.02F;
            float h = (float) EnchantmentHelper.getDepthStrider(this);
            if (h > 3.0F) {
                h = 3.0F;
            }

            if (!this.onGround) {
                h *= 0.5F;
            }

            if (h > 0.0F) {
                j += (0.54600006F - j) * h / 3.0F;
                g += (this.getMovementSpeed() - g) * h / 3.0F;
            }

            if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
                j = 0.96F;
            }

            this.updateVelocity(g, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            Vec3d vec3d = this.getVelocity();
            if (this.horizontalCollision && this.isClimbing()) {
                vec3d = new Vec3d(vec3d.x, 0.2D, vec3d.z);
            }

            this.setVelocity(vec3d.multiply(j, 0.800000011920929D, j));
            Vec3d vec3d2 = this.method_26317(d, bl, this.getVelocity());
            this.setVelocity(vec3d2);
            if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.getY() + e, vec3d2.z)) {
                this.setVelocity(vec3d2.x, 0.30000001192092896D, vec3d2.z);
            }
        } else if (this.isInLava() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState.getFluid())) {
            e = this.getY();
            this.updateVelocity(0.02F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            Vec3d vec3d4;
            if (this.getFluidHeight(FluidTags.LAVA) <= this.getSwimHeight()) {
                this.setVelocity(this.getVelocity().multiply(0.5D, 0.800000011920929D, 0.5D));
                vec3d4 = this.method_26317(d, bl, this.getVelocity());
                this.setVelocity(vec3d4);
            } else {
                this.setVelocity(this.getVelocity().multiply(0.5D));
            }

            if (!this.hasNoGravity()) {
                this.setVelocity(this.getVelocity().add(0.0D, -d / 4.0D, 0.0D));
            }

            vec3d4 = this.getVelocity();
            if (this.horizontalCollision && this.doesNotCollide(vec3d4.x, vec3d4.y + 0.6000000238418579D - this.getY() + e, vec3d4.z)) {
                this.setVelocity(vec3d4.x, 0.30000001192092896D, vec3d4.z);
            }
        } else if (this.isFallFlying()) {
            Vec3d vec3d5 = this.getVelocity();
            if (vec3d5.y > -0.5D) {
                this.fallDistance = 1.0F;
            }

            Vec3d vec3d6 = this.getRotationVector();
            j = this.getPitch() * 0.017453292F;
            double k = Math.sqrt(vec3d6.x * vec3d6.x + vec3d6.z * vec3d6.z);
            double l = vec3d5.horizontalLength();
            double m = vec3d6.length();
            float n = MathHelper.cos(j);
            n = (float)((double)n * (double)n * Math.min(1.0D, m / 0.4D));
            vec3d5 = this.getVelocity().add(0.0D, d * (-1.0D + (double)n * 0.75D), 0.0D);
            double q;
            if (vec3d5.y < 0.0D && k > 0.0D) {
                q = vec3d5.y * -0.1D * (double)n;
                vec3d5 = vec3d5.add(vec3d6.x * q / k, q, vec3d6.z * q / k);
            }

            if (j < 0.0F && k > 0.0D) {
                q = l * (double)(-MathHelper.sin(j)) * 0.04D;
                vec3d5 = vec3d5.add(-vec3d6.x * q / k, q * 3.2D, -vec3d6.z * q / k);
            }

            if (k > 0.0D) {
                vec3d5 = vec3d5.add((vec3d6.x / k * l - vec3d5.x) * 0.1D, 0.0D, (vec3d6.z / k * l - vec3d5.z) * 0.1D);
            }

            this.setVelocity(vec3d5.multiply(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
            this.move(MovementType.SELF, this.getVelocity());
        } else {
            BlockPos blockPos = this.getVelocityAffectingPos();
            float t = this.world.getBlockState(blockPos).getBlock().getSlipperiness();
            j = this.onGround ? t * 0.91F : 0.91F;
            Vec3d vec3d7 = this.method_26318(movementInput, t);
            double v = vec3d7.y;
            if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
                v += (0.05D * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1) - vec3d7.y) * 0.2D;
                this.fallDistance = 0.0F;
            } else if (!this.hasNoGravity()) {
                v -= d;
            }

            this.setVelocity(vec3d7.x * (double)j, v * 0.9800000190734863D, vec3d7.z * (double)j);
        }
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
        } else {
            if (movementType == MovementType.PISTON) {
                movement = this.adjustMovementForPiston(movement);
                if (movement.equals(Vec3d.ZERO)) {
                    return;
                }
            }

            if (this.movementMultiplier.lengthSquared() > 1.0E-7D) {
                movement = movement.multiply(this.movementMultiplier);
                this.movementMultiplier = Vec3d.ZERO;
                this.setVelocity(Vec3d.ZERO);
            }

            movement = this.adjustMovementForSneaking(movement, movementType);
            Vec3d vec3d = this.adjustMovementForCollisions(movement);
            if (vec3d.lengthSquared() > 1.0E-7D) {
                this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
            }

            this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, vec3d.x) || !MathHelper.approximatelyEquals(movement.z, vec3d.z);
            this.verticalCollision = movement.y != vec3d.y;
            this.onGround = this.verticalCollision && movement.y < 0.0D;
            BlockPos blockPos = this.getLandingPos();
            BlockState blockState = this.world.getBlockState(blockPos);
            this.fall(vec3d.y, this.onGround, blockState, blockPos);
            if (!this.isRemoved()) {
                Vec3d vec3d2 = this.getVelocity();
                if (movement.x != vec3d.x) {
                    this.setVelocity(0.0D, vec3d2.y, vec3d2.z);
                }

                if (movement.z != vec3d.z) {
                    this.setVelocity(vec3d2.x, vec3d2.y, 0.0D);
                }

                Block block = blockState.getBlock();
                if (movement.y != vec3d.y) {
                    block.onEntityLand(this.world, this);
                }

                if (this.onGround) {
                    block.onSteppedOn(this.world, blockPos, blockState, this);
                }

                MoveEffect moveEffect = this.getMoveEffect();
                if (moveEffect.hasAny() && !this.hasVehicle()) {
                    double d = vec3d.x;
                    double e = vec3d.y;
                    double f = vec3d.z;
                    this.field_28627 = (float)((double)this.field_28627 + vec3d.length() * 0.6D);
                    if (!blockState.isIn(BlockTags.CLIMBABLE) && !blockState.isOf(Blocks.POWDER_SNOW)) {
                        e = 0.0D;
                    }

                    this.horizontalSpeed += (float)vec3d.horizontalLength() * 0.6F;
                    this.distanceTraveled += (float)Math.sqrt(d * d + e * e + f * f) * 0.6F;
                    if (!blockState.isAir()) {
                        if (this.isTouchingWater()) {
                            if (moveEffect.emitsGameEvents()) {
                                this.emitGameEvent(GameEvent.SWIM);
                            }
                        } else {
                            if (moveEffect.emitsGameEvents() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                                this.emitGameEvent(GameEvent.STEP);
                            }
                        }
                    } else if (blockState.isAir()) {
                        this.addAirTravelEffects();
                    }
                }

                this.tryCheckBlockCollision();
                float i = this.getVelocityMultiplier();
                this.setVelocity(this.getVelocity().multiply(i, 1.0D, i));
            }
        }
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        ShapeContext shapeContext = ShapeContext.of(this);
        VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
        Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7D)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(movement), (entity) -> true);
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
        Vec3d vec3d = movement.lengthSquared() == 0.0D ? movement : adjustMovementForCollisions(this, movement, box, this.world, shapeContext, reusableStream);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl4 = this.onGround || bl2 && movement.y < 0.0D;
        if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
            Vec3d vec3d2 = adjustMovementForCollisions(this, new Vec3d(movement.x, (double)this.stepHeight, movement.z), box, this.world, shapeContext, reusableStream);
            Vec3d vec3d3 = adjustMovementForCollisions(this, new Vec3d(0.0D, (double)this.stepHeight, 0.0D), box.stretch(movement.x, 0.0D, movement.z), this.world, shapeContext, reusableStream);
            if (vec3d3.y < (double)this.stepHeight) {
                Vec3d vec3d4 = adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0D, movement.z), box.offset(vec3d3), this.world, shapeContext, reusableStream).add(vec3d3);
                if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
                    vec3d2 = vec3d4;
                }
            }

            if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
                return vec3d2.add(adjustMovementForCollisions(this, new Vec3d(0.0D, -vec3d2.y + movement.y, 0.0D), box.offset(vec3d2), this.world, shapeContext, reusableStream));
            }
        }

        return vec3d;
    }

    /**
     * We have to do this "inject" since the the applyClimbingSpeed() method
     * in LivingEntity is checking if we are a PlayerEntity, we want to apply the outcome of this check,
     * so this is why we need to set the y-velocity to 0.<p>
     */
    @Override
    public Vec3d method_26318(Vec3d movementInput, float f) {
        if (this.isClimbing() && this.getVelocity().getY() < 0.0D && !this.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && this.isHoldingOntoLadder()) {
            this.setVelocity(this.getVelocity().getX(), 0, this.getVelocity().getZ());
        }
        return super.method_26318(movementInput, f);
    }

    //TODO: relink?
    protected boolean canClimb() {
        return !this.onGround || !this.isSneaking();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.setMovementSpeed(sprinting ? 0.13F : 0.1F);
    }

    @Override
    public ItemStack getMainHandStack() {
        return new ItemStack(Items.AIR);
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        return equippedStack.get(slot);
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public Arm getMainArm() {
        // This is just for rendering
        return Arm.RIGHT;
    }

    @Override
    public MovementDummy clone() {
        return new MovementDummy(this);
    }
}
