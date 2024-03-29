package com.Furnesse.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.Furnesse.core.Core;

public class ItemUtil {
	static Core plugin = Core.instance;

	public static ItemStack getPlayerSkull(Player player, String title, List<String> lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		if (title != null) {
			meta.setDisplayName(title);
		}
		if (lore != null) {
			meta.setLore(convertLore(lore));
		}
		skull.setItemMeta((ItemMeta) meta);
		return skull;
	}

	public static ItemStack getPlayerSkull(OfflinePlayer player, String title, List<String> lore) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		meta.setDisplayName(title);
		meta.setLore(convertLore(lore));
		skull.setItemMeta((ItemMeta) meta);
		return skull;
	}

	public static int getInventorySizeBasedOnList(List<?> list) {
		int size = 9;
		while (list.size() > size && size != 54) {

			size += 9;
		}
		return size;
	}

	public static int getInventorySize(int i) {
		int size = 9;
		while (i > size && size != 54) {

			size += 9;
		}
		return size;
	}

	private static List<String> convertLore(List<String> lore) {
		List<String> newLore = new ArrayList<>();
		for (int i = 0; i < lore.size(); i++) {
			newLore.add(Lang.chat(lore.get(i)));
		}
		return newLore;
	}

	public static ItemStack loadItemFromConfig(String configName, String path) {
		Material m = Material
				.getMaterial(plugin.getFileManager().getConfig(configName).get().getString(path + ".material"));
		int confAmount = plugin.getFileManager().getConfig(configName).get().getInt(path + ".amount");

		int amount = 1;
		if (plugin.getFileManager().getConfig(configName).get().get(path + ".amount") != null) {
			amount = confAmount < 1 ? amount = 1 : confAmount;
		}

		ItemStack is = m != null ? new ItemStack(m)
				: plugin.getHeadAPI()
						.getItemHead(plugin.getFileManager().getConfig(configName).get().getString(path + ".material"));

		String displayName = plugin.getFileManager().getConfig(configName).get().getString(path + ".displayname");

		List<String> lore = plugin.getFileManager().getConfig(configName).get().getStringList(path + ".lore");

		List<String> enchantments = new ArrayList<>();
		if (plugin.getFileManager().getConfig(configName).get().getStringList(path + ".enchantments") != null) {
			enchantments = plugin.getFileManager().getConfig(configName).get().getStringList(path + ".enchantments");
		}

		boolean glowing = false;
		if (plugin.getFileManager().getConfig(configName).get().get(path + ".glowing") != null) {
			glowing = plugin.getFileManager().getConfig(configName).get().getBoolean(path + ".glowing");
		}

		boolean isUnbreakable = true;
		if (plugin.getFileManager().getConfig(configName).get().get(path + ".unbreakable") != null) {
			isUnbreakable = plugin.getFileManager().getConfig(configName).get().getBoolean(path + ".unbreakable");
		}

		int durability = 1;
		if (is.getItemMeta() instanceof Damageable) {
			durability = is.getDurability();
			if (plugin.getFileManager().getConfig(configName).get().get(path + ".durability") != null) {
				durability = plugin.getFileManager().getConfig(configName).get().getInt(path + ".durability");
			}
		}

//		if ((plugin.getFileManager().getConfig(configName).get().get(path + ".modeldata") != null)) {
//			int modelData = plugin.getFileManager().getConfig(configName).get().getInt(path + ".modeldata");
//			if (is.getItemMeta().hasCustomModelData()) {
//				is.getItemMeta().setCustomModelData(modelData);
//			} else {
//				is.getItemMeta().setCustomModelData(modelData);
//			}
//			Debug.Log("modeldata: " + is.getItemMeta().getCustomModelData());
//		}

		return create(is, amount, displayName, lore, enchantments, isUnbreakable, durability, glowing);
	}

	public static ItemStack create(ItemStack item, int amount, String displayName, List<String> lore,
			List<String> enchantments, boolean isUnbreakable, int durability, boolean glowing) {
		if (item == null)
			return null;

		item.setAmount(amount);

		ItemMeta meta = item.getItemMeta();
		if (displayName != null) {
			meta.setDisplayName(Lang.chat(displayName));
		}

		if (lore != null) {
			meta.setLore(convertLore(lore));
		}

		if (enchantments != null) {
			List<String> newLore = new ArrayList<>();
			for (String enchant : enchantments) {
				String enchantName = enchant.split(":")[0];
				int enchantLvl = Integer.valueOf(enchant.split(":")[1]);
				meta.addEnchant(ench(enchantName), enchantLvl, true);

				newLore.add(Lang.chat("&7" + enchantName + " " + enchantLvl));
				for (String str : meta.getLore()) {
					newLore.add(str);
				}
			}
			meta.setLore(convertLore(newLore));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		if (!isUnbreakable) {
			if (meta instanceof Damageable) {
				Damageable damage = (Damageable) meta;
				if (durability < 0) {
					damage.setHealth(((Damageable) meta).getHealth());
				} else {
					damage.setHealth(durability);
				}
			}

		} else {
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}

		if (glowing) {
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		item.setItemMeta(meta);
		return item;
	}

	public static Enchantment ench(String str) {
		Enchantment enchant = null;
		try {
			if (Enchantment.getByKey(NamespacedKey.minecraft(str)) != null) {
				enchant = Enchantment.getByKey(NamespacedKey.minecraft(str));
//				System.out.println("enchantment as string: " + str);
				return enchant;
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
