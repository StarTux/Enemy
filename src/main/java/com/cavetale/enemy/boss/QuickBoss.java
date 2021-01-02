package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.ArrowStormAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.PushAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.util.Prep;
import com.destroystokyo.paper.event.entity.WitchConsumePotionEvent;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class QuickBoss extends LivingEnemy {
    @Getter private double maxHealth = 500;
    @Getter private double health = 500;
    @Getter private final String displayName;
    private AbilityPhases phases;
    private final EntityType bossType;
    private final EntityType addType;

    public QuickBoss(final Context context, final String displayName,
                     final EntityType bossType, final EntityType addType) {
        super(context);
        this.displayName = "" + ChatColor.RED + ChatColor.BOLD + displayName;
        this.bossType = bossType;
        this.addType = addType;
    }

    @Override
    public void spawn(Location location) {
        living = (LivingEntity) location.getWorld().spawn(location, bossType.getEntityClass(), this::prep);
        markLiving();
        phases = new AbilityPhases();
        phases.addAbility(new PushAbility(this, context));
        PauseAbility pause = phases.addAbility(new PauseAbility(this, context, 100));
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        adds.add(addType.getEntityClass(), 8, 1, this::prepAdd);
        phases.addAbility(new HomeAbility(this, context));
        if (bossType == EntityType.STRAY) {
            // Icekelly
            ArrowStormAbility arrowStorm = phases.addAbility(new ArrowStormAbility(this, context));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 0, true, false, true));
            arrowStorm.setDuration(200);
            arrowStorm.setInterval(10);
        }
        phases.begin();
    }

    @Override
    public void onRemove() {
        if (phases != null) {
            phases.end();
        }
    }

    @Override
    public void tick() {
        phases.tick();
        health = living.getHealth();
        if (living instanceof Mob) {
            Mob mob = (Mob) living;
            if (!(mob.getTarget() instanceof Player)) {
                Player target = findTarget();
                if (target != null) mob.setTarget(target);
            }
        }
    }

    private Player findTarget() {
        List<Player> players = context.getPlayers();
        Location eye = getEyeLocation();
        double minVisible = Double.MAX_VALUE;
        double minBlind = Double.MAX_VALUE;
        Player visible = null;
        Player blind = null;
        final double maxVisible = 32 * 32;
        final double maxBlind = 16 * 16;
        for (Player player : players) {
            if (!player.isOnGround()) continue;
            double dist = player.getEyeLocation().distanceSquared(eye);
            if (living == null || living.hasLineOfSight(player)) {
                if (dist < minVisible && dist < maxVisible) {
                    visible = player;
                    minVisible = dist;
                }
            } else {
                if (dist < minBlind && dist < maxBlind) {
                    blind = player;
                    minBlind = dist;
                }
            }
        }
        return visible != null ? visible : null;
    }

    private void prep(Entity entity) {
        LivingEntity living = (LivingEntity) entity;
        living.setCustomName(displayName);
        Prep.health(living, health, maxHealth);
        Prep.boss(living);
    }

    private void prepAdd(Entity entity) {
        LivingEntity living = (LivingEntity) entity;
        Prep.disableEquipmentDrop(living);
    }

    @Override
    public List<ItemStack> getDrops() {
        return Arrays.asList(new ItemStack(Material.DIAMOND));
    }

    @Override
    public void onRandomEvent(Event event) {
        if (event instanceof WitchConsumePotionEvent) {
            ((WitchConsumePotionEvent) event).setCancelled(true);
            return;
        }
    }
}
