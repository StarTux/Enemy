package com.cavetale.enemy;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpellCastEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * An EnemyHandle connects one or several Bukkit Entities with and
 * Enemy instance.  The map is managed by EnemyPlugin.
 */
public interface EnemyHandle {
    Enemy getEnemy();

    default void onEntityDeath(EntityDeathEvent event) { }

    default void onEntityExplode(EntityExplodeEvent event) { }

    default void onEntityDamage(EntityDamageEvent event) { }

    default void onEntityDamageByEntity(EntityDamageByEntityEvent event) { }

    default void onEntityTarget(EntityTargetEvent event) { }

    default void onEntityPathfind(EntityPathfindEvent event) { }

    default void onEntitySpellCast(EntitySpellCastEvent event) { }

    static EnemyHandle of(Entity entity) {
        return EnemyPlugin.getHandle(entity);
    }

    default void onRandomEvent(Event event) { }

    void onEnable();

    void onDisable();

    default void onRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        onDisable();
    }
}
