package me.dkflab.horselunge.commands;

import me.dkflab.horselunge.HorseLunge;
import me.dkflab.horselunge.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.dkflab.horselunge.Utils.sendMessage;

public class CommandListener implements CommandExecutor {

    // All commands are redirected here.

    private HorseLunge main;
    private Entity test;
    public CommandListener(HorseLunge p) {
        main = p;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("horse")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length == 1) {
                    // /horse test
                    if (args[0].equals("test")) {
                        main.foodWaterManager.testForFoodWater(p);
                    }
                    // /horse status
                    if (args[0].equals("status")) {
                        main.checkStatusOfHorse(p);
                        return true;
                    }
                    // /horse vaccine
                    if (args[0].equals("vaccine")) {
                        sendMessage(p, "&cUse /vaccine");
                        return true;
                    }
                    // /horse sick <illness>
                    if (args[0].equals("sick")) {
                        sendMessage(sender, "&cIncorrect usage.");
                        return true;
                    }
                    // /horse help
                    if (args[0].equals("help")) {
                        sendMessage(sender, "&8 -=- &bHorse Help &8-=-");
                        sendMessage(sender, "&c/horse &estatus &7- Food/water levels of horse.");
                        sendMessage(sender, "&c/horse &etest &7- Calculates if a horse would find wheat/water from your location.");
                        sendMessage(sender, "&c/lunge &7- Give lunge lead.");
                        sendMessage(sender, "&c/vaccine &e<illness>&7- Vaccinates horse against illness.");
                        if (sender.hasPermission("horse.forcesickness")) {
                            //sendMessage(sender, "&c/horse &esick <illness> &7- Forcibly give horse illness. You have the permission for this command.");
                        }
                        return true;
                    }
                }
                if (args.length == 2) {
                    // /horse sick <illness>
                    if (args[0] == "sick") {
                        if (sender.hasPermission("horse.forcesickness")) {
                            // TODO: Check args if other diseases can be added.
                            return true;
                        } else {
                            sendMessage(sender, "&cInsufficient permissions.");
                        }
                        return true;
                    } else {
                        sendMessage(p, "&c/horse help");
                    }
                    return true;
                }
                // Check status of horse if no args
                if (main.isOnHorse(p)) {
                    main.checkStatusOfHorse(p);
                } else {
                    sendMessage(p, "&c/horse help");
                }
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        }

        if (cmd.getName().equalsIgnoreCase("lunge")) {
            if (sender instanceof Player) {
                ((Player) sender).getInventory().addItem(main.lead);
                sendMessage(sender, "&aGiven lead!");
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        }

        if (cmd.getName().equalsIgnoreCase("vaccine")) {
            if (sender instanceof Player) {
                if (args.length != 1) {
                    sendMessage(sender, "&eCorrect usage: /vaccine <illness>");
                    sendMessage(sender, "&eIllness parameter is defined in config.");
                    return true;
                }
                if (main.isOnHorse((Player)sender)) {
                    main.illnessManager.vaccine((Player)sender, ((Player) sender).getVehicle(), args[0]);
                }
            } else {
                sendMessage(sender, "&cYou need to be a player!");
            }
        }

        if (cmd.getName().equalsIgnoreCase("tempdebug")) {
            Player p = (Player)sender;
            if (test == null) {
                test = p.getVehicle();
                p.sendMessage("new");
                return true;
            } else {
                if (p.getVehicle() == test) {
                    p.sendMessage("aa");
                }
                p.sendMessage("working");
            }
        }
        return true;
    }

}
