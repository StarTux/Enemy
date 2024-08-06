package com.cavetale.enemy;

public interface TypedEnemy {
    EnemyType getEnemyType();

    default boolean isBoss() {
        return getEnemyType() != null
            && getEnemyType().getCategory() == EnemyCategory.BOSS;
    }
}
