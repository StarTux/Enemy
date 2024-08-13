package com.cavetale.enemy.custom;

import com.cavetale.enemy.Context;
import com.cavetale.enemy.LivingEnemy;
import com.cavetale.enemy.ability.HealEnemyAbility;
import com.cavetale.enemy.util.Prep;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.event.entity.EntitySpellCastEvent;

public final class HealEvoker extends LivingEnemy {
    HealEnemyAbility healAbility;

    public HealEvoker(final Context context) {
        super(context, EntityType.EVOKER);
    }

    @Override
    public void spawn(Location location) {
        living = location.getWorld().spawn(location, Evoker.class, this::prep);
        markLiving();
        healAbility = new HealEnemyAbility(this, context);
        healAbility.setDuration(9999);
        healAbility.setWarmup(20);
        healAbility.setInterval(40);
        healAbility.setHealAmount(10);
        healAbility.begin();
    }

    @Override
    public void tick() {
        if (!healAbility.tick()) {
            healAbility.end();
            healAbility.begin();
        }
    }

    @Override
    public void cleanUp() {
        healAbility.end();
    }

    private void prep(Evoker evoker) {
        evoker.setPersistent(false);
        //Prep.removeGoals(evoker);
        Prep.disableEquipmentDrop(evoker);
    }

    @Override
    protected void onSpellCast(EntitySpellCastEvent event) {
        event.setCancelled(true);
    }
}
