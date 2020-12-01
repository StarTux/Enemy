package com.cavetale.enemy.ability;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.Enemy;
import com.cavetale.enemy.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public final class SplashPotionAbility extends AbstractAbility {
    @Getter @Setter private int interval = 10;
    private int intervalTicks = 0;
    private Random random = new Random();
    private List<ItemStack> potionItems = new ArrayList<>();

    public SplashPotionAbility(final Enemy enemy, final Context context) {
        super(enemy, context);
        duration = 100;
        warmup = 20;
        potionItems.add(new ItemBuilder(Material.SPLASH_POTION).basePotion(PotionType.POISON, false, false).create());
        potionItems.add(new ItemBuilder(Material.SPLASH_POTION).customEffect(PotionEffectType.CONFUSION, 100, 0).create());
    }

    @Override
    public void onBegin() { }

    @Override
    public void onEnd() {
        intervalTicks = 0;
    }

    @Override
    public boolean onTick(int ticks) {
        if (intervalTicks > 0) {
            intervalTicks -= 1;
            return true;
        }
        intervalTicks = interval;
        //
        if (potionItems.isEmpty()) return false;
        Location eye = enemy.getEyeLocation();
        for (Player player : context.getPlayers()) {
            if (!enemy.hasLineOfSight(player)) continue;
            Location loc = player.getEyeLocation();
            Vector vec = loc.subtract(eye).toVector().normalize().multiply(2.0);
            ThrownPotion potion = enemy.launchProjectile(ThrownPotion.class, vec);
            potion.setItem(potionItems.get(random.nextInt(potionItems.size())).clone());
        }
        return true;
    }

    public void clearPotionItems() {
        potionItems.clear();
    }

    public void addPotionItem(ItemStack item) {
        potionItems.add(item);
    }
}
