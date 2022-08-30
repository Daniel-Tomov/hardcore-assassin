package me.cageydinosaur.hardcore_assassin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Tab implements TabCompleter {
	Main plugin;

	public Tab(Main plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			List<String> commands = new ArrayList<>();
			if (sender.hasPermission("assassins")) {
				if (sender.hasPermission("assassins.reload")) {
					commands.add("reload");
				}
				if (sender.hasPermission("assassins.rr")) {
					commands.add("rr");
				}
				if (sender.hasPermission("assassins.rc")) {
					commands.add("rc");
				}
				if (sender.hasPermission("assassins.add")) {
					commands.add("add");
				}
				if (sender.hasPermission("assassins.take")) {
					commands.add("take");
				}
				if (sender.hasPermission("assassins.random")) {
					commands.add("random");
				}
				if (sender.hasPermission("assassins.finish")) {
					commands.add("finish");
				}

				return commands;
			}

		}
		return null;
	}

}
