package com.Furnesse.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Furnesse.core.Events.PlayerEvents;
import com.Furnesse.core.Events.Scoreboard;
import com.Furnesse.core.chat.ChatFormats;
import com.Furnesse.core.commands.CoreCMD;
import com.Furnesse.core.commands.CustomCMD;
import com.Furnesse.core.commands.DeathChestCMD;
import com.Furnesse.core.commands.ItemsCMD;
import com.Furnesse.core.commands.RankCMD;
import com.Furnesse.core.customcommands.CustomCommand;
import com.Furnesse.core.customcommands.CustomCommands;
import com.Furnesse.core.customitems.CItemManager;
import com.Furnesse.core.deathchest.DeathChestListener;
import com.Furnesse.core.deathchest.DeathChestManager;
import com.Furnesse.core.deathchest.DeathChestsGUI;
import com.Furnesse.core.listeners.ChatEvent;
import com.Furnesse.core.listeners.CraftingRecipes;
import com.Furnesse.core.rank.RankManager;
import com.Furnesse.core.utils.FileManager;
import com.Furnesse.core.utils.Lang;
import com.Furnesse.core.utils.Settings;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderHook;
import net.milkbowl.vault.economy.Economy;

public class Core extends JavaPlugin implements Listener {
	public static Core instance;

	private static final Logger log = Logger.getLogger("Minecraft");
//	private static Permission perms = null;
	private static Economy econ = null;
//	private static Chat chat = null;

	private static HeadDatabaseAPI headAPI;
	
	private RankManager rankMan = new RankManager(this);
	private CustomCommands commands = new CustomCommands(this);
	private Scoreboard sb = new Scoreboard(this);
	private CItemManager cItemMan = new CItemManager(this);
	private ChatFormats chatFormat = new ChatFormats(this);

	private FileManager fileManager = new FileManager(this);
	
	private String host, database, username, password;
	public String playerTable = "player_data", deathchestTable = "stored_deathchests";
	private int port;
	private Connection connection;
	
	public boolean usingMySQL = false;
	public boolean usingSb = false;
	public boolean usingDc = false;
	public boolean usingRanks = false;
	public boolean usingChat = true;
	
	public void enableSystems() {
		usingMySQL = getConfig().getBoolean("database.enabled");
		usingSb = getConfig().getBoolean("scoreboard.enabled");
		usingDc = getConfig().getBoolean("deathchests.enabled");
		usingRanks = getConfig().getBoolean("using_ranks");
		usingChat = getConfig().getBoolean("chat.enabled");
		
		this.getLogger().info("MySQL: " + usingMySQL);
		this.getLogger().info("Scoreboard: " + usingSb);
		this.getLogger().info("Deathchests: " + usingDc);
		this.getLogger().info("Ranks: " + usingRanks);
		this.getLogger().info("Chat: " + usingChat);
	}
	
	public void onEnable() {
		this.getLogger().info("<------<< Furnesse CORE >>------>");
		instance = this;
		
		registerConfigs();

		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupEconomy();
		enableSystems();
		setupConfigurations();
		
		registerListeners();
		registerPlaceholders();
		registerCommands();
		disableRecipes();
		
		this.getLogger().info("Has been enabled v" + this.getDescription().getVersion());
		this.getLogger().info("<------------------------------->");
	}
	
	private void registerConfigs() {
		fileManager.getConfig("config.yml").copyDefaults(true).save();
		fileManager.getConfig("lang.yml").copyDefaults(true).save();
		fileManager.getConfig("customcommands.yml").copyDefaults(true).save();
		fileManager.getConfig("customitems.yml").copyDefaults(true).save();
		fileManager.getConfig("deathchests.yml").copyDefaults(true).save();
		fileManager.getConfig("players.yml").copyDefaults(true).save();
		fileManager.getConfig("ranks.yml").copyDefaults(true).save();
	}
	
	public void reloadPlugin() {
		for(FileManager.Config c : FileManager.configs.values()) {
			c.reload();
		}
	}
	
