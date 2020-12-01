package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;

/**
 * Do nothing for a while.
 */
public final class PauseAbility extends AbstractAbility {
    public PauseAbility(final Enemy enemy, final Context context, final int duration) {
        super(enemy, context);
        this.duration = duration;
    }

    public PauseAbility(final Enemy enemy, final Context context) {
        this(enemy, context, 100);
    }

    @Override
    public void onBegin() { }

    @Override
    public void onEnd() { }

    @Override
    public boolean onTick(int ticks) {
        return true;
    }
}
