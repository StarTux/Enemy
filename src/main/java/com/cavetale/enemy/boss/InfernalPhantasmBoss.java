package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.FireballAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.util.Prep;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Zoglin;

public final class InfernalPhantasmBoss extends LivingBoss {
    @Getter private final Component displayName = Component.text("Infernal Phantasm", NamedTextColor.DARK_RED, TextDecoration.BOLD);
    AbilityPhases phases;

    public InfernalPhantasmBoss(final Context context) {
        super(context, EnemyType.INFERNAL_PHANTASM);
    }

    public void spawn(Location loc) {
        living = loc.getWorld().spawn(loc, Blaze.class, this::prep);
        if (living == null) return;
        markLiving();
        if (phases == null) {
            phases = new AbilityPhases();
            DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
            dialogue.addDialogue("Did you enjoy this Halloween night?");
            dialogue.addDialogue("Say farewell to this world!");
            dialogue.addDialogue("You will never escape from me!");
            dialogue.setDuration(100);
            FireballAbility fireballs = phases.addAbility(new FireballAbility(this, context));
            fireballs.setVelocity(2.5);
            fireballs.setLargeChance(0.5);
            fireballs.setDuration(200);
            fireballs.setInterval(10);
            phases.addAbility(new PauseAbility(this, context)).setDuration(60);
            phases.addAbility(new HomeAbility(this, context));
            SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
            adds.setDuration(100);
            adds.setWarmup(20);
            adds.setInterval(20);
            adds.add(Zoglin.class, 8, 1, Prep::add);
            adds.add(PiglinBrute.class, 8, 1, this::prepPiglinBrute);
            adds.setSimultaneous(2);
        }
    }

    private void prep(Blaze blaze) {
        blaze.customName(displayName);
        blaze.setCustomNameVisible(true);
        Prep.health(blaze, health, maxHealth);
        Prep.boss(blaze);
    }

    private void prepPiglinBrute(PiglinBrute brute) {
        brute.setBaby();
        brute.setImmuneToZombification(true);
        Prep.add(brute);
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
}
