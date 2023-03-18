package com.cavetale.enemy;

import com.cavetale.enemy.boss.DecayedBoss;
import com.cavetale.enemy.boss.ForgottenBoss;
import com.cavetale.enemy.boss.InfernalPhantasmBoss;
import com.cavetale.enemy.boss.QuickBoss;
import com.cavetale.enemy.boss.SadisticVampireBoss;
import com.cavetale.enemy.boss.VengefulBoss;
import com.cavetale.enemy.boss.WickedCroneBoss;
import com.cavetale.enemy.custom.HealEvoker;
import com.cavetale.enemy.custom.VampireBat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import static com.cavetale.enemy.EnemyCategory.*;

/**
 * Compile time list of all known enemies. Partially unimplemented
 * because of legacy stuff (see missing ctor). Might become obsolete
 * in the future.
 */
@RequiredArgsConstructor
public enum EnemyType {
    // Halloween 2019
    DECAYED(BOSS, DecayedBoss.class, DecayedBoss::new),
    FORGOTTEN(BOSS, ForgottenBoss.class, ForgottenBoss::new),
    VENGEFUL(BOSS, VengefulBoss.class, VengefulBoss::new),
    // Halloween 2018 (Legacy)
    SKELLINGTON(BOSS, QuickBoss.class, QuickBoss::skellington),
    DEEP_FEAR(BOSS, QuickBoss.class, QuickBoss::deepFear),
    LAVA_LORD(BOSS, QuickBoss.class, QuickBoss::lavaLord),
    // Christmas 2019
    FROSTWRECKER(BOSS, QuickBoss.class, QuickBoss::frostwrecker),
    ICE_GOLEM(BOSS, QuickBoss.class, QuickBoss::iceGolem),
    ICEKELLY(BOSS, QuickBoss.class, QuickBoss::icekelly),
    SNOBEAR(BOSS, QuickBoss.class, QuickBoss::snobear),
    QUEEN_BEE(BOSS, QuickBoss.class, QuickBoss::queenBee),
    // Easter 2020
    HEINOUS_HEN(BOSS, QuickBoss.class, QuickBoss::heinousHen),
    SPECTER(BOSS, QuickBoss.class, QuickBoss::specter),
    // Halloween 2020
    VAMPIRE_BAT(LIVING, VampireBat.class, VampireBat::new),
    HEAL_EVOKER(LIVING, HealEvoker.class, HealEvoker::new),
    // Bosses
    SADISTIC_VAMPIRE(BOSS, SadisticVampireBoss.class, SadisticVampireBoss::new),
    WICKED_CRONE(BOSS, WickedCroneBoss.class, WickedCroneBoss::new),
    INFERNAL_PHANTASM(BOSS, InfernalPhantasmBoss.class, InfernalPhantasmBoss::new),
    // 2021
    ENDER_DRAGON_BOSS(BOSS, QuickBoss.class, QuickBoss::enderDragon),
    GHAST_BOSS(BOSS, QuickBoss.class, QuickBoss::ghast),
    // 2023
    WARDEN_BOSS(BOSS, QuickBoss.class, QuickBoss::warden),
    ;

    public final EnemyCategory category;
    public final Class<? extends Enemy> type;
    private final Function<Context, Enemy> ctor;
    private static final Map<Class<? extends Enemy>, EnemyType> TYPEMAP = new HashMap<>();

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
