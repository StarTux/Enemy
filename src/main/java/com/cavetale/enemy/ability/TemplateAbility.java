package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import lombok.Getter;
import lombok.Setter;

public final class TemplateAbility extends AbstractAbility {
    @Getter @Setter private int interval = 40;
    private int intervalTicks = 0;

    public TemplateAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 100;
    }

    @Override
    public void onBegin() { }

    @Override
    public void onEnd() { }

    @Override
    public boolean onTick(int ticks) {
        if (intervalTicks > 0) {
            intervalTicks -= 1;
            return true;
        }
        intervalTicks = interval;
        //
        return true;
    }
}
