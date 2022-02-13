package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

/**
 * Shoot fireworks at all visible players.
 * Initially wait for the warmup. After each shot, wait for the
 * interval.
 * Immobilizing.
 */
public final class FireworkAbility extends AbstractAbility {
    @Getter @Setter private int interval = 40;
    @Getter @Setter private int fireworkEffects = 3;
    private int intervalTicks = 0;
    private final Random random = new Random();
    public static final String FIREWORK_ID = "raid:firework";

    public FireworkAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 100;
        warmup = 20;
    }

    @Override
    public void onBegin() {
        enemy.setImmobile(true);
    }

    @Override
    public void onEnd() {
        enemy.setImmobile(false);
        intervalTicks = 0;
    }

    @Override
    public boolean onTick(int ticks) {
        if (intervalTicks > 0) {
            intervalTicks -= 1;
            return true;
        }
        intervalTicks = interval;
        Location eye = enemy.getEyeLocation();
        List<Player> players = new ArrayList<>(context.getPlayers());
        players.removeIf(p -> {
                Location target = p.getEyeLocation();
                if (!eye.getWorld().equals(target.getWorld())) return true;
                if (target.distance(eye) > 64) return true;
                if (!enemy.hasLineOfSight(p)) return true;
                return false;
            });
        if (players.isEmpty()) return true;
        Collections.shuffle(players);
        Player player = players.get(0);
        Location target = player.getEyeLocation();
        Vector vec = target.subtract(eye).toVector().normalize();
        Location from = eye.clone().add(vec);
        Firework firework = enemy.getWorld().spawn(from, Firework.class, fw -> {
                fw.setVelocity(vec.multiply(1.0));
                FireworkMeta meta = fw.getFireworkMeta();
                for (int i = 0; i < fireworkEffects; i += 1) {
                    FireworkEffect.Builder builder = FireworkEffect.builder();
                    switch (random.nextInt(4)) {
                    case 0:
                        builder.with(FireworkEffect.Type.BALL).withColor(Color.BLACK);
                        break;
                    case 1:
                        builder.with(FireworkEffect.Type.BALL).withColor(Color.RED);
                        break;
                    case 2:
                        builder.with(FireworkEffect.Type.BALL).withColor(Color.ORANGE);
                        break;
                    case 3:
                        builder.with(FireworkEffect.Type.BALL).withColor(Color.YELLOW);
                        break;
                    default: break;
                    }
                    meta.addEffect(builder.build());
                }
                fw.setFireworkMeta(meta);
                fw.setPersistent(false);
                fw.setShotAtAngle(true);
            });
        EntityMarker.setId(firework, FIREWORK_ID);
        context.registerTemporaryEntity(firework);
        return true;
    }
}
