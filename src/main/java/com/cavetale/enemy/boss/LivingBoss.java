package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.TypedEnemy;
import com.cavetale.mytems.event.combat.DamageCalculationEvent;
import com.cavetale.mytems.event.combat.DamageFactor;
import org.bukkit.event.entity.EntityDamageEvent;

public abstract class LivingBoss extends LivingEnemy implements TypedEnemy {
    protected final EnemyType enemyType;

    protected LivingBoss(final Context context, final EnemyType enemyType) {
        super(context);
        this.enemyType = enemyType;
    }

    @Override
    public final EnemyType getEnemyType() {
        return this.enemyType;
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
     * Make sure the boss has a reasonable minimum armor and
     * protection enchantments.
     */
    @Override
    public void onDefendingDamageCalculation(DamageCalculationEvent event) {
        event.setIfApplicable(DamageFactor.ARMOR, value -> Math.min(0.33, value));
        event.setIfApplicable(DamageFactor.PROTECTION, value -> Math.min(0.33, value));
    }
}
