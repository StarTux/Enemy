package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.FireworkAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.PushAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.util.Prep;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Wither;

public final class VengefulBoss extends LivingEnemy {
    @Getter private double maxHealth = 500;
    @Getter private double health = 500;
    @Getter private final String displayName = "" + ChatColor.DARK_GRAY + ChatColor.BOLD + "The Vengeful";
    AbilityPhases phases = new AbilityPhases();

    public VengefulBoss(final Context context) {
        super(context);
    }

    @Override
    public void spawn(Location location) {
        living = location.getWorld().spawn(location, Wither.class, this::prep);
        markLiving();
        // Abilities
        phases = new AbilityPhases();
        DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
        dialogue.addDialogue("Like the crops in the field, you too shall wither away.");
        dialogue.addDialogue("Your time has come.");
        dialogue.addDialogue("Death draws nearer with every moment.");
        phases.addAbility(new PushAbility(this, context));
        phases.addAbility(new PauseAbility(this, context));
        phases.addAbility(new HomeAbility(this, context));
        FireworkAbility firework = phases.addAbility(new FireworkAbility(this, context));
        firework.setDuration(200);
        firework.setWarmup(0);
        firework.setInterval(10);
        firework.setFireworkEffects(1);
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        adds.setWarmup(20);
        adds.setDuration(40);
        adds.setInterval(20);
        adds.add(Ghast.class, 3, 1, this::prepAdd);
        phases.begin();
    }

    @Override
    public void tick() {
        phases.tick();
        health = living.getHealth();
    }

    @Override
    public void onRemove() {
        if (phases != null) {
            phases.end();
        }
    }

    private void prep(Wither wither) {
        Prep.health(wither, health, maxHealth);
        wither.setCustomName(displayName);
        Prep.boss(wither);
        wither.getBossBar().setVisible(false);
    }

    private void prepAdd(Ghast ghast) {
        Prep.add(ghast);
        Prep.health(ghast, 40);
        Prep.attr(ghast, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
        Prep.attr(ghast, Attribute.GENERIC_ARMOR, 20.0); // dia=20
        Prep.attr(ghast, Attribute.GENERIC_ARMOR_TOUGHNESS, 8.0); // dia=8
    }
}
