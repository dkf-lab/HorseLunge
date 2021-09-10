package me.dkflab.horselunge.managers;

import me.dkflab.horselunge.HorseLunge;
import me.dkflab.horselunge.objects.Illness;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static me.dkflab.horselunge.Utils.randomNumber;
import static me.dkflab.horselunge.Utils.sendMessage;

public class IllnessManager {

    // Manage vaccines and illnesses

    private final HashMap<Entity, List<Illness>> sickMap = new HashMap<>();
    private final HashMap<Entity, List<Illness>> vaccineMap = new HashMap<>();
    public final Map<String, Illness> illnessMap = new HashMap<>();
    private HorseLunge main;
    public IllnessManager(HorseLunge p){
        main = p;
    }

    public void onEnable() {
        if (main.data.getConfig().getConfigurationSection("illnesses") == null) {
            main.console("&cIllnesses configuration section not detected.");
            return;
        }

        Set<String> illnesses = main.data.getConfig().getConfigurationSection("illnesses").getKeys(false);
        System.out.println(" ");
        main.console("&eIf there are any config errors, they MUST be resolved before the plugin will work correctly.");
        for (String n : illnesses) {
            main.console("&a" + n + " detected!");

            String name = main.data.getConfig().getString("illnesses." + n + ".name");
            if (name == null) {
                main.console("&cCould not detect name for " + n + "!");
                return;
            } else {
                main.console("&aDetected name for " + n + " as " + name);
            }

            int percentage = main.data.getConfig().getInt("illnesses." + n + ".percentage");
            main.console("&aDetected percentage for " + n + " as " + percentage);

            boolean preventReccuring = main.data.getConfig().getBoolean("illnesses." + n + ".preventReccuring");
            main.console("&aDetected preventReccuring for " + n + " as " + preventReccuring);

            String s = main.data.getConfig().getString("illnesses." + n + ".potionEffectType");
            if (s != null) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(s);
                if (potionEffectType == null) {
                    main.console("&cCould not detect potionEffectType for " + n + "!");
                    return;
                }
                main.console("&aDetected potion as " + potionEffectType.getName() + " for " + n + "!");
            } else {
                main.console("&cCould not detect potionEffectType for " + n + "!");
                return;
            }

            int multiplier = main.data.getConfig().getInt("illnesses." + n + ".multiplier");
            main.console("&aDetected multiplier for " + n + " as " + multiplier);

            String command = main.data.getConfig().getString("illnesses." + n + ".command");
            main.console("&aDetected command for " + n + " as " + command);

            String permission = main.data.getConfig().getString("illnesses." + n + ".permission");
            main.console("&aDetected permission for " + n + " as " + permission);

            System.out.println(" ");
            // Code above just checks if anything is null
            // loadFrom does the actual loading
            loadFrom(main.data.getConfig());
        }
    }

    public void giveHorseIllness(Entity horse, Illness i) {
        // Check for vaccine
        if (checkVaccineStatus(horse, i)) {
            return;
        }
        if (sickMap.get(horse) == null) {
            sickMap.put(horse, Collections.singletonList(i));
            return;
        }
        // multiple illnesses
        List<Illness> list = sickMap.get(horse);
        list.add(i);
        sickMap.put(horse, list);
        // Does potion effects
        giveIllnessEffects(horse);
    }

    public void illnessRoll(Entity h, Player p) {
        // Only method called when getting on a horse.
        for (Illness i : illnessMap.values()) {
            int chance = randomNumber(0, 100);
            main.console("[DEBUG] Percentage: " + i.getPercentage() + ". Roll: " + chance);
            if (i.getPercentage() > chance) {
                if (!checkVaccineStatus(h, i)) {
                    sendMessage(p, "&7Oh no! Your horse has caught &c"  + i.getName() + "&7!");
                    giveHorseIllness(h, i);
                }
            }
        }
    }

    public void vaccine(Player p, Entity horse, String args) {
        Illness illness = null;
        for (Illness i : illnessMap.values()) {
            if (i.getCommand().equalsIgnoreCase(args)) {
                if (!p.hasPermission(i.getPermission())) {
                    sendMessage(p, "&cInsufficient permissions.");
                    return;
                }
                illness = i;
            }
        }
        if (illness == null) {
            // Illness doesn't exist.
            sendMessage(p, "&7Check your spelling of &c" + args + "&7.");
            return;
        }
        // Illness is initialised.
        if (vaccineMap.get(horse) == null) {
            vaccineMap.put(horse, Collections.singletonList(illness));
            sendMessage(p, "&aVaccine successful against &e" + illness.getName() + "&a!");
            return;
        }
        List<Illness> list = vaccineMap.get(horse);
        // Check if horse is already vaccinated
        if (list.contains(illness)) {
            sendMessage(p, "&7Your horse is already vaccinated against &e" + illness.getName() + "&7!");
            return;
        }
        // Vaccinate horse
        list.add(illness);
        vaccineMap.put(horse, list);
        sendMessage(p, "&aVaccine successful against &e" + illness.getName() + "&a!");
    }

    public Boolean checkVaccineStatus(Entity horse, Illness illness) {
        // Check vaccine status of horse
        if (vaccineMap.get(horse) == null) {
            return false;
        }
        List<Illness> list = vaccineMap.get(horse);
        for (Illness i : list) {
            if (illness == i) {
                if (i.preventReccurring()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeIllnessEffects(Entity horse) {
        LivingEntity e =(LivingEntity)horse;
        for (PotionEffect effect : e.getActivePotionEffects()) {
            e.removePotionEffect(effect.getType());
        }
    }

    public void giveIllnessEffects(Entity horse) {
        removeIllnessEffects(horse);
        for (Illness i : illnessMap.values()) {
            if (hasIllness(horse, i)) {
                LivingEntity le = (LivingEntity)horse;
                le.addPotionEffect(new PotionEffect(i.getPotionEffect(), Integer.MAX_VALUE, i.getMultiplier()));
            }
        }
    }

    public Boolean hasIllness(Entity horse, Illness i) {
        if (sickMap.get(horse) == null) {
            return false;
        }
        List<Illness> list = sickMap.get(horse);
        for (Illness il : list) {
            if (i == il) {
                return true;
            }
        }
        return false;
    }

    public Illness getIllnessFromName(String name) {
        return this.illnessMap.get(name);
    }

    public List<Illness> getHorseIllnesses(Entity horse) {
        return sickMap.get(horse);
    }

    public void loadFrom(final FileConfiguration config) {
        final ConfigurationSection illnessGroupSection = config.getConfigurationSection("illnesses");
        for (final String illnessName: illnessGroupSection.getKeys(false)) {
            final ConfigurationSection illnessSection = config.getConfigurationSection("illnesses." + illnessName);
            if (illnessSection == null) {
                main.console("&cCould not detect settings for " + illnessName + "!");
                return;
            }
            final Illness illness = new Illness(illnessSection, illnessName);
            this.illnessMap.put(illnessName, illness);
        }
    }
}
