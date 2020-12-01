package com.cavetale.enemy;

import org.bukkit.plugin.java.JavaPlugin;

public final class EnemyPlugin extends JavaPlugin {
    EnemyCommand enemyCommand = new EnemyCommand(this);

    @Override
    public void onEnable() {
        enemyCommand.enable();
    }

    @Override
    public void onDisable() {
    }
}
