package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import org.bukkit.Location;

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
        Location loc = enemy.getSpawnLocation();
        boolean doIt = false;
        if (loc != null) {
            if (System.currentTimeMillis() - enemy.getLastDamage() > 10_000L) {
                doIt = true;
            } else if (enemy.getLocation().getBlock().isLiquid()) {
                doIt = true;
            }
        }
        done = true;
        if (doIt) {
            enemy.teleport(loc);
        }
        return true;
    }
}
