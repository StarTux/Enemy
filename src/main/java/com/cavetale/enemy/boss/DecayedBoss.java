package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.FireworkAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.util.ItemBuilder;
import com.cavetale.enemy.util.Prep;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.inventory.EntityEquipment;

public final class DecayedBoss extends LivingEnemy {
    @Getter private double maxHealth = 500;
    @Getter private double health = 500;
    @Getter private final Component displayName = Component.text("The Decayed", NamedTextColor.DARK_RED, TextDecoration.BOLD);
    private AbilityPhases phases;

    public DecayedBoss(final Context context) {
        super(context);
    }

    @Override
    public void spawn(Location location) {
        living = location.getWorld().spawn(location, WitherSkeleton.class, this::prep);
        markLiving();
        phases = new AbilityPhases();
        DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
        dialogue.addDialogue("Your effort is in vain.");
        dialogue.addDialogue("The show's over with my next move.");
        dialogue.addDialogue("Welcome home.");
        FireworkAbility firework = phases.addAbility(new FireworkAbility(this, context));
        firework.setDuration(160);
        firework.setInterval(40);
        PauseAbility pause = phases.addAbility(new PauseAbility(this, context, 100));
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        adds.add(WitherSkeleton.class, 8, 1, this::prepAdd);
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
    }

    private void prep(LivingEntity entity) {
        EntityEquipment eq = entity.getEquipment();
        eq.setHelmet(new ItemBuilder(Material.CARVED_PUMPKIN).create());
        eq.setItemInMainHand(new ItemBuilder(Material.DIAMOND_SWORD)
                             .ench(Enchantment.KNOCKBACK, 2)
                             .ench(Enchantment.DAMAGE_ALL, 5)
                             .removeDamage().create());
        entity.customName(displayName);
        Prep.health(entity, health, maxHealth);
        Prep.boss(entity);
    }

    private void prepAdd(WitherSkeleton witherSkeleton) {
        Prep.disableEquipmentDrop(witherSkeleton);
        witherSkeleton.setPersistent(false);
        witherSkeleton.setRemoveWhenFarAway(true);
    }
}
