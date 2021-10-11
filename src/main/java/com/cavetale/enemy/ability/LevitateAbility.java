package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LevitateAbility extends AbstractAbility {
    @Getter @Setter private int interval = 20;
    private int intervalTicks = 0;

    public LevitateAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 100;
        warmup = 0;
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
        PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION, 200, 0);
        for (Player player : context.getPlayers()) {
            player.addPotionEffect(effect);
        }
        return true;
    }
}
