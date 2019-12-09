package com.Furnesse.core.utils;

import org.bukkit.ChatColor;

import com.Furnesse.core.Core;

public class Lang {
	
	static Core plugin = Core.instance;
	
	public static String chat(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String msg(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public static String PREFIX = msg(plugin.getConfigs().getLangConfig().getString("prefix"));
	public static String NO_PERMISSION = PREFIX +  msg("" + plugin.getConfigs().getLangConfig().getString("no_permission"));
	public static String RELOADED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("reloaded"));
	// RANK
	public static String RANK_CREATED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_created"));
	public static String RANK_DELETED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_deleted"));
	public static String RANK_SETPREFIX = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_setprefix"));
	public static String RANK_SETSUFFIX = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_setsuffix"));
	public static String RANK_PERM_ADDED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_perm_added"));
	public static String RANK_PERM_REMOVED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.rank_perm_removed"));
	public static String USER_SET_RANK = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_set_rank"));
	public static String USER_ADDED_PERM = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_added_perm"));
	public static String USER_REMOVED_PERM = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_removed_perm"));
	public static String USER_RANK_RECEIVED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_rank_received"));
	public static String USER_PERM_RECEIVED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_perm_received"));
	public static String USER_PERM_REMOVED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("rank.user_perm_removed"));
	// DEATHCHEST
	public static String DEATHCOORDS = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathcoords"));
	public static String DEATHCHEST_EXPIRE_TIME = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathchest_expire_time"));
	public static String DEATHCHEST_DISSAPEARED = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathchest_disappeared"));
	public static String DEATHCHEST_CANNOT_BREAK = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathchest_cannot_break"));
	public static String DEATHCHEST_CANNOT_OPEN = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathchest_cannot_open"));
	public static String DEATHCHEST_FASTLOOT_COMPLETE = PREFIX + msg("" + plugin.getConfigs().getLangConfig().getString("deathchest.deathchest_fastloot_complete"));
}
