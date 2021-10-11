package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public final class DialogueAbility extends AbstractAbility {
    private int dialogueIndex = 0;
    private List<String> dialogues = new ArrayList<>(4);

    public DialogueAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
    }

    public void addDialogue(String line) {
        dialogues.add(line);
    }

    @Override
    public void onBegin() { }

    @Override
    public void onEnd() { }

    @Override
    public boolean onTick(int ticks) {
        if (ticks != warmup) return true;
        if (dialogues.isEmpty()) return true;
        String line = dialogues.get(dialogueIndex);
        Component message = Component.text()
            .append(enemy.getDisplayName())
            .append(Component.text(": ", NamedTextColor.GRAY))
            .append(Component.text(line, NamedTextColor.RED, TextDecoration.ITALIC))
            .build();
        dialogueIndex += 1;
        if (dialogueIndex >= dialogues.size()) dialogueIndex = 0;
        for (Player player : context.getPlayers()) {
            player.sendMessage(message);
            player.sendActionBar(message);
        }
        return true;
    }
}
