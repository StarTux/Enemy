package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.TypedEnemy;
import lombok.Getter;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public abstract class LivingBoss extends LivingEnemy implements TypedEnemy {
    @Getter protected double maxHealth = 500;
    @Getter protected double health = 500;
    protected final EnemyType enemyType;

    protected LivingBoss(final Context context, final EnemyType enemyType, final EntityType entityType) {
        super(context, entityType);
        this.enemyType = enemyType;
    }

    @Override
    public final EnemyType getEnemyType() {
        return this.enemyType;
    }

    /**
     * This must be called before spawning the boss!
     */
    public final void setMaxHealth(final double value) {
        this.maxHealth = value;
        this.health = value;
    }

    /**
     * Cancel conventional damage.
     */
    @Override
    public void onDamage(EntityDamageEvent event) {
        switch (event.getCause()) {
        case CONTACT:
        case SUFFOCATION:
        case LAVA:
            event.setCancelled(true);
        default: break;
        }
    }

    /**
     * Make immune to potion effects.
     */
    @Override
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        switch (event.getCause()) {
        case AREA_EFFECT_CLOUD:
        case ARROW:
        case ATTACK:
        case WITHER_ROSE:
            event.setCancelled(true);
        default: break;
        }
    }
}
