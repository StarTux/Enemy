package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;

public final class HomeAbility extends AbstractAbility {
    boolean done = false;

    public HomeAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 100;
    }

    @Override
    public void onBegin() {
        done = false;
    }

    @Override
    public void onEnd() { }

    @Override
    public boolean onTick(int ticks) {
        enemy.teleport(enemy.getSpawnLocation());
        done = true;
        return true;
    }
}
