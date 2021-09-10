package me.dkflab.horselunge.objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

public class Illness {

    private PotionEffectType potionEffect;
    private int percentage,multiplier;
    private boolean preventReccuring;
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

    public int getMultiplier() {
        return this.multiplier;
    }

    public String getCommand() {
        return this.command;
    }

    public String getPermission() {
        return this.permission;
    }
}
