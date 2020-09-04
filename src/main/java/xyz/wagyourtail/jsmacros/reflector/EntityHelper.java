package xyz.wagyourtail.jsmacros.reflector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.runscript.classes.common.MathHelper;

public class EntityHelper {
    protected Entity e;
    
    public EntityHelper(Entity e) {
        this.e = e;
    }
    
    public MathHelper.Pos3D getPos() {
        if (e == null) return null;
        return new MathHelper.Pos3D(e.x, e.y, e.z);
    }
    
    public double getX() {
        return e.getX();
    }

    public double getY() {
        return e.getY();
    }
    
    public double getZ() {
        return e.getZ();
    }
    
    public float getPitch() {
        return e.pitch;
    }
    
    public float getYaw() {
        return MathHelper.fwrapDegrees(e.yaw);
    }
    
    public String getName() {
        if (e == null) return null;
        return e.getName().getString();
    }
    
    public String getType() {
        if (e == null) return null;
        return EntityType.getId(e.getType()).toString();
    }
    
    public boolean isGlowing() {
        return e.isGlowing();
    }
    
    public boolean isInLava() {
        return e.isInLava();
    }
    
    public boolean isOnFire() {
        return e.isOnFire();
    }
    
    public EntityHelper getVehicle() {
        Entity parent = e.getVehicle();
        if (parent != null) return new EntityHelper(parent);
        return null;
    }
    
    public List<EntityHelper> getPassengers() {
        List<EntityHelper> entities = e.getPassengerList().stream().map((e) -> new EntityHelper(e)).collect(Collectors.toList());
        return entities.size() == 0 ? null : entities;
        
    }
    
    public EntityHelper setGlowing(boolean val) {
        e.setGlowing(val);
        return this;
    }
    
    public List<StatusEffectHelper> getStatusEffects() {
        if (!(e instanceof LivingEntity)) return null;
        List<StatusEffectHelper> l = new ArrayList<>();
        for (StatusEffectInstance i : ImmutableList.copyOf(((LivingEntity) e).getStatusEffects())) {
            l.add(new StatusEffectHelper(i));
        }
        return l;
    }
    
    public Entity getRaw() {
        return e;
    }
    
    public String toString() {
        return String.format("Entity:{\"name\":\"%s\", \"type\":\"%s\"}", this.getName(), this.getType());
    }
    
    public static EntityHelper create(Entity e) {
        if (e instanceof ClientPlayerEntity) return new ClientPlayerEntityHelper((ClientPlayerEntity) e);
        if (e instanceof PlayerEntity) return new PlayerEntityHelper((PlayerEntity) e);
        return new EntityHelper(e);
    }
}