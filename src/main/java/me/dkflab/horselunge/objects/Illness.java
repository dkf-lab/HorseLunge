package me.dkflab.horselunge.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

public class Illness {

    private PotionEffectType potionEffect;
    private int percentage,multiplier,deathChance,selfCureChance,treatmentTime;
    private boolean preventReccuring,requireIllness;
    private String command,permission,name;

    public Illness(final ConfigurationSection section, final String name) {
        this.name = section.getString("name");
        final String effectName = section.getString("potionEffectType");
        this.potionEffect = PotionEffectType.getByName(effectName);
        this.percentage = section.getInt("percentage");
        this.preventReccuring = section.getBoolean("preventReccuring");
        this.multiplier = section.getInt("multiplier");
        this.command = section.getString("command");
        this.permission = section.getString("permission");
        this.deathChance = section.getInt("deathChance");
        this.selfCureChance = section.getInt("selfCureChance");
        this.requireIllness = section.getBoolean("requireIllness");
        this.treatmentTime = section.getInt("cureTime");
    }

    public String getName() {
        return this.name;
    }

    public PotionEffectType getPotionEffect() {
        return this.potionEffect;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public boolean preventReccurring() {
        return this.preventReccuring;
    }

    public boolean requireIllness() {
        return this.requireIllness;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public String getCommand() {
        return this.command;
    }

    public String getPermission() {
        return this.permission;
    }

    public int getChanceOfDeath() {
        return this.deathChance;
    }

    public int getSelfCureChance() {
        return this.selfCureChance;
    }

    public int getTreatmentTime() {
        return this.treatmentTime;
    }
}
