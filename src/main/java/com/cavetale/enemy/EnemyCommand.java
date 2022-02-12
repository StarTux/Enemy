package com.cavetale.enemy;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class EnemyCommand extends AbstractCommand<EnemyPlugin> {
    protected EnemyCommand(final EnemyPlugin plugin) {
        super(plugin, "enemy");
    }

    protected void onEnable() {
        rootNode.addChild("list").denyTabCompletion()
            .description("List spawned enemies")
            .senderCaller(this::list);
        rootNode.addChild("summon").denyTabCompletion()
            .completers(CommandArgCompleter.enumLowerList(EnemyType.class))
            .playerCaller(this::summon);
        rootNode.addChild("info").denyTabCompletion()
            .playerCaller(this::info);
    }

    boolean list(CommandSender sender, String[] args) {
        if (args.length != 0) return false;
        for (Enemy enemy : Enemy.ID_MAP.values()) {
            sender.sendMessage("[" + enemy.getEnemyId() + "] " + enemy.getInfo());
        }
        sender.sendMessage("Total " + Enemy.ID_MAP.size());
        return true;
    }

    boolean summon(Player player, String[] args) {
        if (args.length != 1) return false;
        EnemyType enemyType = EnemyType.valueOf(args[0].toUpperCase());
        Enemy enemy = enemyType.create(DefaultContext.INSTANCE);
        enemy.setSpawnLocation(player.getLocation());
        enemy.spawn(player.getLocation());
        player.sendMessage("Spawned #" + enemy.getEnemyId());
        return true;
    }

    boolean info(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        int id = Integer.parseInt(args[0]);
        Enemy enemy = Enemy.ofEnemyId(id);
        if (enemy == null) throw new CommandWarn("No such id: " + id);
        sender.sendMessage("#" + enemy.getEnemyId()
                           + " valid=" + enemy.isValid()
                           + " health=" + (int) enemy.getHealth());
        return true;
    }
}
