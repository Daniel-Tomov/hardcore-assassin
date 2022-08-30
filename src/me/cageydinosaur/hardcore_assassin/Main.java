package me.cageydinosaur.hardcore_assassin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin {

	private File customRespawnsFile;
	private FileConfiguration RespawnsConfig;

	public ArrayList<String> respawnList = new ArrayList<String>();
	public ArrayList<assassins> assassinsList = new ArrayList<assassins>();

	Team one, two, three, more;

	public void onEnable() {

		getCommand("assassins").setExecutor(new Cmd(this));
		getCommand("assassins").setTabCompleter(new Tab(this));
		this.getServer().getPluginManager().registerEvents(new Events(this), this);

		this.saveDefaultConfig();

		this.createRespawnsConfig();
		this.addRespawnsToList();

	}

	public void onDisable() {
		this.addRespawnsToConfig();
	}

	public String chat(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	/*
	 * private void setPlayerName(Player player ,String color){ String oldName =
	 * player.getName();
	 * 
	 * Player changingName = ((Player)player).getHandle();
	 * changingName.setCustomName(color+player.getName()); for(Player playerinworld
	 * : Bukkit.getOnlinePlayers()){ if(playerinworld != player){
	 * ((Player)playerinworld).getHandle().netServerHandler.sendPacket(new
	 * Packet20NamedEntitySpawn(changingName)); } } changingName.name = oldName; }
	 */

	public void createScoreboard(Player player, int respawnAmt) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();

		if (board.getTeam("one") == null) {
			one = board.registerNewTeam("one");
			one.setColor(ChatColor.RED);
			one.setPrefix(ChatColor.RED + "");
		} else {
			one = board.getTeam("one");
		}

		if (board.getTeam("two") == null) {
			two = board.registerNewTeam("two");
			two.setColor(ChatColor.YELLOW);
			two.setPrefix(ChatColor.YELLOW + "");
		} else {
			two = board.getTeam("two");
		}

		if (board.getTeam("three") == null) {
			three = board.registerNewTeam("three");
			three.setColor(ChatColor.GREEN);
			three.setPrefix(ChatColor.GREEN + "");
		} else {
			three = board.getTeam("three");
		}

		if (board.getTeam("more") == null) {
			more = board.registerNewTeam("more");
			more.setColor(ChatColor.DARK_PURPLE);
			more.setPrefix(ChatColor.DARK_PURPLE + "");
		} else {
			more = board.getTeam("more");
		}

		if (respawnAmt == 1) {
			player.setPlayerListName(ChatColor.RED + player.getName());
			player.setDisplayName(ChatColor.RED + player.getName());
			one.addPlayer((OfflinePlayer) player);
		} else if (respawnAmt == 2) {
			player.setPlayerListName(ChatColor.YELLOW + player.getName());
			player.setDisplayName(ChatColor.YELLOW + player.getName());
			two.addPlayer((OfflinePlayer) player);
		} else if (respawnAmt == 3) {
			player.setPlayerListName(ChatColor.GREEN + player.getName());
			player.setDisplayName(ChatColor.GREEN + player.getName());
			three.addPlayer((OfflinePlayer) player);
		} else if (respawnAmt > 3) {
			player.setPlayerListName(ChatColor.DARK_PURPLE + player.getName());
			player.setDisplayName(ChatColor.DARK_PURPLE + player.getName());
			more.addPlayer((OfflinePlayer) player);
		}

//		one.addPlayer((OfflinePlayer) player);

		player.setScoreboard(board);
	}

	public String assassinMessage() {
		return this.getConfig().getString("assassin_Message");
	}

	public void finish() {
		for (assassins i : this.assassinsList) {
			Player hunter = i.getHunter();

			hunter.sendMessage(ChatColor.RED + "You were not able to kill " + ChatColor.GREEN + i.getHunted().getName()
					+ ChatColor.RED + " quick enough. You have lost a respawn.");
			hunter.setHealth(0);
		}
	}

	public boolean verbosity() {
		return this.getConfig().getBoolean("verbosity");
	}

	public boolean natural() {
		return this.getConfig().getBoolean("natural_Causes");
	}
	
	public int get_Number_Of_Assassins() {return this.getConfig().getInt("amount_Of_Assassins");}

	public Player getHunter(Player a) {
		for (assassins i : this.assassinsList) {
			if (i.getHunted() == a) {
				return i.getHunter();
			}
		}
		return null;
	}

	public void removePlayers(Player hunter, Player hunted) {
		for (int i = 0; i < this.assassinsList.size(); i++) {
			assassins obj = this.assassinsList.get(i);

			if (obj.getHunter() == hunter && obj.getHunted() == hunted) {
				this.assassinsList.remove(i);
			}
		}
	}

	public String getPlayerName() {
		String playerName = "hey";
		for (String i : this.respawnList) {
			String[] split = i.split(", ", 2);
			playerName = split[0];
		}
		return playerName;
	}

	public void addInfo(Player player, int respawnsL) {
		this.respawnList.add(player.getName() + ", " + Integer.toString(respawnsL));
	}

	public void removeInfo(Player player) {
		for (String i : respawnList) {
			String[] split = i.split(", ", 2);
			if (split[0].equals(player.getName())) {
				respawnList.remove(split[0] + ", " + split[1]);
				return;
			}
		}
	}

	public boolean ifRespawns(Player player) {
		for (String i : this.respawnList) {
			String[] split = i.split(", ", 2);
			if (Bukkit.getPlayer(split[0]) == player) {
				return true;
			}
		}
		return false;
	}

	public int getPlayerRespawns(Player player) {
		int respawnsLeft = 3;
		for (String i : this.respawnList) {
			String[] split = i.split(", ", 2);
			if (Bukkit.getPlayer(split[0]) == player) {
				respawnsLeft = Integer.parseInt(split[1]);
				return respawnsLeft;
			}
		}
		return respawnsLeft;
	}

	public void addRespawnsToConfig() {
		ArrayList<String> list = new ArrayList<String>();
		// String[] list = null;
		list.clear();
		/* this.clearConfig(); */
		for (String r : this.respawnList) {
			list.add(r);
		}
		this.getRespawnsConfig().set("respawns", list);
		this.saveRespawnsConfig();
	}

	public void addRespawnsToList() {
		this.respawnList.clear();
		for (String i : this.RespawnsConfig.getStringList("respawns")) {
			this.respawnList.add(i);

		}
	}

	public void clearRespawnsConfig() {
		this.getRespawnsConfig().set("respawns", "");
		this.saveRespawnsConfig();
	}

	public FileConfiguration getRespawnsConfig() {
		return this.RespawnsConfig;
	}

	public void saveRespawnsConfig() {
		try {
			this.RespawnsConfig.save(customRespawnsFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createRespawnsConfig() {
		customRespawnsFile = new File(getDataFolder(), "respawns.yml");
		if (!customRespawnsFile.exists()) {
			customRespawnsFile.getParentFile().mkdirs();
			saveResource("respawns.yml", true);
		}

		RespawnsConfig = new YamlConfiguration();
		try {
			RespawnsConfig.load(customRespawnsFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
