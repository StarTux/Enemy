package com.cavetale.enemy;

import com.cavetale.mytems.event.combat.DamageCalculationEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
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
    @Getter @Setter protected Context context;
    @Getter @Setter protected Location spawnLocation;
    private boolean removed;
    @Getter @Setter private long lastDamage; // Last legit combat dmg!
    @Getter private long spawnTime;
    @Getter @Setter private long glowTime;
    @Getter @Setter protected int difficultyLevel;

    protected Enemy(@NonNull final Context context) {
        this.enemyId = nextEnemyId++;
        ID_MAP.put(enemyId, this);
        this.context = context;
        this.spawnTime = System.currentTimeMillis();
    }

    public static Enemy ofEnemyId(int theEnemyId) {
        return ID_MAP.get(theEnemyId);
    }

    public final void resetContext() {
        this.context = DefaultContext.INSTANCE;
    }

    public abstract void spawn(Location location);

    public final void spawn() {
        spawn(spawnLocation);
        this.spawnTime = System.currentTimeMillis();
    }

    /**
     * Overriders must call super!
     */
    public final void remove() {
        if (removed) return;
        removed = true;
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

    /**
     * Is spawned and still exists?
     * If this yields true, several others must not return null:
     * - Locations
     */
    public abstract boolean isValid();

    public abstract boolean isAlive();

    public abstract boolean isDead();

    public abstract double getHealth();

    public abstract void setHealth(double h);

    public abstract double getMaxHealth();

    public abstract void setTarget(LivingEntity target);

    public abstract LivingEntity getCurrentTarget();

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
        if (entity == null) return null;
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
        if (!isValid()) return getClass().getSimpleName();
        Location loc = getLocation();
        return getClass().getSimpleName()
            + ":" + loc.getWorld().getName()
            + ":" + loc.getBlockX()
            + "," + loc.getBlockY()
            + "," + loc.getBlockZ()
            + (isDead() ? "(dead)" : "");
    }

    /**
     * Find a valid player target for this Enemy.
     * We try to find a player visible to this enemy.  Failing that,
     * we make due with just any player.
     */
    public Player findPlayerTarget() {
        if (!isValid()) return null;
        List<Player> players = new ArrayList<>(context.getPlayers(this));
        players.removeIf(p -> {
                switch (p.getGameMode()) {
                case SURVIVAL: case ADVENTURE: return false;
                case CREATIVE: case SPECTATOR: default: return true;
                } });
        if (players.isEmpty()) return null;
        Location eye = getEyeLocation();
        double minVisible = Double.MAX_VALUE;
        double minBlind = Double.MAX_VALUE;
        Player visible = null;
        Player blind = null;
        final double maxVisible = 48 * 48;
        final double maxBlind = 32 * 32;
        for (Player player : players) {
            if (!((LivingEntity) player).isOnGround()) continue;
            double dist = player.getEyeLocation().distanceSquared(eye);
            if (hasLineOfSight(player)) {
                if (dist < minVisible && dist < maxVisible) {
                    visible = player;
                    minVisible = dist;
                }
            } else {
                if (dist < minBlind && dist < maxBlind) {
                    blind = player;
                    minBlind = dist;
                }
            }
        }
        if (visible == null && blind == null) return null;
        Player target = visible != null ? visible : blind;
        if (getLivingEntity() != null && !new EntityTargetEvent(getLivingEntity(), target, EntityTargetEvent.TargetReason.CUSTOM).callEvent()) {
            return null;
        }
        setTarget(target);
        return target;
    }

    public void onDefendingDamageCalculation(DamageCalculationEvent event) { }

    public void onEntityPotionEffect(EntityPotionEffectEvent event) { }

    public void onEntityRegainHealth(EntityRegainHealthEvent event) { }
}
