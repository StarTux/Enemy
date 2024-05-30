package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.ArrowStormAbility;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.ability.SplashPotionAbility;
import com.cavetale.enemy.util.ItemBuilder;
import com.cavetale.enemy.util.Prep;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Witch;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public final class WickedCroneBoss extends LivingBoss {
    @Getter private final Component displayName = Component.text("Wicked Crone", NamedTextColor.DARK_RED, TextDecoration.BOLD);
    AbilityPhases phases;

    public WickedCroneBoss(final Context context) {
        super(context, EnemyType.WICKED_CRONE);
    }

    @Override
    public void spawn(Location loc) {
        living = loc.getWorld().spawn(loc, Witch.class, this::prep);
        if (living == null) return;
        markLiving();
        if (phases == null) {
            phases = new AbilityPhases();
            DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
            dialogue.addDialogue("Nobody likes unwelcomed guests!");
            dialogue.addDialogue("What a bunch of pests!");
            dialogue.addDialogue("The ritual is nearly complete!");
            SplashPotionAbility splash = phases.addAbility(new SplashPotionAbility(this, context));
            splash.clearPotionItems();
            splash.addPotionItem(new ItemBuilder(Material.SPLASH_POTION).basePotion(PotionType.LONG_SLOWNESS).create());
            splash.addPotionItem(new ItemBuilder(Material.SPLASH_POTION).basePotion(PotionType.STRONG_POISON).create());
            splash.addPotionItem(new ItemBuilder(Material.SPLASH_POTION).basePotion(PotionType.LONG_WEAKNESS).create());
            splash.addPotionItem(new ItemBuilder(Material.SPLASH_POTION).basePotion(PotionType.STRONG_HARMING).create());
            splash.setDuration(200);
            splash.setInterval(4);
            PauseAbility pause = phases.addAbility(new PauseAbility(this, context));
            pause.setDuration(60);
            SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
            adds.add(MagmaCube.class, 9, 3, Prep::add);
            adds.setWarmup(20);
            adds.setDuration(200);
            adds.setInterval(40);
            ArrowStormAbility arrowStorm = phases.addAbility(new ArrowStormAbility(this, context));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0, true, false, true));
            arrowStorm.setDamage(15.0);
            arrowStorm.setDuration(200);
            arrowStorm.setInterval(1);
        }
    }

    private void prep(Witch witch) {
        witch.customName(displayName);
        witch.setCustomNameVisible(true);
        witch.setPersistent(false);
        Prep.health(witch, health, maxHealth);
        Prep.disableEquipmentDrop(witch);
    }

    @Override
    public void tick() {
        if (living == null || !living.isValid()) return;
        phases.tick();
        health = living.getHealth();
    }

    @Override
    public void cleanUp() {
        if (phases != null) {
            phases.end();
        }
    }

    @Override
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }
}
