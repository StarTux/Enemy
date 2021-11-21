package com.cavetale.enemy;

import com.cavetale.core.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public final class EnemyCommand extends AbstractCommand<EnemyPlugin> {
    protected EnemyCommand(final EnemyPlugin plugin) {
        super(plugin, "enemy");
    }

    protected void onEnable() {
        rootNode.addChild("list").denyTabCompletion()
            .description("List spawned enemies")
            .senderCaller(this::list);
    }

    boolean list(CommandSender sender, String[] args) {
        if (args.length != 0) return false;
        for (Enemy enemy : Enemy.ID_MAP.values()) {
            sender.sendMessage("[" + enemy.getEnemyId() + "] " + enemy.getInfo());
        }
        sender.sendMessage("Total " + Enemy.ID_MAP.size());
        return true;
    }
}
