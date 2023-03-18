package com.cavetale.enemy.boss;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.EnemyType;
import com.cavetale.enemy.ability.AbilityPhases;
import com.cavetale.enemy.ability.ArrowStormAbility;
import com.cavetale.enemy.ability.FireballAbility;
import com.cavetale.enemy.ability.HomeAbility;
import com.cavetale.enemy.ability.LightningAbility;
import com.cavetale.enemy.ability.PauseAbility;
import com.cavetale.enemy.ability.SpawnAddsAbility;
import com.cavetale.enemy.ability.ThrowAbility;
import com.cavetale.enemy.util.Prep;
import com.destroystokyo.paper.event.entity.WitchConsumePotionEvent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Bee;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.Player;
import org.bukkit.entity.SizedFireball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import static com.cavetale.enemy.EnemyType.*;

public final class QuickBoss extends LivingBoss {
    @Getter private double maxHealth = 500;
    @Getter private double health = 500;
    @Getter private final Component displayName;
    private AbilityPhases phases;
    private final EntityType bossType;
    private final EntityType addType;

    public QuickBoss(final Context context, final EnemyType enemyType,
                     final String displayName,
                     final EntityType bossType, final EntityType addType) {
        super(context, enemyType);
        this.displayName = Component.text(displayName, NamedTextColor.RED, TextDecoration.BOLD);
        this.bossType = bossType;
        this.addType = addType;
    }

    public static QuickBoss skellington(Context ctx) {
        return new QuickBoss(ctx, SKELLINGTON, "Skellington", EntityType.WITHER_SKELETON, EntityType.WITHER_SKELETON);
    }

    public static QuickBoss deepFear(Context ctx) {
        return new QuickBoss(ctx, DEEP_FEAR, "Deep Fear", EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN);
    }

    public static QuickBoss lavaLord(Context ctx) {
        return new QuickBoss(ctx, LAVA_LORD, "Lava Lord", EntityType.MAGMA_CUBE, EntityType.MAGMA_CUBE);
    }

    public static QuickBoss frostwrecker(Context ctx) {
        return new QuickBoss(ctx, FROSTWRECKER, "Frostwrecker", EntityType.DROWNED, EntityType.DROWNED);
    }

    public static QuickBoss iceGolem(Context ctx) {
        return new QuickBoss(ctx, ICE_GOLEM, "Ice Golem", EntityType.SNOWMAN, EntityType.SNOWMAN);
    }

    public static QuickBoss icekelly(Context ctx) {
        return new QuickBoss(ctx, ICEKELLY, "Icekelly", EntityType.STRAY, EntityType.STRAY);
    }

    public static QuickBoss snobear(Context ctx) {
        return new QuickBoss(ctx, SNOBEAR, "Snobear", EntityType.POLAR_BEAR, EntityType.POLAR_BEAR);
    }

    public static QuickBoss queenBee(Context ctx) {
        return new QuickBoss(ctx, QUEEN_BEE, "Queen Bee", EntityType.BEE, EntityType.BEE);
    }

    public static QuickBoss heinousHen(Context ctx) {
        return new QuickBoss(ctx, HEINOUS_HEN, "Heinous Hen", EntityType.CHICKEN, EntityType.BEE);
    }

    public static QuickBoss specter(Context ctx) {
        return new QuickBoss(ctx, SPECTER, "Specter", EntityType.PHANTOM, EntityType.PHANTOM);
    }

    public static QuickBoss enderDragon(Context ctx) {
        return new QuickBoss(ctx, ENDER_DRAGON_BOSS, "Ender Dragon", EntityType.ENDER_DRAGON, EntityType.BLAZE);
    }

    public static QuickBoss ghast(Context ctx) {
        return new QuickBoss(ctx, GHAST_BOSS, "Ur-Ghast", EntityType.GHAST, EntityType.BLAZE);
    }

    public static QuickBoss warden(Context ctx) {
        return new QuickBoss(ctx, WARDEN_BOSS, "Blind Guardian", EntityType.WARDEN, EntityType.ENDERMAN);
    }