	private void setupMySQL() {
		host = getConfig().getString("database.mysql.host");
		port = getConfig().getInt("database.mysql.port");
		database = getConfig().getString("database.mysql.database");
		username = getConfig().getString("database.mysql.username");
		password = getConfig().getString("database.mysql.password");
		
		try {
			synchronized(this) {
				if(getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, username, password));
				
				setupTables();
				this.getLogger().info("�aSuccesfully loaded MySQL");
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void setupTables() {
		String table1 = "CREATE TABLE IF NOT EXISTS " + this.playerTable + "(uuid VARCHAR(200), username VARCHAR(16))";
		String table2 = "CREATE TABLE IF NOT EXISTS " + this.deathchestTable + "(uuid VARCHAR(200), owner VARCHAR(16), location VARCHAR(64))";
		try {
			PreparedStatement playerStmt = connection.prepareStatement(table1);
			PreparedStatement deathchestsStmt = connection.prepareStatement(table2);
			
			playerStmt.executeUpdate();
			deathchestsStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@EventHandler
	public void onDatabaseLoad(DatabaseLoadEvent event) {
		headAPI = new HeadDatabaseAPI();
		try {
			ItemStack item = headAPI.getItemHead("23282");
			getLogger().log(Level.INFO, headAPI.getItemID(item));
		} catch (NullPointerException e) {
			getLogger().log(Level.SEVERE, "Could not find the head you were looking for.");
		}
	}
	
	private void registerCommands() {		
		registerCustomCommands();
		
		if(usingDc) {
			getCommand("deathchests").setExecutor(new DeathChestCMD());
		}
		
		if (usingRanks)
			getCommand("rank").setExecutor(new RankCMD());
		getCommand("core").setExecutor(new CoreCMD());
		getCommand("items").setExecutor(new ItemsCMD());
	}

	private void registerCustomCommands() {
		try {
			Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class,
					Plugin.class);
			constructor.setAccessible(true);
			Field field = SimplePluginManager.class.getDeclaredField("commandMap");
			field.setAccessible(true);
			CommandMap commandMap = (CommandMap) field.get((SimplePluginManager) getServer().getPluginManager());
			for (CustomCommand ccmd : commands.getCommands()) {
				PluginCommand pluginCommand = constructor.newInstance(ccmd.getCmd(), this);
				pluginCommand.setAliases(ccmd.getAliases());
				pluginCommand.setPermission(ccmd.getPerm());
				pluginCommand.setPermissionMessage(Lang.NO_PERMISSION);
				commandMap.register(getDescription().getName(), pluginCommand);
				getCommand(ccmd.getCmd()).setExecutor(new CustomCMD());
//				System.out.println("loaded custom command: " + ccmd.getCmd());
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new CraftingRecipes(), this);
		pm.registerEvents(new PlayerEvents(), this);
		pm.registerEvents(this, this);
		if(usingChat)
			pm.registerEvents(new ChatEvent(), this);
		if(usingDc) {
			pm.registerEvents(new DeathChestListener(), this);
			pm.registerEvents(new DeathChestsGUI(), this);
		}
	}

	private boolean setupEconomy() {
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private void registerPlaceholders() {
		PlaceholderAPI.registerPlaceholderHook("core", new PlaceholderHook() {
			@Override
			public String onRequest(OfflinePlayer player, String params) {
				if (player != null && player.isOnline()) {
					return onPlaceholderRequest(player.getPlayer(), params);
				}
				return null;
			}

			@Override
			public String onPlaceholderRequest(Player player, String params) {
				if (player == null) {
					return null;
				}
//				if (usingRanks) {
//					if (params.equalsIgnoreCase("rank_prefix")) {
//						return getRanks().getPRank(player).getPrefix();
//					}
//
//					if (params.equalsIgnoreCase("rank_suffix")) {
//						return getRanks().getPRank(player).getSuffix();
//					}
//
//					if (params.equalsIgnoreCase("rank")) {
//						return getRanks().getPRank(player).getName();
//					}
//				}
				return null;
			}

		});
	}

	public void setupConfigurations() {		
		if(usingMySQL) {
			this.getLogger().info("Enabling Mysql database");
			setupMySQL();
		}
		
		if (usingChat) {
			this.getLogger().info("Enabling Chat");
			chatFormat.loadChatFormats();
		}

		if (usingRanks) {
			this.getLogger().info("Enabling Ranks");
			rankMan.loadRanks();
		}

		if (usingSb) {
			this.getLogger().info("Enabling Scoreboard");
			Settings.setupSbVarialbes();
		}

		if (usingDc) {
			this.getLogger().info("Enabling DeathChests");
			Settings.setupDcVariables();
		}
		
		commands.loadCustomCommands();
		cItemMan.loadCustomItems();
	}

	public static List<Material> disabledRecipes = new ArrayList<>();

	private void disableRecipes() {
		int amount = 0;
		for (String val : getConfig().getStringList("disable-recipes")) {
			try {
				Material mat = Material.getMaterial(val);
				disabledRecipes.add(mat);
				amount++;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		this.getLogger().info("Disabled " + amount + " recipe(s)");
		
	}

	public void onDisable() {
		if(usingDc && DeathChestManager.getInstance().getDeathChestsByUUID().values() != null)
			DeathChestManager.getInstance().clearDeathChests();
		
		this.getLogger().info("Has been disabled v" + this.getDescription().getVersion());
	}

	public Economy getEconomy() {
		return econ;
	}

	public RankManager getRanks() {
		return rankMan;
	}

	public CustomCommands getCommands() {
		return commands;
	}

	public Scoreboard getScoreboard() {
		return sb;
	}
	
	public HeadDatabaseAPI getHeadAPI() {
		return headAPI;
	}
	
	public FileManager getFileManager() {
		return fileManager;
	}
	
	public CItemManager getItemManager() {
		return cItemMan;
	}
	
	public ChatFormats getChatFormat() {
		return chatFormat;
	}
}
