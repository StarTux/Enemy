package com.cavetale.enemy;

import java.util.List;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class DefaultContext implements Context {
    public static final DefaultContext INSTANCE = new DefaultContext();

    @Override
    public JavaPlugin getPlugin() {
        return EnemyPlugin.instance;
    }

    @Override
    public List<Player> getPlayers() {
        return List.of();
    }

    @Override
    public void registerNewEnemy(Enemy enemy) { }

    @Override
    public void registerTemporaryEntity(Entity entity) { }

    @Override
    public boolean isTemporaryEntity(Entity entity) {
        return false;
    }

    @Override
    public int countTemporaryEntities(Class<? extends Entity> type) {
        return 0;
    }

    @Override
    public void onDeath(Enemy enemy) { }

    @Override
    public List<Enemy> getEnemies() {
        return List.of();
    }
}
