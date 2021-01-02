package com.cavetale.enemy;

import com.cavetale.worldmarker.EntityMarker;
import com.cavetale.worldmarker.MarkTagContainer;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * An enemy wrapping a LivingEntity.
 */
public abstract class LivingEnemy implements Enemy {
    @Getter protected final Context context;
    protected LivingEntity living;
    private double backupSpeed;
    private double mountBackupSpeed;
    private boolean invulnerable;
    @Getter private boolean dead = false; // overrides
    @Getter private final Set<UUID> damagers = new HashSet<>();
    @Setter protected Location spawnLocation;

    public LivingEnemy(final Context context) {
        this.context = context;
    }

    public final void markLiving() {
        EntityMarker.setId(living, WORLD_MARKER_ID);
        EntityMarker.getEntity(living).getPersistent(context.getPlugin(), WORLD_MARKER_ID, Handle.class, () -> new Handle());
    }

    /**
     * The location where this enemy should spawn or respawn, or go home.
     */
    @Override
    public Location getSpawnLocation() {
        return spawnLocation != null ? spawnLocation : context.getSpawnLocation();
    }

    /**
     * Called every tick while the LivingEntity is valid.
     */
    public abstract void tick();

    /**
     * Passthrough.
     */
    @Override
    public void remove() {
        if (living == null) return;
        living.remove();
        living = null;
    }

    /**
     * Stored with the LivingEntity.
     * Holds reference to this.
     */
    public final class Handle implements EnemyHandle {
        public JavaPlugin getPlugin() {
            return context.getPlugin();
        }

        @Override
        public boolean shouldSave() {
            return false;
        }

        @Override
        public Enemy getEnemy() {
            return LivingEnemy.this;
        }

        public LivingEnemy getLivingEnemy() {
            return LivingEnemy.this;
        }

        @Override
        public void onTick(MarkTagContainer container) {
            tick();
        }

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            event.getDrops().clear();
            onDeath();
            context.onDeath(LivingEnemy.this);
        }

        @Override
        public void onEntityExplode(EntityExplodeEvent event) {
            onDeath();
            context.onDeath(LivingEnemy.this);
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            onDamage(event);
        }

        @Override
        public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
            onDamageByEntity(event);
        }

        @Override
        public void onEntityTarget(EntityTargetEvent event) {
            onTarget(event);
        }

        @Override
        public void onEntityPathfind(EntityPathfindEvent event) {
            onPathfind(event);
        }

        @Override
        public void onEntitySpellCast(EntitySpellCastEvent event) {
            onSpellCast(event);
        }

        @Override
        public void onRandomEvent(Event event) {
            LivingEnemy.this.onRandomEvent(event);
        }
    }

    /**
     * Property. Contract allows null.
     */
    @Override
    public LivingEntity getLivingEntity() {
        return living;
    }

    /**
     * Property. Contract allows null.
     */
    @Override
    public Mob getMob() {
        return living instanceof Mob ? (Mob) living : null;
    }

    /**
     * Passthrough.
     */
    @Override
    public void teleport(Location to) {
        if (living == null) return;
        living.teleport(to);
    }

    /**
     * Passthrough.
     */
    @Override
    public World getWorld() {
        if (living == null) return context.getWorld();
        return living.getWorld();
    }

    /**
     * Passthrough.
     */
    @Override
    public Location getLocation() {
        if (living == null) return getSpawnLocation();
        return living.getLocation();
    }

    /**
     * Passthrough.
     */
    @Override
    public Location getEyeLocation() {
        if (living == null) return getSpawnLocation();
        return living.getEyeLocation();
    }

    /**
     * Passthrough.
     */
    @Override
    public boolean hasLineOfSight(Entity other) {
        if (living == null) return false;
        return living.hasLineOfSight(other);
    }

    /**
     * Passthrough.
     */
    @Override
    public String getDisplayName() {
        if (living == null) return "";
        return living.getCustomName();
    }

    /**
     * Get health from the entity.
     */
    @Override
    public double getHealth() {
        if (living == null) return 0;
        return living.getHealth();
    }

    /**
     * Passthrough.
     */
    @Override
    public void setHealth(double health) {
        if (living == null) return;
        living.setHealth(health);
    }

    /**
     * Get max health from the entity.
     */
    @Override
    public double getMaxHealth() {
        if (living == null) return 0;
        return living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
    }

    /**
     * Is entity spawned?
     */
    @Override
    public boolean isValid() {
        return living != null && living.isValid();
    }

    /**
     * Did we ever die?
     */
    @Override
    public boolean isAlive() {
        return !dead;
    }

    /**
     * Passthrough.
     */
    @Override
    public <T extends Projectile> T launchProjectile(Class<T> projectile, Vector velocity) {
        if (living == null) return null;
        return living.launchProjectile(projectile, velocity);
    }

    /**
     * Property.
     */
    @Override
    public boolean isInvulnerable() {
        return invulnerable;
    }

    /**
     * Property.
     */
    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    /**
     * Do your magic.
     */
    @Override
    public void setImmobile(boolean immobile) {
        if (living == null) return;
        if (immobile) {
            backupSpeed = living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
            living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
            Entity vehicle = living.getVehicle();
            if (vehicle instanceof Mob) {
                Mob veh = (Mob) vehicle;
                mountBackupSpeed = veh.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
                veh.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
            }
        } else {
            living.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(backupSpeed);
            Entity vehicle = living.getVehicle();
            if (vehicle instanceof Mob) {
                Mob veh = (Mob) vehicle;
                veh.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(mountBackupSpeed);
            }
        }
    }

    /**
     * Passthrough.
     */
    @Override
    public BoundingBox getBoundingBox() {
        if (living == null) return null;
        return living.getBoundingBox();
    }

    /**
     * What happens on death? Overriders must call super:onDeath or
     * set dead to true.
     */
    protected void onDeath() {
        dead = true;
    }

    /**
     * Something.
     */
    public void onDamage(EntityDamageEvent event) {
    }

    /**
     * Keep tabs on enemies.
     */
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (living.equals(event.getEntity())) {
            damagedBy(event.getDamager(), event);
        }
    }

    private void damagedBy(Entity damager, EntityDamageByEntityEvent event) {
        if (damager == null) {
            return;
        } else if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;
            if (projectile.getShooter() instanceof Entity) {
                damagedBy((Entity) projectile.getShooter(), event);
            }
        } else {
            damagers.add(damager.getUniqueId());
        }
    }

    protected void onTarget(EntityTargetEvent event) { }

    protected void onPathfind(EntityPathfindEvent event) { }

    protected void onSpellCast(EntitySpellCastEvent event) { }

    protected void onRandomEvent(Event event) { }
}
