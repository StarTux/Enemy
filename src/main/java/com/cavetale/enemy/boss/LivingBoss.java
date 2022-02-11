package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.TypedEnemy;
import com.cavetale.mytems.event.combat.DamageCalculationEvent;
import com.cavetale.mytems.event.combat.DamageFactor;

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
     * Make sure the boss has a reasonable minimum armor and
     * protection enchantments.
     */
    @Override
    public void onDefendingDamageCalculation(DamageCalculationEvent event) {
        event.setIfApplicable(DamageFactor.ARMOR, Math.min(0.2, event.getCalculation().getArmorFactor()));
        event.setIfApplicable(DamageFactor.PROTECTION, Math.min(0.2, event.getCalculation().getProtectionFactor()));
    }
}
