package me.dkflab.horselunge;

import me.dkflab.horselunge.listeners.Interact;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class HorseLunge extends JavaPlugin implements CommandExecutor {

    public static Location loc;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new Interact(this), this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lunge")) {
            if (sender instanceof Player) {
                Player p = (Player)sender;
                loc = p.getLocation();
            }
        }
        return true;
    }
}
