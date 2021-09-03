package me.dkflab.horselunge;

import me.dkflab.horselunge.listeners.Interact;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public final class HorseLunge extends JavaPlugin implements CommandExecutor {

    HashMap<Player, Entity> lungeMap = new HashMap<>();
    HashMap<Entity, Integer> water = new HashMap<>();
    HashMap<Entity, Integer> food = new HashMap<>();
    HashMap<Entity, String> illness = new HashMap<>();
    HashMap<Entity, Boolean> jab = new HashMap<>();
    public ItemStack lead;
    public DataManager data;

    @Override
    public void onEnable() {
        // Plugin startup logic
        //saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Interact(this), this);
        // Custom lead
        lead = new ItemStack(Material.SLIME_BALL);
        ItemMeta meta = lead.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lLunging Lead"));
        meta.setLore(Collections.singletonList(ChatColor.GRAY + "Right-click horse to begin lunging."));
        lead.setItemMeta(meta);
        //data = new DataManager(this);
        // FOOD
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity e:food.keySet()) {
                    checkFoodLevel(e, true);
                }
            }
        }.runTaskTimer(this, 0L, 15000); // 12.5min
        // WATER
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity e : water.keySet()) {
                    checkWaterLevel(e, true);
                }
            }
        }.runTaskTimer(this, 0L, 10200); // 8.5min
    }

    public void checkWaterLevel(Entity e, boolean take) {
        // Goal: to check water level, and then apply relevant potion affects
        LivingEntity le = (LivingEntity)e;
        int waterLevel = waterLevel(e);
        if (waterLevel <= 1) {
            if (e.getPassengers().size() != 0) {
                sendMessage(e.getPassengers().get(0), "&cYour horse needs water!");
            }
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
            return;
        }
        if (take) {
            if (!checkForBlockNearby(e.getLocation(), Material.WATER)) {
                if (!checkForBlockNearby(e.getLocation(), Material.CAULDRON)) {
                    waterLevel -= 1;
                }
            }
        }
        // apply potion effects
        if (waterLevel == 1) {
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
            if (e.getPassengers().size() != 0) {
                // message player
                sendMessage(e.getPassengers().get(0), "&cYour horse needs water!");
            }
        }
        water.put(e, waterLevel);
    }

    public void checkFoodLevel(Entity e, boolean take) {
        LivingEntity le = (LivingEntity)e;
        int foodLevel = foodLevel(e);
        if (foodLevel <= 1) {
            if (e.getPassengers().size() != 0) {
                sendMessage(e.getPassengers().get(0), "&cYour horse needs food!");
            }
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
            return;
        }
        if (take) {
            if (!checkForBlockNearby(e.getLocation(), Material.HAY_BLOCK)) {
                if (!checkForBlockNearby(e.getLocation(), Material.GRASS_BLOCK)) {
                    foodLevel -= 1;
                }
            }
        }
        // apply potion effects
        if (foodLevel == 1) {
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
            if (e.getPassengers().size() != 0) {
                // message player
                sendMessage(e.getPassengers().get(0), "&cYour horse needs food!");
            }
        }
        water.put(e, foodLevel);
    }

    public void addSlowness(Entity e, int amp) {
        LivingEntity le = (LivingEntity) e;
        le.removePotionEffect(PotionEffectType.SLOW);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, amp));
    }

    public void removeSlow(Entity e) {
        LivingEntity le = (LivingEntity) e;
        le.removePotionEffect(PotionEffectType.SLOW);
        if (illness.get(e) == null) {
            return;
        }
        if (illness.get(e).equals("Slowvid-19")) {
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3));
        }
    }

    public HashMap<Player, Entity> lungeMap() {
        return lungeMap;
    }

    public int waterLevel(Entity e) {
        water.putIfAbsent(e, 5);
        return water.get(e);
    }

    public int foodLevel(Entity e) {
        food.putIfAbsent(e, 5);
        return food.get(e);
    }

    public void setWaterLevel(Entity e,Integer level) {
        water.put(e, level);
    }

    public void setFoodLevel(Entity e, Integer level) {
        food.put(e, level);
    }

    public void illnessRoll(Entity h, Player p) {
        if (illness.get(h) != null) {
            return;
        }
        jab.putIfAbsent(h, false);
        if (jab.get(h)) {
            return;
        }
        if (randomNumber(0, 100) >= 90) {
            // 10%
            illness.put(h, "Slowvid-19");
            sendMessage(p, "&bOh No! Your horse has contracted &c" + illness.get(h) +"&b!");
            LivingEntity le = (LivingEntity) h;
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3));
        }
    }

    public void messagePlayerFoodWaterLevels(Player p, Entity h, Boolean noAlert) {
        switch(waterLevel(h)) {
            case 1:
                sendMessage(p, "&4Your horse is critically low on water!");
                sendMessage(p, "&4It's speed will be impacted.");
                break;
            case 2:
                sendMessage(p, "&cYour horse is low on water!");
                break;
            case 3:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&eYour horse has an average water level!");
                break;
            case 4:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&aYour horse has a good water level!");
                break;
            case 5:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&2Your horse has a high water level!");
                break;
            default:
                break;
        }
        switch(foodLevel(h)) {
            case 1:
                sendMessage(p, "&4Your horse is critically low on food!");
                sendMessage(p, "&4It's speed will be impacted.");
                break;
            case 2:
                sendMessage(p, "&cYour horse is low on food!");
                break;
            case 3:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&eYour horse has an average food level!");
                break;
            case 4:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&aYour horse has a good food level!");
                break;
            case 5:
                if (noAlert) {
                    return;
                }
                sendMessage(p, "&2Your horse has a high food level!");
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("horse")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                if (args.length == 1) {
                    // /horse test
                    if (args[0].equals("test")) {
                        if (checkForBlockNearby(p.getLocation(), Material.HAY_BLOCK)) {
                            sendMessage(p, "&aFood detected!");
                        } else {
                            if (checkForBlockNearby(p.getLocation(), Material.GRASS_BLOCK)) {
                                sendMessage(p, "&aFood detected!");
                                return true;
                            }
                            sendMessage(p, "&cFood not detected!");
                        }
                        if (checkForBlockNearby(p.getLocation(), Material.WATER)) {
                            sendMessage(p, "&aWater detected!");
                        } else {
                            if (checkForBlockNearby(p.getLocation(), Material.CAULDRON)) {
                                sendMessage(p, "&aWater detected!");
                                return true;
                            }
                            sendMessage(p, "&cWater not detected!");
                        }
                        return true;
                    }
                    if (args[0].equals("status")) {
                        checkStatusOfHorse(p);
                        return true;
                    }
                    if (args[0].equals("vaccine")) {
                        vaccine(p);
                        return true;
                    }
                    // /horse sick <illness>
                    if (args[0].equals("sick")) {
                        sendMessage(sender, "&cIncorrect usage.");
                        return true;
                    }
                    // /horse help
                    if (args[0].equals("help")) {
                        sendMessage(sender, "&7 -=- &bHorse Help &7-=-");
                        sendMessage(sender, "&c/horse &estatus &7- Food/water levels of horse.");
                        sendMessage(sender, "&c/horse &etest &7- Calculates if a horse would find wheat/water from your location.");
                        sendMessage(sender, "&c/horse &esick <illness> &7- Forcibly give horse illness. (perm needed)");
                        sendMessage(sender, "&c/horse &evaccine &7- Vaccinate horse. (perm needed)");
                        sendMessage(sender, "&c/lunge &7- Give lunge lead.");
                        return true;
                    }
                }
                if (args.length == 2) {
                    // /horse sick <illness>
                    if (args[0]=="sick") {
                        if (sender.hasPermission("horse.forcesickness")) {
                            // TODO: Check args if other diseases can be added.
                            if (p.isInsideVehicle()) {
                                String s = p.getVehicle().getType().toString();
                                if (s.substring(0,5).equals("HORSE")) {
                                    // Player on horse
                                    illness.put(p.getVehicle(), "Slowvid-19");
                                    sendMessage(p, "&bOh No! Your horse has contracted &c" + illness.get(p.getVehicle()) +"&b!");
                                    ((LivingEntity)p.getVehicle()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3));
                                } else {
                                    sendMessage(p, "&cYou need to be riding a horse!");
                                }
                            } else {
                                sendMessage(p, "&cYou need to be on a horse!");
                            }
                        } else {
                            sendMessage(sender, "&cInsufficient permissions.");
                        }
                        return true;
                    }
                    sendMessage(p, "&c/horse help");
                    return true;
                }
                // status of horse
                checkStatusOfHorse(p);
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("lunge")) {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(lead);
                sendMessage(sender, "&aGiven lead!");
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("vaccine")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                vaccine(p);
                } else {
                    sendMessage(sender, "&cYou need to be riding a horse!");
                }
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        return true;
        }


    public void vaccine(Player p) {
        if (p.isInsideVehicle()) {
            String s = p.getVehicle().getType().toString();
            if (s.substring(0, 5).equals("HORSE")) {
                if (p.hasPermission("horse.vaccine")) {
                    LivingEntity horse = (LivingEntity) p.getVehicle();
                    horse.removePotionEffect(PotionEffectType.SLOW);
                    illness.put(horse, null);
                    jab.put(horse, true);
                    sendMessage(p, "&aVaccine success!");
                } else {
                    sendMessage(p, "&cInsufficient permissions.");
                }

            } else {
                sendMessage(p, "&cYou need to be riding a horse!");
            }
        }
    }

    public void checkStatusOfHorse(Player p) {
        if (p.isInsideVehicle()) {
            String s = p.getVehicle().getType().toString();
            if (s.substring(0,5).equals("HORSE")) {
                messagePlayerFoodWaterLevels(p, p.getVehicle(), false);
                if (illness.get(p.getVehicle()) == "Slowvid-19") {
                    sendMessage(p, "&bYour horse also suffers from &cSlowvid-19&b!");
                }
            }else {
                sendMessage(p, "&cYou need to be riding a horse!");
            }
        } else {
            sendMessage(p, "&cYou need to be riding a horse!");
        }
    }

    public boolean checkForBlockNearby(Location loc, Material m) {
        loc = loc.add(3, 0, 3);
        for(int i = 0; i < 10 ; i++)
        {
            for(int j = 0; j < 10 ; j++ )
            {
                if(loc.getBlock().getType().equals(m))
                {
                    return true;
                }
                loc = loc.add(0,0,-1);
            }
            loc = loc.add(-1,0,10);
        }
        return false;
    }

    //utils
    public void sendMessage(CommandSender p, String msg) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[ZH] &r" + msg));
    }

    public int randomNumber(int min, int max) {
        return new Random().nextInt((max-min) +1)+min;
    }
}
