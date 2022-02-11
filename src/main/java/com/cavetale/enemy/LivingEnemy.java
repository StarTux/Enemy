package com.cavetale.enemy;

import com.cavetale.worldmarker.entity.EntityMarker;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * An enemy wrapping one single LivingEntity.
 */
public abstract class LivingEnemy extends Enemy {
    protected LivingEntity living;
    private double backupSpeed;
    private double mountBackupSpeed;
    private boolean invulnerable;
    @Getter private boolean dead = false; // overrides
    @Getter private final Set<UUID> damagers = new HashSet<>();

    public LivingEnemy(final Context context) {
        super(context);
    }

    public final void markLiving() {
        EntityMarker.setId(living, WORLD_MARKER_ID);
        Handle handle = new Handle();
        EnemyPlugin.setHandle(living, handle);
        handle.onEnable();
    }

    /**
     * Called every tick while the LivingEntity is valid.
     */
    public abstract void tick();

    /**
     * Clean up.
     */
    @Override
    protected final void onRemove() {
        if (living == null) return;
        if (!living.isDead()) living.remove();
        living = null;
        cleanUp();
    }

    protected abstract void cleanUp();

    /**
     * Stored with the LivingEntity.
     * Holds reference to this.
     */
    public final class Handle implements EnemyHandle {
        private BukkitTask task;

        @Override
        public Enemy getEnemy() {
            return LivingEnemy.this;
        }

        public LivingEnemy getLivingEnemy() {
            return LivingEnemy.this;
        }

        @Override
        public void onEntityDeath(EntityDeathEvent event) {
            event.getDrops().clear();
            context.onDeath(LivingEnemy.this);
            onDeath();
        }

        @Override
        public void onEntityExplode(EntityExplodeEvent event) {
            context.onDeath(LivingEnemy.this);
            onDeath();
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

        @Override
        public void onEnable() {
            task = Bukkit.getScheduler().runTaskTimer(context.getPlugin(), LivingEnemy.this::tick, 0L, 1L);
        }

        @Override
        public void onDisable() {
            task.cancel();
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
        return living != null ? living.getWorld() : null;
    }

    /**
     * Passthrough.
     */
    @Override
    public Location getLocation() {
        return living != null ? living.getLocation() : null;
    }

    /**
     * Passthrough.
     */
    @Override
    public Location getEyeLocation() {
        return living != null ? living.getEyeLocation() : null;
    }

    /**
     * Passthrough.
     */
    @Override
    public boolean hasLineOfSight(Entity other) {
        return living != null ? living.hasLineOfSight(other) : false;
    }

    /**
     * Passthrough.
     */
    @Override
    public Component getDisplayName() {
        return living != null ? living.customName() : Component.empty();
    }

    /**
     * Get health from the entity.
     */
    @Override
    public double getHealth() {
        return living != null ? living.getHealth() : 0;
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
        return living != null
            ? living.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()
            : 0;
    }

    /**
     * Just call Mob#setTarget.
     */
    @Override
    public void setTarget(LivingEntity target) {
        if (!(living instanceof Mob)) return;
        ((Mob) living).setTarget(target);
    }

    /**
     * Just fetch Mob#getTarget.
     */
    @Override
    public LivingEntity getCurrentTarget() {
        return living instanceof Mob
            ? ((Mob) living).getTarget()
            : null;
    }

    /**
     * Is entity spawned?
     */
    @Override
    public boolean isValid() {
        return living != null && !living.isDead();
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
        return living != null
            ? living.launchProjectile(projectile, velocity)
            : null;
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
        return living != null ? living.getBoundingBox() : null;
    }

    /**
     * What happens on death? Overriders must call super:onDeath or
     * set dead to true.
     */
    protected void onDeath() {
        dead = true;
        remove();
    }

    /**
     * Something.
     */
    public void onDamage(EntityDamageEvent event) { }

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

    /**
     * Deny targeting other Enemies.
     */
    protected void onTarget(EntityTargetEvent event) {
        if (event.getTarget() != null && Enemy.of(event.getTarget()) != null) {
            event.setCancelled(true);
        }
    }

    protected void onPathfind(EntityPathfindEvent event) { }

    protected void onSpellCast(EntitySpellCastEvent event) { }

    protected void onRandomEvent(Event event) { }

    /**
     * Print some basic info.
     */
    @Override
    public String getInfo() {
        Location loc = getLocation();
        return (living != null ? living.getType().name() : getClass().getSimpleName())
            + ":" + loc.getWorld().getName()
            + ":" + loc.getBlockX()
            + "," + loc.getBlockY()
            + "," + loc.getBlockZ()
            + (isDead() ? "(dead)" : "");
    }
}
