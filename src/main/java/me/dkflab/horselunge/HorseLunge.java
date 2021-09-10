package me.dkflab.horselunge;

import me.dkflab.horselunge.commands.CommandListener;
import me.dkflab.horselunge.listeners.Interact;
import me.dkflab.horselunge.managers.DataManager;
import me.dkflab.horselunge.managers.FoodWaterManager;
import me.dkflab.horselunge.managers.IllnessManager;
import me.dkflab.horselunge.objects.Illness;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;

import static me.dkflab.horselunge.Utils.sendMessage;

public final class HorseLunge extends JavaPlugin {

    HashMap<Player, Entity> lungeMap = new HashMap<>();
    public ItemStack lead;
    public DataManager data;
    public FoodWaterManager foodWaterManager;
    public IllnessManager illnessManager;
    public CommandListener commands;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Interact(this), this);
        commands = new CommandListener(this);
        getCommand("lunge").setExecutor(commands);
        getCommand("horse").setExecutor(commands);
        getCommand("vaccine").setExecutor(commands);
        getCommand("tempdebug").setExecutor(commands);
        lead = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = lead.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lLunging Lead"));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Right-click horse to begin lunging."));
        lead.setItemMeta(meta);
        //
        data = new DataManager(this);
        foodWaterManager = new FoodWaterManager(this);
        illnessManager = new IllnessManager(this);
        illnessManager.onEnable();
        // Timers
        new BukkitRunnable() {
            @Override
            public void run() {
                foodWaterManager.foodCheck();
            }
        }.runTaskTimer(this, 0L, 15000); // 12.5min : 15,000
        new BukkitRunnable() {
            @Override
            public void run() {
                foodWaterManager.waterCheck();
            }
        }.runTaskTimer(this, 0L, 10200); // 8.5min : 10,200
    }

    public HashMap<Player, Entity> lungeMap() {
        return lungeMap;
    }

    public void checkStatusOfHorse(Player p) {
        // Check water, food, illness, vaccines
        if (p.isInsideVehicle()) {
            String s = p.getVehicle().getType().toString();
            Entity horse = p.getVehicle();
            if (s.substring(0,5).equals("HORSE")) {
                foodWaterManager.messagePlayerFoodWaterLevels(p, horse, false);
                // TODO: Check for illness/vaccines here!
                // Illnesses
                if (illnessManager.getHorseIllnesses(horse) != null) {
                    for (Illness illness : illnessManager.getHorseIllnesses(horse)) {
                        sendMessage(p, "&7Your horse suffers from &c" + illness.getName() + "&7!");
                    }
                }
                // Vaccines
            } else {
                sendMessage(p, "&cYou need to be riding a horse!");
            }
        } else {
            sendMessage(p, "&cYou need to be riding a horse!");
        }
    }

    public Boolean isOnHorse(Player p) {
        if (p.isInsideVehicle()) {
            String s = p.getVehicle().toString();
            s = s.substring(0, 5);
            if (s.equalsIgnoreCase("HORSE")||s.equalsIgnoreCase("Craft")) {
                return true;
            }
        }
        sendMessage(p, "&cYou need to be riding a horse!");
        return false;
    }

    public void console(String msg) {
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&f&l[ZH] &r"+msg));
    }
}
