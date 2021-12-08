package com.github.kevindagame.Commands;

import com.github.kevindagame.ShopGuiDataLogger;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Command implements CommandExecutor {
    private final ShopGuiDataLogger main;

    public Command(ShopGuiDataLogger main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equals("dump")) {
                if (main.getDB().dumpData()) {
                    sender.sendMessage("Succesfully created file!");
                } else {
                    sender.sendMessage("An error occured when writing file! See the console for more information.");
                }
            } else if (args[0].equals("report")) {
                if (main.getDB().report()) {
                    sender.sendMessage("Succesfully created file!");
                } else {
                    sender.sendMessage("An error occured when writing file! See the console for more information.");
                }
            }
        }
        if (args.length == 2) {
            if (args[0].equals("item")) {
                for (Material m : Material.values()) {
                    if (args[1].equalsIgnoreCase(m.toString())) {
                        if (main.getDB().itemReport(args[1].toUpperCase())) {
                            sender.sendMessage("Succesfully created file!");
                        } else {
                            sender.sendMessage("An error occured when writing file! See the console for more information.");
                        }
                        return true;
                    }
                }
                sender.sendMessage("invalid item!");

            }
        }
        return true;
    }
}
