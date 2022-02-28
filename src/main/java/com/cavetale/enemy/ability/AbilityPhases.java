package com.cavetale.enemy.ability;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintain a list of abilities and use them in sequence, looping
 * around.  Each ability lasts as long as its tick method returns
 * true.
 */
public final class AbilityPhases implements Ability {
    List<Ability> abilities = new ArrayList<>();
    int abilityIndex = 0;
    boolean abilityIsNew = true;

    @Override
    public void begin() { }

    /**
     * Despite the contract of Ability, this will return false when
     * the abilities wrap around.
     */
    @Override
    public boolean tick() {
        if (abilities.isEmpty()) return false;
        Ability ability = abilities.get(abilityIndex);
        if (abilityIsNew) {
            ability.begin();
            abilityIsNew = false;
            System.out.println("ABILITY " + ability.getClass().getSimpleName());
        }
        if (!ability.tick()) {
            ability.end();
            abilityIndex += 1;
            abilityIsNew = true;
            if (abilityIndex >= abilities.size()) {
                abilityIndex = 0;
                return false;
            }
        }
        return true;
    }

    @Override
    public void end() {
        if (abilities.isEmpty()) return;
        abilities.get(abilityIndex).end();
    }

    public <T extends Ability> T addAbility(T ability) {
        abilities.add(ability);
        return ability;
    }
}
