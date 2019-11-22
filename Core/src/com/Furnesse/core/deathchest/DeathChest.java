package com.Furnesse.core.deathchest;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class DeathChest {

	private String uuid;
	private String owner;
	private Location loc;
	private ItemStack[] drops;

	public DeathChest(String uuid, String owner, Location loc, ItemStack[] drops) {
		this.uuid = uuid;
		this.owner = owner;
		this.loc = loc;
		this.drops = drops;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public ItemStack[] getDrops() {
		return drops;
	}

	public void setDrops(ItemStack[] drops) {
		this.drops = drops;
	}
}