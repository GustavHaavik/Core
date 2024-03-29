package com.Furnesse.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.Furnesse.core.Core;
import com.Furnesse.core.config.Message;
import com.Furnesse.core.deathchest.DeathChestsGUI;
import com.Furnesse.core.utils.Lang;

public class DeathChestCMD implements CommandExecutor {
	Core plugin = Core.instance;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration langFile = plugin.getFileManager().getConfig("lang.yml").get();

		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Player commands
			if (args.length == 0) {
				player.openInventory(DeathChestsGUI.dcMenu);
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					if (player.hasPermission("core.dc.help")) {
						for (String str : langFile.getStringList("deathchest.help")) {
							player.sendMessage(Lang.chat(str));
						}
					}
				}

				if (args[0].equalsIgnoreCase("clear")) {
					if (player.hasPermission("core.dc.clear")) {
//						plugin.getDeathChest().removeAll();
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}

				if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("core.dc.reload")) {
//						plugin.getDeathChest().loadDeathChests();
					} else {
						player.sendMessage(Message.NO_PERMISSION.getChatMessage());
					}
				}

				if (args[0].equalsIgnoreCase("gui")) {
//					if(player.hasPermission("core.dc.gui")) {
//						
//					}
					player.openInventory(DeathChestsGUI.dcMenu);
				}
			}
			return true;
		}
		return false;
	}
}
