package me.cageydinosaur.hardcore_assassin;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Cmd implements CommandExecutor {
	Main plugin;

	public Cmd(Main plugin) {
		this.plugin = plugin;
	}

	public ArrayList<String> assassins = new ArrayList<String>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.hasPermission("assassins"))) {
			sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "Usage:");
			if (sender.hasPermission("assassins.add"))
				sender.sendMessage(
						ChatColor.RED + "/assassins add <player> " + ChatColor.GREEN + "- gives player 1 respawn");
			if (sender.hasPermission("assassins.take"))
				sender.sendMessage(ChatColor.RED + "/assassins take <player>" + ChatColor.GREEN
						+ " - takes one respawn from player");
			if (sender.hasPermission("assassins.reload"))
				sender.sendMessage(ChatColor.RED + "/assassins reload" + ChatColor.GREEN + " - reloads the config");
			if (sender.hasPermission("assassins.rr"))
				sender.sendMessage(ChatColor.RED + "/assassins rr" + ChatColor.GREEN
						+ " - Reloads the respawns into the list. Be careful with this one! Will reset respawns of players to their state after last adding respawns to the config. Use /assassins rc first!");
			if (sender.hasPermission("assassins.rc"))
				sender.sendMessage(ChatColor.RED + "/assassins rc" + ChatColor.GREEN
						+ " - Adds the current respawns to the config file.");
			if (sender.hasPermission("assassins.random"))
				sender.sendMessage(ChatColor.RED + "/assassins random" + ChatColor.GREEN + " - Randomizes assassins.");
			if (sender.hasPermission("assassins.finish"))
				sender.sendMessage(ChatColor.RED + "/assassins finish" + ChatColor.GREEN + " - Kills any assassins that haven't killed their target.");
			return true;
		}

		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("assassins.reload")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				plugin.reloadConfig();
				sender.sendMessage("Reloaded the config");
				return true;

			} else if (args[0].equalsIgnoreCase("rr")) {
				if (!sender.hasPermission("assassins.reloadrespawns")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				plugin.addRespawnsToList();

				sender.sendMessage("Added respawns to list");
				return true;

			} else if (args[0].equalsIgnoreCase("rc")) {
				if (!sender.hasPermission("assassins.reloadrespawnsconfig")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				plugin.addRespawnsToConfig();
				sender.sendMessage("Added respawns to config");
				return true;

			} else if (args[0].equalsIgnoreCase("add")) {
				if (!(sender.hasPermission("assassins.add"))) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "You must specify a player name");
					return true;
				} else if (args.length == 2) {
					Player recievingPlayer = Bukkit.getPlayer(args[1]);
					if (recievingPlayer == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online");
						return true;
					}

					int respawnAmt = plugin.getPlayerRespawns(recievingPlayer) + 1;
					plugin.removeInfo(recievingPlayer);
					plugin.addInfo(recievingPlayer, respawnAmt);
					plugin.createScoreboard(recievingPlayer, respawnAmt);

					sender.sendMessage(ChatColor.RED + recievingPlayer.getDisplayName() + ChatColor.GREEN + " now has "
							+ ChatColor.RED + respawnAmt + ChatColor.GREEN + " respawns");

					return true;
				}

			} else if (args[0].equalsIgnoreCase("take")) {
				if (!(sender.hasPermission("assassins.take"))) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				if (args.length == 1) {
					sender.sendMessage(ChatColor.RED + "You must specify a player name");
					return true;
				} else if (args.length == 2) {
					Player recievingPlayer = Bukkit.getPlayer(args[1]);
					if (recievingPlayer == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online");
						return true;
					}

					int respawnAmt = plugin.getPlayerRespawns(recievingPlayer) - 1;
					plugin.removeInfo(recievingPlayer);
					plugin.addInfo(recievingPlayer, respawnAmt);
					plugin.createScoreboard(recievingPlayer, respawnAmt);

					sender.sendMessage(ChatColor.RED + recievingPlayer.getDisplayName() + ChatColor.GREEN + " now has "
							+ ChatColor.RED + respawnAmt + ChatColor.GREEN + " respawns");
					
					if (respawnAmt == 0) {
						recievingPlayer.sendMessage(ChatColor.RED + "An admin has decided you should die.");
						recievingPlayer.setHealth(0);
						recievingPlayer.setGameMode(GameMode.SPECTATOR);
					}

					return true;
				}

			} else if (args[0].equalsIgnoreCase("random")) {
				ArrayList<String> hunters = new ArrayList<String>();
				ArrayList<String> hunteds = new ArrayList<String>();
				if (!sender.hasPermission("assassins.random")) {
					sender.sendMessage(ChatColor.RED + "You do not have permission to use that!");
					return true;
				}
				int amount_Of_Players = Bukkit.getOnlinePlayers().size();
				
				for (Player i : Bukkit.getOnlinePlayers()) {
					hunters.add(i.getName());
					hunteds.add(i.getName());
				}

				for (int i = 0; i < plugin.get_Number_Of_Assassins(); i++) {
					Random rand = new Random();
					int random_Number = rand.nextInt(amount_Of_Players);					
					Player assassin = Bukkit.getPlayer(hunters.get(random_Number));
					hunters.remove(random_Number);

					random_Number = rand.nextInt(amount_Of_Players);
					Player hunted = Bukkit.getPlayer(hunteds.get(random_Number));
					hunteds.remove(random_Number);
					
					plugin.assassinsList.add(new assassins(assassin, hunted));
					
					assassin.sendMessage(plugin.chat(plugin.assassinMessage().replace("<player>", hunted.getName())));
				}
				return true;
			}else if (args[0].equalsIgnoreCase("finish")){
				plugin.finish();
				return true;
				
			}else {
				sender.sendMessage(ChatColor.RED + "That command doesn't exist");
				return true;
			}
		}
		return true;
	}

}
