package com.cavetale.enemy;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * Wrap a newly spawned entity.
 */
public final class LivingEnemyWrapper extends LivingEnemy {
    public LivingEnemyWrapper(final Context context, final LivingEntity entity) {
        super(context);
        this.living = entity;
        markLiving();
    }

    @Override
    public void spawn(Location location) { }

    @Override
    protected void cleanUp() { }

    @Override
    public void tick() { }
}