    public static QuickBoss piglinBrute(Context ctx) {
        return new QuickBoss(ctx, PIGLIN_BRUTE_BOSS, "Major Pain", EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN);
    }

    @Override
    public void spawn(Location location) {
        living = (LivingEntity) location.getWorld().spawn(location, bossType.getEntityClass(), this::prep);
        markLiving();
        phases = new AbilityPhases();
        phases.addAbility(new ThrowAbility(this, context));
        PauseAbility pause = phases.addAbility(new PauseAbility(this, context, 100));
        SpawnAddsAbility adds = phases.addAbility(new SpawnAddsAbility(this, context));
        if (addType == EntityType.BEE) {
            adds.add(addType.getEntityClass(), 32, 1, this::prepAdd);
        } else {
            adds.add(addType.getEntityClass(), 8, 1, this::prepAdd);
        }
        phases.addAbility(new HomeAbility(this, context));
        if (enemyType == EnemyType.ICEKELLY) {
            ArrowStormAbility arrowStorm = phases.addAbility(new ArrowStormAbility(this, context));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 0, true, false, true));
            arrowStorm.setDamage(15.0);
            arrowStorm.setDuration(200);
            arrowStorm.setInterval(1);
        } else if (enemyType == EnemyType.WARDEN_BOSS) {
            ArrowStormAbility arrowStorm = phases.addAbility(new ArrowStormAbility(this, context));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 300, 0, true, false, true));
            arrowStorm.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 0, true, false, true));
            arrowStorm.setDamage(15.0);
            arrowStorm.setDuration(200);
            arrowStorm.setInterval(1);
        } else if (enemyType == EnemyType.HEINOUS_HEN) {
            LightningAbility lightning = phases.addAbility(new LightningAbility(this, context));
            lightning.setDuration(20 * 60);
        } else if (enemyType == EnemyType.PIGLIN_BRUTE_BOSS) {
            FireballAbility fireballs = phases.addAbility(new FireballAbility(this, context));
            fireballs.setVelocity(3.0);
            fireballs.setLargeChance(0.0);
            fireballs.setDuration(20 * 30);
            fireballs.setInterval(2);
        }
        phases.begin();
    }

    @Override
    public void cleanUp() {
        if (phases != null) {
            phases.end();
        }
    }

    @Override
    public void tick() {
        if (living == null || !living.isValid()) return;
        phases.tick();
        health = living.getHealth();
        if (living instanceof Mob) {
            Mob mob = (Mob) living;
            if (!(mob.getTarget() instanceof Player)) {
                findPlayerTarget();
            }
        }
        if (living instanceof Bee) {
            ((Bee) living).setHasStung(false);
        }
    }

    private void prep(Entity entity) {
        if (entity instanceof Phantom phantom) phantom.setSize(20);
        if (entity instanceof EnderDragon enderDragon) enderDragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
        if (entity instanceof MagmaCube magmaCube) magmaCube.setSize(8);
        if (entity instanceof PiglinAbstract piglin) piglin.setImmuneToZombification(true);
        LivingEntity living = (LivingEntity) entity;
        living.customName(displayName);
        Prep.health(living, health, maxHealth);
        Prep.boss(living);
    }

    private void prepAdd(Entity entity) {
        LivingEntity living = (LivingEntity) entity;
        Prep.disableEquipmentDrop(living);
        if (entity instanceof Bee bee) bee.setAnger(72000);
    }

    @Override
    public void onRandomEvent(Event event) {
        if (event instanceof WitchConsumePotionEvent) {
            ((WitchConsumePotionEvent) event).setCancelled(true);
            return;
        }
    }

    @Override
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        // Protect the ghast boss from its own projectiles
        // When deflected by player, they will appear as shooter!
        if (bossType == EntityType.GHAST && event.getDamager() instanceof SizedFireball) {
            event.setCancelled(true);
            return;
        }
        super.onDamageByEntity(event);
    }
}
