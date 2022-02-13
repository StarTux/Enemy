package com.cavetale.enemy;

import com.cavetale.enemy.ability.EggLauncherAbility;
import com.cavetale.enemy.ability.FireworkAbility;
import com.cavetale.mytems.event.combat.DamageCalculationEvent;
import com.cavetale.worldmarker.entity.EntityMarker;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.destroystokyo.paper.event.entity.WitchConsumePotionEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public final class EnemyListener implements Listener {
    private final JavaPlugin plugin;

    public EnemyListener enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        return this;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void onEntityDeath(EntityDeathEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityDeath(event);
    }

    /**
     * When an entity explodes.
     * NOTE: This will not be called if mobGriefing is set to false...
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    void onEntityExplode(EntityExplodeEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityExplode(event);
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityDamage(EntityDamageEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityDamage(event);
    }

    @EventHandler(ignoreCancelled = false)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == DamageCause.ENTITY_EXPLOSION && EntityMarker.hasId(event.getDamager(), EggLauncherAbility.EXPLOSIVE_EGG_ID)) {
            if (!(event.getEntity() instanceof Player)) {
                event.setCancelled(true);
            }
        } else if (EntityMarker.hasId(event.getDamager(), FireworkAbility.FIREWORK_ID)) {
            if (event.getEntity() instanceof Player) {
                double base = event.getFinalDamage();
                event.setDamage(base * 1.25);
            } else {
                event.setCancelled(true);
            }
        }
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityDamageByEntity(event);
    }

    @EventHandler(ignoreCancelled = false)
    void onProjectileCollide(ProjectileCollideEvent event) {
        Projectile proj = event.getEntity();
        if (proj.getShooter() instanceof Entity shooterEntity) {
            Enemy shooterEnemy = Enemy.of(shooterEntity);
            Enemy targetEnemy = Enemy.of(event.getCollidedWith());
            if (shooterEnemy != null && targetEnemy != null) {
                event.setCancelled(true);
            }
        }
        String id = EntityMarker.getId(proj);
        if (id == null) return;
        switch (id) {
        case EggLauncherAbility.EXPLOSIVE_EGG_ID:
        case FireworkAbility.FIREWORK_ID: // doesn't seem to work with fireworks
            if (!(event.getCollidedWith() instanceof Player)) {
                event.setCancelled(true);
            }
            break;
        default: break;
        }
    }

    @EventHandler
    void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        if (EntityMarker.hasId(proj, EggLauncherAbility.EXPLOSIVE_EGG_ID)) {
            proj.getWorld().createExplosion(proj, 1.0f);
            proj.remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    void onEntityTarget(EntityTargetEvent event) {
        if (event.getReason() == EntityTargetEvent.TargetReason.CUSTOM) return;
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityTarget(event);
    }

    @EventHandler
    void onEntityPathfind(EntityPathfindEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityPathfind(event);
    }

    @EventHandler
    void onEntitySpellCat(EntitySpellCastEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntitySpellCast(event);
    }

    @EventHandler
    void onWitchPotionConsume(WitchConsumePotionEvent event) {
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onRandomEvent(event);
    }

    @EventHandler
    void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        EnemyHandle handle = EnemyPlugin.removeHandle(event.getEntity());
        if (handle == null) return;
        handle.onRemoveFromWorld(event);
    }

    @EventHandler
    void onPluginDisable(PluginDisableEvent event) {
        for (Enemy enemy : Enemy.ID_MAP.values()) {
            if (enemy.getContext().getPlugin() == event.getPlugin()) {
                enemy.resetContext();
            }
        }
    }

    @EventHandler
    protected void onDamageCalculation(DamageCalculationEvent event) {
        Enemy enemy = Enemy.of(event.getTarget());
        if (enemy == null) return;
        enemy.onDefendingDamageCalculation(event);
    }

    @EventHandler
    protected void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Enemy enemy = Enemy.of(event.getEntity());
        if (enemy != null) enemy.onEntityRegainHealth(event);
    }
}
