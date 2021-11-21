package com.cavetale.enemy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

/**
 * An Enemy instance represents a unit which fights the player and
 * vice versa. It could be a wrapper for a literal bukkit Entity, a
 * collection of entities, or entirely virtual.
 */
public abstract class Enemy {
    public static final String WORLD_MARKER_ID = "raid:enemy";
    private static int nextEnemyId = 1;
    protected static final Map<Integer, Enemy> ID_MAP = new HashMap<>();
    @Getter protected final int enemyId;

    protected Enemy() {
        this.enemyId = nextEnemyId++;
        ID_MAP.put(enemyId, this);
    }

    public static Enemy ofEnemyId(int theEnemyId) {
        return ID_MAP.get(theEnemyId);
    }

    public abstract Context getContext();

    public abstract void spawn(Location location);

    /**
     * Spawn at the default spawn location.
     */
    public void spawn() {
        spawn(getSpawnLocation());
    }

    /**
     * The default spawn location for this enemy.
     */
    public Location getSpawnLocation() {
        return getContext().getSpawnLocation();
    }

    public abstract void setSpawnLocation(Location location);

    /**
     * Overriders must call super!
     */
    public final void remove() {
        onRemove();
        ID_MAP.remove(this.enemyId);
    }

    protected abstract void onRemove();

    public abstract World getWorld();

    public abstract Location getLocation();

    public abstract Location getEyeLocation();

    public abstract boolean hasLineOfSight(Entity other);

    public abstract Component getDisplayName();

    public abstract <T extends Projectile> T launchProjectile(Class<T> projectile, Vector velocity);

    public abstract void setInvulnerable(boolean invulnerable);

    public abstract void setImmobile(boolean immobile);

    public abstract boolean isInvulnerable();

    /**
     * May return null.
     */
    public abstract LivingEntity getLivingEntity();

    /**
     * May return null.
     */
    public abstract Mob getMob();

    public abstract void teleport(Location to);

    public abstract BoundingBox getBoundingBox();

    public abstract Collection<UUID> getDamagers();

    public abstract boolean isValid();

    public abstract boolean isAlive();

    public abstract boolean isDead();

    public abstract double getHealth();

    public abstract void setHealth(double h);

    public abstract double getMaxHealth();

    /**
     * Get all damagers who are currently online.
     */
    public List<Player> getPlayerDamagers() {
        return getDamagers().stream()
            .map(Bukkit::getPlayer)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Get the Enemy that belongs to this entity, or null if none is found.
     */
    public static Enemy of(Entity entity) {
        EnemyHandle handle = EnemyPlugin.getHandle(entity);
        if (handle == null) return null;
        return handle.getEnemy();
    }

    /**
     * Customize drops if desired.
     */
    public List<ItemStack> getDrops() {
        return Collections.emptyList();
    }

    /**
     * Print some basic info used in commands.
     */
    public String getInfo() {
        Location loc = getLocation();
        return getClass().getSimpleName()
            + ":" + loc.getWorld().getName()
            + ":" + loc.getBlockX()
            + "," + loc.getBlockY()
            + "," + loc.getBlockZ()
            + (isDead() ? "(dead)" : "");
    }
}
