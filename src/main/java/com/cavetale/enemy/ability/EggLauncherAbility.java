package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import com.cavetale.worldmarker.entity.EntityMarker;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class EggLauncherAbility extends AbstractAbility {
    public static final String EXPLOSIVE_EGG_ID = "raid:explosive_egg";
    @Getter @Setter private int interval = 0;
    private int intervalTicks = 0;
    private final Random random = new Random();

    public EggLauncherAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 200;
        warmup = 20;
    }

    @Override
    public void onBegin() {
        enemy.setInvulnerable(true);
        enemy.setImmobile(true);
        enemy.getWorld().spawnParticle(Particle.FLASH, enemy.getEyeLocation(), 10, 0.0, 0.0, 0.0, 0.0);
        enemy.getWorld().playSound(enemy.getEyeLocation(), Sound.ENTITY_CHICKEN_HURT, SoundCategory.HOSTILE, 2f, 0.5f);
    }

    @Override
    public void onEnd() {
        enemy.setInvulnerable(false);
        enemy.setImmobile(false);
        if (enemy.isValid()) {
            enemy.getWorld().spawnParticle(Particle.SMOKE, enemy.getEyeLocation(), 5, 0.5, 0.5, 0.5, 0.0);
            enemy.getWorld().playSound(enemy.getEyeLocation(), Sound.BLOCK_FIRE_EXTINGUISH, SoundCategory.HOSTILE, 2f, 1f);
        }
    }

    private double rnd() {
        return random.nextBoolean()
            ? random.nextDouble()
            : -random.nextDouble();
    }

    @Override
    public boolean onTick(int ticks) {
        if (intervalTicks > 0) {
            intervalTicks -= 1;
            return true;
        }
        intervalTicks = interval;
        //
        List<Player> players = new ArrayList<>(context.getPlayers(enemy));
        players.removeIf(p -> !enemy.hasLineOfSight(p));
        if (players.isEmpty()) return true;
        Player target = players.get(random.nextInt(players.size()));
        Vector velo = target.getEyeLocation()
            .subtract(enemy.getEyeLocation())
            .toVector().normalize()
            .add(new Vector(rnd() * 0.05,
                            random.nextDouble() * 0.05,
                            rnd() * 0.05))
            .multiply(2.0);
        Egg egg = (Egg) enemy.launchProjectile(Egg.class, velo);
        EntityMarker.setId(egg, EXPLOSIVE_EGG_ID);
        enemy.getWorld().playSound(enemy.getEyeLocation(), Sound.ENTITY_EGG_THROW, SoundCategory.HOSTILE, 1f, 1f);
        return true;
    }
}
