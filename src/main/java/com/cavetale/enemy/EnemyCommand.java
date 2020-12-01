package com.cavetale.enemy;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

@RequiredArgsConstructor
public final class EnemyCommand implements TabExecutor {
    private final EnemyPlugin plugin;

    public void enable() {
        plugin.getCommand("enemy").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return null;
    }
}
