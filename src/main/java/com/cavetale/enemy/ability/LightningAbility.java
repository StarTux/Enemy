package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

public final class LightningAbility extends AbstractAbility {
    @Getter @Setter private int interval = 40;
    private int intervalTicks = 0; // count down
    private int lightningTicks = 0; //count up
    private UUID lightningTarget;
    private List<Location> lightningSpots = new ArrayList<>();
    private final Random random = new Random();
    public static final String LIGHTNING_ID = "enemy:lightning";

    public LightningAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 400;
    }

    @Override
    public void onBegin() {
        lightningTarget = null;
        enemy.getWorld().spawnParticle(Particle.FLASH, enemy.getEyeLocation(), 5, 0.5, 0.5, 0.5, 0.0);
        lightningSpots.clear();
    }

    @Override
    public void onEnd() {
    }

    @Override
    public boolean onTick(int ticks) {
        if (intervalTicks > 0) {
            intervalTicks -= 1;
            if (intervalTicks == 0) lightningTicks = 0;
            return true;
        }
        //
        final int stateTicks = lightningTicks++;
        if (stateTicks == 10) {
            findLightningSpot(context.getPlayers(enemy));
        } else if (stateTicks == 30) {
            for (Location location : lightningSpots) {
                enemy.getWorld().spawn(location, LightningStrike.class, lightning -> {
                        EntityMarker.setId(lightning, LIGHTNING_ID);
                        lightning.setLifeTicks(20 * 2);
                    });
            }
            intervalTicks = interval;
        }
        return true;
    }

    private void findLightningSpot(List<Player> players) {
        lightningSpots.clear();
        Player target;
        if (lightningTarget == null) {
            target = players
                .get(random.nextInt(players.size()));
            lightningTarget = target.getUniqueId();
        } else {
            target = null;
            for (Player p : players) {
                if (p.getUniqueId().equals(lightningTarget)) {
                    target = p;
                }
            }
            if (target == null) {
                lightningTarget = null;
                return;
            }
        }
        target.getWorld().spawnParticle(Particle.END_ROD, target.getEyeLocation(),
                                     6, 0.2, 0.2, 0.2, 0);
        lightningSpots.add(target.getLocation());
    }
}
