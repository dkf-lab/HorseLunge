package me.dkflab.horselunge.managers;

import me.dkflab.horselunge.HorseLunge;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

import static me.dkflab.horselunge.Utils.checkForBlockNearby;
import static me.dkflab.horselunge.Utils.sendMessage;

public class FoodWaterManager {

    // Manages food/water intake

    HashMap<Entity, Integer> water = new HashMap<>();
    HashMap<Entity, Integer> food = new HashMap<>();
    private HorseLunge main;

    public FoodWaterManager(HorseLunge p) {
        main = p;
    }

    public void foodCheck() {
        for (Entity e: food.keySet()) {
            if (!checkForBlockNearby(e.getLocation(), Material.HAY_BLOCK)) {
                if (!checkForBlockNearby(e.getLocation(), Material.GRASS_BLOCK)) {
                    setFoodLevel(e, foodLevel(e)-1);
                }
            }
            checkFoodLevel(e);
        }
    }

    public void waterCheck() {
        for (Entity e : water.keySet()) {
            if (!checkForBlockNearby(e.getLocation(), Material.WATER)) {
                if (!checkForBlockNearby(e.getLocation(), Material.CAULDRON)) {
                    setWaterLevel(e, waterLevel(e)-1);
                }
            }
            checkWaterLevel(e);
        }
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

    public void checkWaterLevel(Entity e) {
        // Goal: to check water level, and then apply relevant potion affects
        LivingEntity le = (LivingEntity)e;
        int waterLevel = waterLevel(e);
        if (waterLevel <= 1) {
            if (e.getPassengers().size() != 0) {
                sendMessage(e.getPassengers().get(0), "&cYour horse needs water!");
            }
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
        }
    }

    public void checkFoodLevel(Entity e) {
        LivingEntity le = (LivingEntity)e;
        int foodLevel = foodLevel(e);
        if (foodLevel <= 1) {
            if (e.getPassengers().size() != 0) {
                sendMessage(e.getPassengers().get(0), "&cYour horse needs food!");
            }
            le.removePotionEffect(PotionEffectType.SLOW);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200));
        }
    }

    public void testForFoodWater(Player p) {
        if (checkForBlockNearby(p.getLocation(), Material.HAY_BLOCK)) {
            sendMessage(p, "&aFood detected!");
        } else {
            if (checkForBlockNearby(p.getLocation(), Material.GRASS_BLOCK)) {
                sendMessage(p, "&aFood detected!");
                return;
            }
            sendMessage(p, "&cFood not detected!");
        }
        if (checkForBlockNearby(p.getLocation(), Material.WATER)) {
            sendMessage(p, "&aWater detected!");
        } else {
            if (checkForBlockNearby(p.getLocation(), Material.CAULDRON)) {
                sendMessage(p, "&aWater detected!");
                return;
            }
            sendMessage(p, "&cWater not detected!");
        }
    }
}
