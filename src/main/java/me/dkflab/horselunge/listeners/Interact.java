package me.dkflab.horselunge.listeners;

import me.dkflab.horselunge.HorseLunge;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Interact implements Listener {

    private HorseLunge main;
    public Interact(HorseLunge m) {
        main = m;
    }
    private int i;

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        String s = e.getRightClicked().getType().toString();
        if (s.substring(0,5).equals("HORSE")) {
            Player p = e.getPlayer();
            Entity horse = e.getRightClicked();
            LivingEntity le = (LivingEntity)horse;
            // check for lead for lunging
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.SLIME_BALL)) { // TODO: item.isSimilar(main.lead)
                leadToggle(e);
                return;
            }
            // drinking
            if (item.getType().equals(Material.WATER_BUCKET)) {
                e.setCancelled(true);
                p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET, 1));
                main.sendMessage(p, "&aYour horses' thirst has been fulfilled!");
                main.setWaterLevel(horse, 5);
                return;
            }
            // feeding
            if (item.getType().equals(Material.WHEAT)) {
                e.setCancelled(true);
                item.setAmount(item.getAmount()-1);
                p.getInventory().setItemInMainHand(item);
                main.setFoodLevel(horse, 5);
                main.sendMessage(p, "&aYour horses' appetite has been fulfilled!");
                return;
            }
            // check food, water
            main.removeSlow(horse);
            main.checkFoodLevel(horse, false);
            main.checkWaterLevel(horse, false);
            // message
            if (main.foodLevel(horse) == 1) {
                main.sendMessage(p, "&4Your horse is critically low on food!");
                main.sendMessage(p, "&4It's speed will be impacted.");
            }
            if (main.waterLevel(horse) == 1) {
                main.sendMessage(p, "&4Your horse is critically low on water!");
                main.sendMessage(p, "&4It's speed will be impacted.");
            }
            // sickness
            main.illnessRoll(horse, p);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        main.lungeMap().remove(e.getPlayer());
    }

    private void leadToggle(PlayerInteractEntityEvent e) {
        e.setCancelled(true);
        if (main.lungeMap().containsKey(e.getPlayer())) {
            // player deactivating
            main.lungeMap().remove(e.getPlayer());
            main.sendMessage(e.getPlayer(), "&cDeactivated Lunging!");
        } else {

            Entity horse = e.getRightClicked();
            main.sendMessage(e.getPlayer(), "&aActivated Lunging!");
            followPlayer(e.getPlayer(), horse, 1);
            main.lungeMap().put(e.getPlayer(), horse);
        }
    }

    // LUNGING
    private void followPlayer(Player p, Entity horse, double d) {
        final Location center = p.getLocation().getBlock().getLocation();
        final float radius = 3F;
        final float radPerSec = 1F; //2 default
        final float radPerTick = radPerSec / 20f;

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                ++tick;
                Location loc = getLocationAroundCircle(p.getLocation().getBlock().getLocation(), radius, radPerTick * tick, p.getLocation());
                horse.setVelocity(new Vector(1, 0, 0));
                horse.teleport(loc);
                if (!main.lungeMap().containsKey(p)) {
                    cancel();
                }
            }

        }.runTaskTimer(main, 0L, 1L);
    }

    public Location getLocationAroundCircle(Location center, double radius, double angleInRadian, Location player) {
        if (main.randomNumber(0, 2) == 0) {
            radius += 0.2;
        }
        if (main.randomNumber(0, 2) == 1) {
            radius += 0.5;
        }
        if (main.randomNumber(0, 2)==2) {
            radius += 0.7;
        }
        double x = center.getX() + radius * Math.cos(angleInRadian);
        double z = center.getZ() + radius * Math.sin(angleInRadian);
        double y = center.getY();
        Location loc = new Location(center.getWorld(), x, y, z);
        return loc;
    }
}
