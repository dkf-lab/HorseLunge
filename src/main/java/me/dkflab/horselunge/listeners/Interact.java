package me.dkflab.horselunge.listeners;

import me.dkflab.horselunge.HorseLunge;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Random;

public class Interact implements Listener {

    private HorseLunge main;
    public Interact(HorseLunge m) {
        main = m;
    }
    private int i;
    HashMap<Player, Horse> horses = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        // add donkey support
        if (e.getRightClicked().getType().equals(EntityType.HORSE)) {
            // check for lead
            if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.LEAD)) {
                // we have a lead
                e.setCancelled(true);
                if (horses.containsKey(e.getPlayer())) {
                    // player deactivating
                    horses.remove(e.getPlayer());
                } else {
                    Horse horse = (Horse) e.getRightClicked();
                    System.out.println("moving horse");
                    horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                    followPlayer(e.getPlayer(), horse, 1);
                    horses.put(e.getPlayer(), horse);
                }
            }
        }
    }

    private void followPlayer(Player player, LivingEntity entity, double d) {
        final LivingEntity e = entity;
        final Player p = player;
        final float f = (float) d;
        i = 0;
        new BukkitRunnable() {
            public void run() {
                if (e.getPassengers().contains(p)) {
                    return;
                }
                if (!(horses.containsKey(p))) {
                    cancel();
                    return;
                }
                ((EntityInsentient) ((CraftEntity) e).getHandle()).getNavigation().a(p.getLocation().getX()+ new Random().nextInt(5-1+1), p.getLocation().getY(), p.getLocation().getZ()+new Random().nextInt(5-1+1), f);
            }
        }.runTaskTimer(main, 20L, 20L);
    }
}
