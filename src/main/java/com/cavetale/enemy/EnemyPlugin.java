package com.cavetale.enemy;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnemyPlugin extends JavaPlugin {
    @Getter private static EnemyPlugin instance;
    EnemyCommand enemyCommand = new EnemyCommand(this);
    EnemyListener enemyListener = new EnemyListener(this);
    private Map<Integer, EnemyHandle> idHandleMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        enemyCommand.enable();
        enemyListener.enable();
    }

    @Override
    public void onDisable() {
    }

    public static void setHandle(Entity entity, EnemyHandle handle) {
        instance.idHandleMap.put(entity.getEntityId(), handle);
    }

    public static EnemyHandle removeHandle(Entity entity) {
        return instance.idHandleMap.remove(entity.getEntityId());
    }

    public static EnemyHandle getHandle(Entity entity) {
        return instance.idHandleMap.get(entity.getEntityId());
    }
}
