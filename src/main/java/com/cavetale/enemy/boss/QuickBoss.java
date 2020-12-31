package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.DialogueAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.PushAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.util.Prep;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

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
        DialogueAbility dialogue = phases.addAbility(new DialogueAbility(this, context));
        dialogue.addDialogue("You just wait!");
        dialogue.addDialogue("No escape!");
        dialogue.addDialogue("No soup for you.");
        phases.addAbility(new PushAbility(this, context));
        PauseAbility pause = phases.addAbility(new PauseAbility(this, context, 100));
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        adds.add(addType.getEntityClass(), 8, 1, this::prepAdd);
        phases.addAbility(new HomeAbility(this, context));
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
}
