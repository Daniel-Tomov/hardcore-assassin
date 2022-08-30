package me.cageydinosaur.hardcore_assassin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {

	Main plugin;

	public Events(Main plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player joiner = e.getPlayer();
		if (!(plugin.ifRespawns(joiner))) {
			plugin.addInfo(joiner, 3);
			plugin.createScoreboard(joiner, 3);

			if (plugin.verbosity())
				joiner.sendMessage(ChatColor.GREEN + "You have " + ChatColor.RED + "3" + ChatColor.GREEN
						+ " respawns. If you die you will " + ChatColor.RED + "lose one" + ChatColor.GREEN
						+ " respawn. If you " + ChatColor.RED + "kill" + ChatColor.GREEN + " other players, you will "
						+ ChatColor.RED + "gain one" + ChatColor.GREEN
						+ " respawn. Once you lose all of your respawns, you will be put into " + ChatColor.RED
						+ "Spectator Mode" + ChatColor.GREEN + ". Have fun playing!");
		} else {
			int respawnAmt = plugin.getPlayerRespawns(joiner);
			if (plugin.verbosity())
				joiner.sendMessage(ChatColor.GREEN + "Just a reminder, you have " + ChatColor.RED + respawnAmt
						+ ChatColor.GREEN + " respawns.");
			plugin.createScoreboard(joiner, respawnAmt);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player killed = e.getEntity();

		Player killer = plugin.getHunter(killed);
		if (killer != null) {

			if (killer == killed) {
				plugin.removePlayers(killer, killed);
				return;
			}
			int respawnAmt = plugin.getPlayerRespawns(killer) + 1;

			plugin.createScoreboard(killer, respawnAmt);

			plugin.removeInfo(killer);
			plugin.addInfo(killer, respawnAmt);

			if (plugin.verbosity())
				killer.sendMessage(ChatColor.GREEN + "You have killed " + ChatColor.RED + killed.getDisplayName()
						+ ChatColor.GREEN + " and have received one respawn. Your total is " + ChatColor.RED
						+ respawnAmt + ChatColor.GREEN + " respawns");

			respawnAmt = plugin.getPlayerRespawns(killed) - 1;
			if (respawnAmt == 0) {
				if (plugin.verbosity())
					killed.sendMessage(ChatColor.RED + "You are out of respawns.");
				killed.setGameMode(GameMode.SPECTATOR);
				plugin.removeInfo(killed);
				plugin.addInfo(killed, respawnAmt);
			} else {
				plugin.createScoreboard(killed, respawnAmt);

				plugin.removeInfo(killed);
				plugin.addInfo(killed, respawnAmt);

				if (plugin.verbosity())
					killed.sendMessage(ChatColor.GREEN + "You have died" + ChatColor.GREEN + " and now have "
							+ ChatColor.RED + respawnAmt + ChatColor.GREEN + " respawns");

			}

			plugin.removePlayers(killer, killed);

			return;
		} else if (plugin.natural()) {
			
			if (killed.getKiller() instanceof Player){
				killer = killed.getKiller();
				int respawnAmt = plugin.getPlayerRespawns(killer) + 1;

				plugin.createScoreboard(killer, respawnAmt);

				plugin.removeInfo(killer);
				plugin.addInfo(killer, respawnAmt);

				if (plugin.verbosity())
					killer.sendMessage(ChatColor.GREEN + "You have killed " + ChatColor.RED + killed.getDisplayName()
							+ ChatColor.GREEN + " and have received one respawn. Your total is " + ChatColor.RED
							+ respawnAmt + ChatColor.GREEN + " respawns");
			}
			
			int respawnAmt = plugin.getPlayerRespawns(killed) - 1;
			if (respawnAmt < 0) {return;}
			if (respawnAmt == 0) {
				if (plugin.verbosity())
					killed.sendMessage(ChatColor.RED + "You are out of respawns.");
				killed.setGameMode(GameMode.SPECTATOR);
			} else {
				plugin.createScoreboard(killed, respawnAmt);

				plugin.removeInfo(killed);
				plugin.addInfo(killed, respawnAmt);

				if (plugin.verbosity())
					killed.sendMessage(ChatColor.GREEN + "You have died" + ChatColor.GREEN + " and now have "
							+ ChatColor.RED + respawnAmt + ChatColor.GREEN + " respawns");

			}
		}

	}
}
