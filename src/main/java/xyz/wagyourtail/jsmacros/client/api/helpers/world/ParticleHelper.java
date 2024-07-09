package xyz.wagyourtail.jsmacros.client.api.helpers.world;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;
import xyz.wagyourtail.doclet.DocletReplaceParams;
import xyz.wagyourtail.doclet.DocletReplaceReturn;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IParticleManager;
import xyz.wagyourtail.jsmacros.client.api.classes.RegistryHelper;
import xyz.wagyourtail.jsmacros.client.api.classes.math.Pos3D;
import xyz.wagyourtail.jsmacros.client.mixins.access.MixinParticle;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.MethodWrapper;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author aMelonRind
 * @since 1.9.0
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ParticleHelper extends BaseHelper<Particle> {
    private static final Map<String, Class<? extends Particle>> PARTICLES = new HashMap<>();
    private static final Map<Class<? extends Particle>, String> PARTICLE_NAMES = new HashMap<>();

    public ParticleHelper(Particle base) {
        super(base);
    }

    /**
     * @return the particle name or null if it's not registered in the api
     */
    @Nullable
    @DocletReplaceReturn("ParticleName | null")
    public String getName() {
        return PARTICLE_NAMES.get(base.getClass());
    }

    public Pos3D getPos() {
        return new Pos3D(getX(), getY(), getZ());
    }

    public double getX() {
        return ((MixinParticle) base).getX();
    }

    public double getY() {
        return ((MixinParticle) base).getY();
    }

    public double getZ() {
        return ((MixinParticle) base).getZ();
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setPos(Pos3D pos) {
        return setPos(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setPos(double x, double y, double z) {
        base.setPos(x, y, z);
        ((MixinParticle) base).setPrevPosX(x);
        ((MixinParticle) base).setPrevPosY(y);
        ((MixinParticle) base).setPrevPosZ(z);
        return this;
    }

    public Pos3D getVelocity() {
        return new Pos3D(getVelocityX(), getVelocityY(), getVelocityZ());
    }

    public double getVelocityX() {
        return ((MixinParticle) base).getVelocityX();
    }

    public double getVelocityY() {
        return ((MixinParticle) base).getVelocityY();
    }

    public double getVelocityZ() {
        return ((MixinParticle) base).getVelocityZ();
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setVelocity(Pos3D vel) {
        return setVelocity(vel.getX(), vel.getY(), vel.getZ());
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setVelocity(double vx, double vy, double vz) {
        base.setVelocity(vx, vy, vz);
        return this;
    }

    public boolean isAlive() {
        return base.isAlive();
    }

    /**
     * @return self for chaining
     * @see ParticleHelper#remove()
     */
    public ParticleHelper markDead() {
        base.markDead();
        return this;
    }

    /**
     * same as {@code markDead().setPos(0, -999, 0)};
     * @return self for chaining
     */
    public ParticleHelper remove() {
        return markDead().setPos(0, -999, 0);
    }

    public int getAge() {
        return ((MixinParticle) base).getAge();
    }

    public int getMaxAge() {
        return base.getMaxAge();
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setMaxAge(int maxAge) {
        base.setMaxAge(maxAge);
        return this;
    }

    public int getColor() {
        return ((int) Math.floor(getRed()   * 255) << 16) +
               ((int) Math.floor(getGreen() * 255) <<  8) +
                (int) Math.floor(getBlue()  * 255);
    }

    public float getRed() {
        return ((MixinParticle) base).getRed();
    }

    public float getGreen() {
        return ((MixinParticle) base).getGreen();
    }

    public float getBlue() {
        return ((MixinParticle) base).getBlue();
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setColor(int color) {
        int r = 0xFF & (color >> 16);
        int g = 0xFF & (color >>  8);
        int b = 0xFF &  color;
        setColor(r / 255.0, g / 255.0, b / 255.0);
        return this;
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setColor(double r, double g, double b) {
        base.setColor((float) r, (float) g, (float) b);
        return this;
    }

    public float getAlpha() {
        return ((MixinParticle) base).getAlpha();
    }

    /**
     * @return self for chaining
     */
    public ParticleHelper setAlpha(double alpha) {
        ((MixinParticle) base).invokeSetAlpha((float) alpha);
        return this;
    }

    public static class Accessor {
        private static final ParticleManager pm = MinecraftClient.getInstance().particleManager;
        private static final Set<Accessor> activeAccessors = new HashSet<>();

        @Nullable
        private Class<? extends Particle> type;
        @Nullable
        public Pos3D pos;
        private double range = 256;
        /**
         * should always be range * range
         */
        private double rangeSq = 65536;
        public boolean cubeShape = true;
        @Nullable
        private ParticleTextureSheet sheet;
        @Nullable
        private ParticleTextureSheet sheetCache;
        public boolean shouldCacheSheet = true;
        @Nullable
        private MethodWrapper<ParticleHelper, ?, ?, ?> listener;

        public static void onTick(Queue<Particle> newParticles) {
            if (activeAccessors.isEmpty()) return;
            Set<Accessor> rem = null;
            synchronized (activeAccessors) {
                for (Accessor accessor : activeAccessors) {
                    for (Particle p : newParticles) if (!accessor.accept(p)) {
                        if (rem == null) rem = new HashSet<>();
                        rem.add(accessor);
                        break;
                    }
                }
                if (rem != null) activeAccessors.removeAll(rem);
            }
        }

        public ParticleManager getRawParticleManager() {
            return pm;
        }

        /**
         * same as {@code for (const particle of accessor.get()) particle.remove();}
         * @return self for chaining
         */
        public Accessor clearParticles() {
            if (type == null && pos == null) ((IParticleManager) pm).jsmacros_clearParticles();
            else for (Particle p : getInternal()) {
                p.markDead();
                p.setPos(0, -999, 0);
                ((MixinParticle) p).setPrevPosX(0);
                ((MixinParticle) p).setPrevPosY(-999);
                ((MixinParticle) p).setPrevPosZ(0);
            }
            return this;
        }

        /**
         * These names are subject to change and are only for an easier access. They will probably not
         * change in the future, but it is not guaranteed.
         *
         * @return a list of all particle names.
         * @since 1.9.0
         */
        @DocletReplaceReturn("JavaList<ParticleName>")
        public List<String> getParticleNames() {
            return ImmutableList.copyOf(PARTICLES.keySet());
        }

        /**
         * sets the particle type that this accessor is accessing
         * @param name the name of the particle's class
         * @return self for chaining
         */
        @DocletReplaceParams("name: ParticleName")
        public Accessor setTypeByName(String name) {
            try {
                setTypeByName(name, null);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e); // shouldn't happen
            }
            return this;
        }

        /**
         * sets the particle type that this accessor is accessing
         * @param name the name of the particle's class
         * @param classPath fallback classpath if the type doesn't resolve to a class
         * @return self for chaining
         * @throws ClassNotFoundException if the classPath doesn't resolve to a class
         */
        @SuppressWarnings("unchecked")
        @DocletReplaceParams("name: ParticleName, classPath: string | null")
        public Accessor setTypeByName(String name, @Nullable String classPath) throws ClassNotFoundException {
            Class<? extends Particle> res = PARTICLES.get(name);
            if (res == null) {
                if (classPath == null)
                    throw new NullPointerException("The specified particle type (" + name + ") is not registered in the api!");
                Class<?> get = Class.forName(classPath);
                if (!Particle.class.isAssignableFrom(get))
                    throw new RuntimeException("The class resolved from " + classPath + " is not a particle class!");
                res = (Class<? extends Particle>) get;
            }
            return setType(res);
        }

        /**
         * sets the particle type that this accessor is accessing
         * @param id the id of the particle
         * @return self for chaining
         */
        @DocletReplaceParams("id: ParticleId")
        public Accessor setTypeById(String id) {
            ParticleEffect effect = (ParticleEffect) Registries.PARTICLE_TYPE.get(RegistryHelper.parseIdentifier(id));
            if (effect == null) throw new NullPointerException("particle " + id + " not found");
            Particle particle = ((IParticleManager) pm).jsmacros_createParticle(effect, 0, 0, 0, 0, 0, 0);
            if (particle == null) throw new NullPointerException("particle " + id + " not found");
            return setType(particle.getClass());
        }

        /**
         * sets the particle type that this accessor is accessing
         * @param type particle's class, null for clear
         * @return self for chaining
         */
        public Accessor setType(@Nullable Class<? extends Particle> type) {
            this.type = type;
            sheetCache = sheet;
            return this;
        }

        @Nullable
        public Class<? extends Particle> getType() {
            return type;
        }

        @Nullable
        @DocletReplaceReturn("ParticleName")
        public String getTypeName() {
            return type == null ? null : PARTICLE_NAMES.get(type);
        }

        /**
         * sets the center pos for filtering
         * @return self for chaining
         */
        public Accessor setPos(@Nullable Pos3D pos) {
            this.pos = pos;
            return this;
        }

        /**
         * sets the center pos for filtering
         * @return self for chaining
         */
        public Accessor setPos(double x, double y, double z) {
            this.pos = new Pos3D(x, y, z);
            return this;
        }

        /**
         * sets the range for filtering
         * @return self for chaining
         */
        public Accessor setRange(double range) {
            if (range < 0) throw new IllegalArgumentException("Range cannot be negative!");
            this.range = range;
            rangeSq = range * range;
            return this;
        }

        public double getRange() {
            return range;
        }

        /**
         * sets the range shape for filtering
         * @param cube {@code false} for sphere, {@code true} for cube. default is cube
         * @return self for chaining
         */
        public Accessor setRangeShape(boolean cube) {
            cubeShape = cube;
            return this;
        }

        /**
         * sets the sheet for faster accessing.<br>
         * doesn't affect listener.
         * @return self for chaining
         */
        public Accessor setSheet(@Nullable ParticleTextureSheet sheet) {
            this.sheet = sheetCache = sheet;
            return this;
        }

        /**
         * if the accessor should cache the found particle's sheet for faster accessing. default is true.<br>
         * doesn't affect listener.
         * @return self for chaining
         */
        public Accessor setShouldCacheSheet(boolean should) {
            shouldCacheSheet = should;
            sheetCache = sheet;
            return this;
        }

        public List<ParticleHelper> get() {
            return getInternal().stream().map(ParticleHelper::new).collect(Collectors.toList());
        }

        private List<Particle> getInternal() {
            Map<ParticleTextureSheet, Queue<Particle>> particles = ((IParticleManager) pm).jsmacros_getParticles();
            List<Particle> res = new ArrayList<>();
            try {
                if (sheetCache == null) {
                    particles.values().forEach(q -> q.forEach(shouldCacheSheet && type != null ? p -> {
                        if (filter(p)) {
                            sheetCache = p.getType();
                            res.add(p);
                        }
                    } : p -> {
                        if (filter(p)) res.add(p);
                    }));
                } else {
                    Queue<Particle> q = particles.get(sheetCache);
                    if (q != null) for (Particle p : q) if (filter(p)) res.add(p);
                }
                return res;
            } catch (ConcurrentModificationException ignored) {}

            // second try, pretty rare to happen
            res.clear();
            try {
                if (sheetCache == null) {
                    for (Queue<Particle> q : List.copyOf(particles.values())) {
                        for (Particle p : List.copyOf(q)) if (filter(p)) res.add(p);
                    }
                } else {
                    Queue<Particle> q = particles.get(sheetCache);
                    if (q != null) for (Particle p : List.copyOf(q)) if (filter(p)) res.add(p);
                }
            } catch (ConcurrentModificationException cme) {
                JsMacros.LOGGER.warn("particleAccessor.getInternal() errors twice, which is not expected", cme);
            }
            return res;
        }

        /**
         * sets the listener for new particles
         * @param listener methodToJavaAsync for async, methodToJava for joined
         * @return self for chaining
         */
        public Accessor setListener(@Nullable MethodWrapper<ParticleHelper, ?, ?, ?> listener) {
            this.listener = listener;
            if (this.listener != null) synchronized (activeAccessors) {
                activeAccessors.add(this);
            }
            return this;
        }

        /**
         * @return false if this accessor should be removed
         */
        private boolean accept(Particle p) {
            if (listener == null) return false;
            if (filter(p)) try {
                listener.accept(new ParticleHelper(p));
            } catch (Throwable e) {
                Core.getInstance().profile.logError(e);
                listener = null;
                return false;
            }
            return true;
        }

        private boolean filter(Particle p) {
            if (p == null || !p.isAlive() || (type != null && !type.isInstance(p))) return false;
            if (pos == null) return true;
            double dx = Math.abs(((MixinParticle) p).getX() - pos.getX());
            if (dx > range) return false;
            double dy = Math.abs(((MixinParticle) p).getY() - pos.getY());
            if (dy > range) return false;
            double dz = Math.abs(((MixinParticle) p).getZ() - pos.getZ());
            if (dz > range) return false;
            return cubeShape || (dx * dx + dy * dy + dz * dz) <= rangeSq;
        }

    }

    @SuppressWarnings("SpellCheckingInspection")
    public static void init() {

        PARTICLES.put("AbstractDustParticle", net.minecraft.client.particle.AbstractDustParticle.class);
        PARTICLES.put("AnimatedParticle", net.minecraft.client.particle.AnimatedParticle.class);
        PARTICLES.put("AscendingParticle", net.minecraft.client.particle.AscendingParticle.class);
        PARTICLES.put("AshParticle", net.minecraft.client.particle.AshParticle.class);
        PARTICLES.put("BlockDustParticle", net.minecraft.client.particle.BlockDustParticle.class);
        PARTICLES.put("BlockFallingDustParticle", net.minecraft.client.particle.BlockFallingDustParticle.class);
        PARTICLES.put("BlockLeakParticle", net.minecraft.client.particle.BlockLeakParticle.class);
        PARTICLES.put("BlockMarkerParticle", net.minecraft.client.particle.BlockMarkerParticle.class);
        PARTICLES.put("BubbleColumnUpParticle", net.minecraft.client.particle.BubbleColumnUpParticle.class);
        PARTICLES.put("BubblePopParticle", net.minecraft.client.particle.BubblePopParticle.class);
        PARTICLES.put("CampfireSmokeParticle", net.minecraft.client.particle.CampfireSmokeParticle.class);
        PARTICLES.put("CherryLeavesParticle", net.minecraft.client.particle.CherryLeavesParticle.class);
        PARTICLES.put("CloudParticle", net.minecraft.client.particle.CloudParticle.class);
        PARTICLES.put("CrackParticle", net.minecraft.client.particle.CrackParticle.class);
        PARTICLES.put("CurrentDownParticle", net.minecraft.client.particle.CurrentDownParticle.class);
        PARTICLES.put("DamageParticle", net.minecraft.client.particle.DamageParticle.class);
        PARTICLES.put("DragonBreathParticle", net.minecraft.client.particle.DragonBreathParticle.class);
        PARTICLES.put("DustColorTransitionParticle", net.minecraft.client.particle.DustColorTransitionParticle.class);
        PARTICLES.put("ElderGuardianAppearanceParticle", net.minecraft.client.particle.ElderGuardianAppearanceParticle.class);
        PARTICLES.put("EmitterParticle", net.minecraft.client.particle.EmitterParticle.class);
        PARTICLES.put("EmotionParticle", net.minecraft.client.particle.EmotionParticle.class);
        PARTICLES.put("EnchantGlyphParticle", net.minecraft.client.particle.EnchantGlyphParticle.class);
        PARTICLES.put("EndRodParticle", net.minecraft.client.particle.EndRodParticle.class);
        PARTICLES.put("ExplosionEmitterParticle", net.minecraft.client.particle.ExplosionEmitterParticle.class);
        PARTICLES.put("ExplosionLargeParticle", net.minecraft.client.particle.ExplosionLargeParticle.class);
        PARTICLES.put("ExplosionSmokeParticle", net.minecraft.client.particle.ExplosionSmokeParticle.class);
        PARTICLES.put("FireSmokeParticle", net.minecraft.client.particle.FireSmokeParticle.class);
        PARTICLES.put("FireworksSparkParticle$FireworkParticle", net.minecraft.client.particle.FireworksSparkParticle.FireworkParticle.class);
        PARTICLES.put("FireworksSparkParticle$Flash", net.minecraft.client.particle.FireworksSparkParticle.Flash.class);
        PARTICLES.put("FishingParticle", net.minecraft.client.particle.FishingParticle.class);
        PARTICLES.put("FlameParticle", net.minecraft.client.particle.FlameParticle.class);
        PARTICLES.put("GlowParticle", net.minecraft.client.particle.GlowParticle.class);
        PARTICLES.put("ItemPickupParticle", net.minecraft.client.particle.ItemPickupParticle.class);
        PARTICLES.put("LargeFireSmokeParticle", net.minecraft.client.particle.LargeFireSmokeParticle.class);
        PARTICLES.put("LavaEmberParticle", net.minecraft.client.particle.LavaEmberParticle.class);
        PARTICLES.put("NoRenderParticle", net.minecraft.client.particle.NoRenderParticle.class);
        PARTICLES.put("NoteParticle", net.minecraft.client.particle.NoteParticle.class);
        PARTICLES.put("PortalParticle", net.minecraft.client.particle.PortalParticle.class);
        PARTICLES.put("RainSplashParticle", net.minecraft.client.particle.RainSplashParticle.class);
        PARTICLES.put("RedDustParticle", net.minecraft.client.particle.RedDustParticle.class);
        PARTICLES.put("ReversePortalParticle", net.minecraft.client.particle.ReversePortalParticle.class);
        PARTICLES.put("SculkChargeParticle", net.minecraft.client.particle.SculkChargeParticle.class);
        PARTICLES.put("SculkChargePopParticle", net.minecraft.client.particle.SculkChargePopParticle.class);
        PARTICLES.put("ShriekParticle", net.minecraft.client.particle.ShriekParticle.class);
        PARTICLES.put("SnowflakeParticle", net.minecraft.client.particle.SnowflakeParticle.class);
        PARTICLES.put("SonicBoomParticle", net.minecraft.client.particle.SonicBoomParticle.class);
        PARTICLES.put("SoulParticle", net.minecraft.client.particle.SoulParticle.class);
        PARTICLES.put("SpellParticle", net.minecraft.client.particle.SpellParticle.class);
        PARTICLES.put("SpitParticle", net.minecraft.client.particle.SpitParticle.class);
        PARTICLES.put("SquidInkParticle", net.minecraft.client.particle.SquidInkParticle.class);
        PARTICLES.put("SuspendParticle", net.minecraft.client.particle.SuspendParticle.class);
        PARTICLES.put("SweepAttackParticle", net.minecraft.client.particle.SweepAttackParticle.class);
        PARTICLES.put("TotemParticle", net.minecraft.client.particle.TotemParticle.class);
        PARTICLES.put("VibrationParticle", net.minecraft.client.particle.VibrationParticle.class);
        PARTICLES.put("WaterBubbleParticle", net.minecraft.client.particle.WaterBubbleParticle.class);
        PARTICLES.put("WaterSplashParticle", net.minecraft.client.particle.WaterSplashParticle.class);
        PARTICLES.put("WaterSuspendParticle", net.minecraft.client.particle.WaterSuspendParticle.class);
        PARTICLES.put("WhiteAshParticle", net.minecraft.client.particle.WhiteAshParticle.class);

        PARTICLES.forEach((name, clazz) -> PARTICLE_NAMES.put(clazz, name));
    }

    public static void main(String[] args) throws IOException {
        Set<String> skip = Set.of( // these class has private access
                "BlockLeakParticle$ContinuousFalling",
                "BlockLeakParticle$Dripping",
                "BlockLeakParticle$DrippingLava",
                "BlockLeakParticle$DripstoneLavaDrip",
                "BlockLeakParticle$Falling",
                "BlockLeakParticle$FallingHoney",
                "BlockLeakParticle$Landing",
                "FireworksSparkParticle$Explosion"
        );
        Map<String, String> classes = new TreeMap<>();
        ParticleHelper.init();
        int flags = Modifier.ABSTRACT | Modifier.PRIVATE | Modifier.PROTECTED;
        ClassPath.from(ParticleHelper.class.getClassLoader())
                .getTopLevelClassesRecursive("net.minecraft.client.particle")
                .stream()
                .map(ClassPath.ClassInfo::load)
                .flatMap(c -> Stream.concat(Stream.of(c), Arrays.stream(c.getDeclaredClasses())))
                .filter(Particle.class::isAssignableFrom)
                .filter(c -> (c.getModifiers() & flags) == 0)
                .filter(c -> !PARTICLES.containsValue(c))
                .filter(c -> !c.equals(Particle.class))
                .forEach(c -> {
                    String name;
                    if (c.getEnclosingClass() != null) {
                        name = c.getEnclosingClass().getSimpleName() + "$" + c.getSimpleName();
                    } else {
                        name = c.getSimpleName();
                    }
                    classes.put(name, c.getCanonicalName() + ".class");
                });
        for (String name : skip) classes.remove(name);
        if (!classes.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : classes.entrySet()) {
                builder.append("PARTICLES.put(\"").append(entry.getKey()).append("\", ").append(entry.getValue()).append(");").append(System.lineSeparator());
            }
            System.out.println(builder);
        }
    }

}
