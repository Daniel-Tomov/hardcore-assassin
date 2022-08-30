package me.cageydinosaur.hardcore_assassin;

import org.bukkit.entity.Player;


public class assassins {
	private Player hunter;
	private Player hunted;

	
	Main plugin;
	public assassins(Main plugin) {
		this.plugin = plugin;
	}
	
	public assassins(Player hunter, Player hunted) {
		this.hunter = hunter;
		this.hunted = hunted;
	}
	
	
	public boolean isHunted(Player a) {
		for (assassins i : plugin.assassinsList) {
			if (i.hunter == a) {
				return true;
			}
		}
		return false;
	}
	
	public Player getHunter() {
		return this.hunter;
	}
	public Player getHunted() {
		return this.hunted;
	}
}
