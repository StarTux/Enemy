package com.cavetale.enemy;

import com.cavetale.enemy.boss.*;
import com.cavetale.enemy.custom.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.entity.EntityType;

/**
 * Compile time list of all known enemies. Partially unimplemented
 * because of legacy stuff (see missing ctor). Might become obsolete
 * in the future.
 */
public enum EnemyType {
    // Halloween 2019
    DECAYED(DecayedBoss.class, DecayedBoss::new),
    FORGOTTEN(ForgottenBoss.class, ForgottenBoss::new),
    VENGEFUL(VengefulBoss.class, VengefulBoss::new),
    // Halloween 2018 (Legacy)
    SKELLINGTON(QuickBoss.class, ctx -> new QuickBoss(ctx, "Skellington", EntityType.WITHER_SKELETON, EntityType.WITHER_SKELETON)),
    DEEP_FEAR(QuickBoss.class, ctx -> new QuickBoss(ctx, "Deep Fear", EntityType.ELDER_GUARDIAN, EntityType.GUARDIAN)),
    LAVA_LORD(QuickBoss.class, ctx -> new QuickBoss(ctx, "Lava Lord", EntityType.MAGMA_CUBE, EntityType.MAGMA_CUBE)),
    // Christmas 2019
    FROSTWRECKER(QuickBoss.class, ctx -> new QuickBoss(ctx, "Frostwrecker", EntityType.DROWNED, EntityType.DROWNED)),
    ICE_GOLEM(QuickBoss.class, ctx -> new QuickBoss(ctx, "Ice Golem", EntityType.SNOWMAN, EntityType.SNOWMAN)),
    ICEKELLY(QuickBoss.class, ctx -> new QuickBoss(ctx, "Icekelly", EntityType.STRAY, EntityType.STRAY)),
    SNOBEAR(QuickBoss.class, ctx -> new QuickBoss(ctx, "Snobear", EntityType.POLAR_BEAR, EntityType.POLAR_BEAR)),
    QUEEN_BEE(QuickBoss.class, ctx -> new QuickBoss(ctx, "Queen Bee", EntityType.BEE, EntityType.BEE)),
    // Easter 2020
    HEINOUS_HEN(QuickBoss.class, ctx -> new QuickBoss(ctx, "Heinous Hen", EntityType.CHICKEN, EntityType.ZOMBIE)),
    SPECTER(QuickBoss.class, ctx -> new QuickBoss(ctx, "Specter", EntityType.PHANTOM, EntityType.PHANTOM)),
    // Halloween 2020
    VAMPIRE_BAT(VampireBat.class, VampireBat::new),
    HEAL_EVOKER(HealEvoker.class, HealEvoker::new),
    // Bosses
    SADISTIC_VAMPIRE(SadisticVampireBoss.class, SadisticVampireBoss::new),
    WICKED_CRONE(WickedCroneBoss.class, WickedCroneBoss::new),
    INFERNAL_PHANTASM(InfernalPhantasmBoss.class, InfernalPhantasmBoss::new),
    // 2021
    ENDER_DRAGON(QuickBoss.class, ctx -> new QuickBoss(ctx, "Ender Dragon", EntityType.ENDER_DRAGON, EntityType.BLAZE)),
    GHAST_BOSS(QuickBoss.class, ctx -> new QuickBoss(ctx, "GHAST", EntityType.GHAST, EntityType.BLAZE)),
    ;
    //

    public final Class<? extends Enemy> type;
    private final Function<Context, Enemy> ctor;
    private static final Map<Class<? extends Enemy>, EnemyType> TYPEMAP = new HashMap<>();

    EnemyType(final Class<? extends Enemy> type, final Function<Context, Enemy> ctor) {
        this.type = type;
        this.ctor = ctor;
    }

    static {
        for (EnemyType enemyType : EnemyType.values()) {
            TYPEMAP.put(enemyType.type, enemyType);
        }
    }

    public Enemy create(Context context) {
        return ctor.apply(context);
    }

    public static EnemyType of(Enemy enemy) {
        return TYPEMAP.get(enemy.getClass());
    }
}
