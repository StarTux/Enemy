package com.cavetale.enemy.util;

import com.cavetale.worldmarker.util.Tags;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import static com.cavetale.mytems.MytemsPlugin.namespacedKey;

/**
 * Utility class to prepare an entity for battle, quickly.
 */
public final class Prep {
    private Prep() { }

    public static void disableEquipmentDrop(LivingEntity entity) {
        EntityEquipment eq = entity.getEquipment();
        if (eq != null) {
            eq.setHelmetDropChance(0.0f);
            eq.setChestplateDropChance(0.0f);
            eq.setLeggingsDropChance(0.0f);
            eq.setBootsDropChance(0.0f);
            eq.setItemInMainHandDropChance(0.0f);
            eq.setItemInOffHandDropChance(0.0f);
        }
    }

    public static boolean hasAttr(Attributable entity, Attribute attribute) {
        return entity.getAttribute(attribute) != null;
    }

    public static double getAttr(Attributable entity, Attribute attribute) {
        AttributeInstance inst = entity.getAttribute(attribute);
        if (inst == null) return 0;
        return inst.getValue();
    }

    public static void attr(Attributable entity, Attribute attribute, double value) {
        AttributeInstance inst = entity.getAttribute(attribute);
        if (inst == null) {
            entity.registerAttribute(attribute);
            inst = entity.getAttribute(attribute);
            if (inst == null) return;
        }
        inst.setBaseValue(value);
    }

    public static void health(LivingEntity entity, double health, double maxHealth) {
        attr(entity, Attribute.GENERIC_MAX_HEALTH, maxHealth);
        entity.setHealth(Math.min(health, maxHealth));
    }

    public static void health(LivingEntity entity, double health) {
        health(entity, health, health);
    }

    public static void boss(LivingEntity entity) {
        if (entity instanceof Zombie zombie) zombie.setShouldBurnInDay(false);
        if (entity instanceof AbstractSkeleton skeleton) skeleton.setShouldBurnInDay(false);
        attr(entity, Attribute.GENERIC_ATTACK_DAMAGE, 15.0);
        attr(entity, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
        attr(entity, Attribute.GENERIC_ARMOR, 20.0); // diamond or netherite
        attr(entity, Attribute.GENERIC_ARMOR_TOUGHNESS, 12.0); // netherite
        entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(Math.max(2.0, (4.0 / entity.getHeight())));
        entity.setCustomNameVisible(true);
        entity.setPersistent(false);
        entity.setMaximumNoDamageTicks(0);
        disableEquipmentDrop(entity);
        Tags.set(entity.getPersistentDataContainer(), namespacedKey("skillPoints"), 100);
    }

    public static void add(LivingEntity entity) {
        entity.setPersistent(false);
        if (entity instanceof Spider) {
            entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.35);
        } else {
            entity.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(0.75);
        }
        disableEquipmentDrop(entity);
    }

    public static void removeGoals(Mob mob) {
        Bukkit.getMobGoals().removeAllGoals(mob);
    }

    public static void movementSpeed(LivingEntity entity, double speed) {
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
    }
}
