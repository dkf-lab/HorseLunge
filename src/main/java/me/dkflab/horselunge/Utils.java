package me.dkflab.horselunge;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.Random;

public class Utils {

    public static void sendMessage(CommandSender p, String msg) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&l[ZH] &r" + msg));
    }

    public static int randomNumber(int min, int max) {
        return new Random().nextInt((max-min) +1)+min;
    }

    public static void removePotionEffects(LivingEntity le) {
        for (PotionEffect e:le.getActivePotionEffects()) {
            le.removePotionEffect(e.getType());
        }
    }

    public static boolean checkForBlockNearby(Location loc, Material m) {
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
        loc = loc.add(0, 1, 0);
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
        loc = loc.add(0, -1, 0);
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
}
