package com.cavetale.enemy;

import com.cavetale.enemy.ability.EggLauncherAbility;
import com.cavetale.enemy.ability.FireworkAbility;
import com.cavetale.enemy.ability.LightningAbility;
import com.cavetale.mytems.event.combat.DamageCalculationEvent;
import com.cavetale.mytems.event.combat.DamageFactor;
import com.cavetale.worldmarker.entity.EntityMarker;
import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.destroystokyo.paper.event.entity.WitchConsumePotionEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
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
        Enemy enemy = Enemy.of(event.getEntity());
        if (enemy != null) {
            if (enemy.isInvulnerable()) event.setCancelled(true);
            switch (event.getCause()) {
            case ENTITY_ATTACK:
            case PROJECTILE:
                enemy.setLastDamage(System.currentTimeMillis());
            default: break;
            }
        }
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityDamage(event);
    }

    @EventHandler(ignoreCancelled = false)
    void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Enemy attackerEnemy = Enemy.of(event.getDamager());
        Enemy targetEnemy = Enemy.of(event.getEntity());
        if (attackerEnemy != null && targetEnemy != null) {
            event.setCancelled(true);
        }
        if (event.getCause() == DamageCause.ENTITY_EXPLOSION && EntityMarker.hasId(event.getDamager(), EggLauncherAbility.EXPLOSIVE_EGG_ID)) {
            if (!(event.getEntity() instanceof Player)) {
                event.setCancelled(true);
            }
        } else if (EntityMarker.hasId(event.getDamager(), FireworkAbility.FIREWORK_ID)) {
            if (event.getEntity() instanceof Player) {
                event.setDamage(15.0);
            } else {
                event.setCancelled(true);
            }
        } else if (EntityMarker.hasId(event.getDamager(), LightningAbility.LIGHTNING_ID)) {
            if (event.getEntity() instanceof Player) {
                event.setDamage(15.0);
            } else {
                event.setCancelled(true);
            }
        }
        EnemyHandle handle = EnemyHandle.of(event.getEntity());
        if (handle == null) return;
        handle.onEntityDamageByEntity(event);
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        final Projectile proj = event.getEntity();
        // Explosive egg ability
        if (EntityMarker.hasId(proj, EggLauncherAbility.EXPLOSIVE_EGG_ID)) {
            if (!(event.getHitEntity() instanceof Player)) {
                event.setCancelled(true);
            } else {
                proj.getWorld().createExplosion(proj, 1.0f);
                proj.remove();
            }
        } else if (EntityMarker.hasId(proj, FireworkAbility.FIREWORK_ID)) {
            // doesn't seem to work with fireworks
            if (!(event.getHitEntity() instanceof Player)) {
                event.setCancelled(true);
            }
        }
        // Prevent enemies hitting other enemies
        if (proj.getShooter() instanceof Entity shooterEntity && event.getHitEntity() != null) {
            final Enemy shooterEnemy = Enemy.of(shooterEntity);
            final Enemy targetEnemy = Enemy.of(event.getHitEntity());
            if (shooterEnemy != null && targetEnemy != null) {
                event.setCancelled(true);
            }
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

    @EventHandler(priority = EventPriority.HIGH)
    protected void onDamageCalculation(DamageCalculationEvent event) {
        Enemy enemy = Enemy.of(event.getTarget());
        if (enemy == null) return;
        enemy.onDefendingDamageCalculation(event);
        if (enemy instanceof TypedEnemy typed && typed.isBoss()) {
            // Mark as handled, just to be on the safe side.
            event.setHandled(true);
            final var calc = event.getCalculation();
            // Give bosses full armor and enchantment protection.
            calc.setIfApplicable(DamageFactor.ARMOR, 0.2);
            calc.setIfApplicable(DamageFactor.PROTECTION, 0.2);
            calc.setIfApplicable(DamageFactor.RESISTANCE, 0.5);
            // Lower arrow damage to bosses.
            // Arrow damage multiplies its damage value with velocity.
            if (calc.getProjectile() instanceof AbstractArrow arrow && !(arrow instanceof Trident) && arrow.getShooter() instanceof Player shooter) {
                final double damage = Math.max(0.0, calc.getBaseDamage() - 12.0);
                arrow.setDamage(damage);
                if (damage < 0.01) {
                    calc.getEvent().setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    protected void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Enemy enemy = Enemy.of(event.getEntity());
        if (enemy != null) enemy.onEntityRegainHealth(event);
    }

    @EventHandler
    protected void onEntityPotionEffect(EntityPotionEffectEvent event) {
        Enemy enemy = Enemy.of(event.getEntity());
        if (enemy == null) return;
        enemy.onEntityPotionEffect(event);
        if (enemy instanceof TypedEnemy typed && typed.isBoss()) {
            switch (event.getAction()) {
            case ADDED: break;
            default: return;
            }
            switch (event.getCause()) {
            case AREA_EFFECT_CLOUD:
            case ARROW:
            case ATTACK:
            case POTION_SPLASH:
            case WITHER_ROSE:
                break;
            default: return;
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onEntityCombust(EntityCombustEvent event) {
        Enemy enemy = Enemy.of(event.getEntity());
        if (enemy == null) return;
        if (enemy instanceof TypedEnemy typed && typed.isBoss()) {
            event.setCancelled(true);
        }
    }
}
