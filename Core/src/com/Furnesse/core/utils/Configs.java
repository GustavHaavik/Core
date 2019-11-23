package com.Furnesse.core.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.Furnesse.core.Core;

public class Configs {

	private FileConfiguration lang;
	private File langFile;
	private FileConfiguration players;
	private File playersFile;
	private FileConfiguration ranks;
	private File ranksFile;
	private FileConfiguration cCommands;
	private File cCommandsFile;
	private FileConfiguration deathchests;
	private File deathchestsFile;
	private FileConfiguration cItems;
	private File cItemsFile;
	
	
	private Core plugin;

	public Configs(Core plugin) {
		this.plugin = plugin;
	}

	public void createCustomConfig() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		langFile = new File(plugin.getDataFolder(), "lang.yml");
		playersFile = new File(plugin.getDataFolder(), "players.yml");
		ranksFile = new File(plugin.getDataFolder(), "ranks.yml");
		cCommandsFile = new File(plugin.getDataFolder(), "customcommands.yml");
		deathchestsFile = new File(plugin.getDataFolder(), "deathchests.yml");
		cItemsFile = new File(plugin.getDataFolder(), "customitems.yml");
		
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}

		if (!configFile.exists()) {
			plugin.saveDefaultConfig();
		}

		if (!langFile.exists()) {
			plugin.saveResource("lang.yml", false);
		}

		if (!playersFile.exists()) {
			plugin.saveResource("players.yml", false);
		}
		
		if (!ranksFile.exists()) {
			plugin.saveResource("ranks.yml", false);
		}
		
		if (!cCommandsFile.exists()) {
			plugin.saveResource("customcommands.yml", false);
		}
		
		if (!deathchestsFile.exists()) {
			plugin.saveResource("deathchests.yml", false);
		}
		
		if (!cItemsFile.exists()) {
			plugin.saveResource("customitems.yml", false);
		}

		lang = new YamlConfiguration();
		players = new YamlConfiguration();
		ranks = new YamlConfiguration();
		cCommands = new YamlConfiguration();
		deathchests = new YamlConfiguration();
		cItems = new YamlConfiguration();
		try {
			lang.load(langFile);
			players.load(playersFile);
			ranks.load(ranksFile);
			cCommands.load(cCommandsFile);
			deathchests.load(deathchestsFile);
			cItems.load(cItemsFile);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to load yml files!");
			plugin.getLogger().log(Level.SEVERE, "Error: " + e.getMessage());
		}
	}

	public void saveConfigs() {
		plugin.saveConfig();
		try {
			lang.save(langFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save lang.yml file!");
		}
		try {
			players.save(playersFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save players.yml file!");
		}
		try {
			ranks.save(ranksFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save ranks.yml file!");
		}
		try {
			cCommands.save(cCommandsFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save customcommands.yml file!");
		}
		try {
			deathchests.save(deathchestsFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save deathchests.yml file!");
		}
		try {
			cItems.save(cItemsFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save customitems.yml file!");
		}
	}

	public void reloadConfigs(CommandSender sender) {
		try {
			lang.load(langFile);
			players.load(playersFile);
			cCommands.load(cCommandsFile);
			ranks.load(ranksFile);
			deathchests.load(deathchestsFile);
			cItems.load(cItemsFile);
			sender.sendMessage(Lang.RELOADED);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Failed to reload yml files!");
			plugin.getLogger().log(Level.SEVERE, "Error: " + e.getMessage());
		}
	}

	public FileConfiguration getLangConfig() {
		return lang;
	}

	public FileConfiguration getPlayersConfig() {
		return players;
	}
	
	public FileConfiguration getRanksConfig() {
		return ranks;
	}
	
	public FileConfiguration getCmdsConfig() {
		return cCommands;
	}
	
	public FileConfiguration getDchestsConfig() {
		return deathchests;
	}
	
	public FileConfiguration getCItemsConfig() {
		return cItems;
	}
}
