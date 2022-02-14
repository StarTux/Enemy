package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.PullAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.ability.SplashPotionAbility;
import com.cavetale.enemy.util.Prep;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Vex;
import org.bukkit.inventory.EntityEquipment;

public final class ForgottenBoss extends LivingBoss {
    @Getter private double maxHealth = 500;
    @Getter private double health = 500;
    @Getter private final Component displayName = Component.text("The Forgotten", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD);
    AbilityPhases phases = new AbilityPhases();

    public ForgottenBoss(final Context context) {
        super(context, EnemyType.FORGOTTEN);
    }

    @Override
    public void spawn(Location location) {
        living = location.getWorld().spawn(location, Evoker.class, this::prep);
        markLiving();
        // Abilities
        phases = new AbilityPhases();
        DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
        dialogue.addDialogue("The journey ends here.");
        dialogue.addDialogue("My powers are beyond your understanding.");
        dialogue.addDialogue("You were foolish to come here.");
        PullAbility pull = phases.addAbility(new PullAbility(this, context));
        pull.setDuration(150);
        pull.setInterval(40);
        pull.setWarmup(10);
        SplashPotionAbility splash = phases.addAbility(new SplashPotionAbility(this, context));
        splash.setDuration(200);
        splash.setWarmup(10);
        splash.setInterval(5);
        phases.addAbility(new PauseAbility(this, context, 100));
        phases.addAbility(new HomeAbility(this, context));
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        adds.add(Vex.class, 32, 2, this::prepAdd);
        adds.setInterval(20);
        adds.setDuration(200);
        phases.begin();
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

    private void prep(Evoker entity) {
        Prep.health(entity, health, maxHealth);
        entity.customName(displayName);
        Prep.boss(entity);
    }

    private void prepAdd(Vex vex) {
        EntityEquipment eq = vex.getEquipment();
        eq.clear();
        Prep.health(vex, 4);
        Prep.add(vex);
    }
}
