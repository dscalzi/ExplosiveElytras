/*
 * ExplosiveElytras
 * Copyright (C) 2017-2018 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.managers;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.dscalzi.explosiveelytras.ExplosiveElytras;

public class ConfigManager {

	private static boolean initialized;
	private static ConfigManager instance;
	
	//TODO Will be implemented in a later version
	private final double configVersion = 1.0;
	private ExplosiveElytras plugin;
	private FileConfiguration config;
	
	private ConfigManager(ExplosiveElytras plugin){
		this.plugin = plugin;
		loadConfig();
	}
	
	public void loadConfig(){
    	verifyFile();
    	this.plugin.reloadConfig();
		this.config = this.plugin.getConfig(); 
    }
	
	public void verifyFile(){
    	File file = new File(this.plugin.getDataFolder(), "config.yml");
		if (!file.exists()){
			this.plugin.saveDefaultConfig();
		}
    }
	
	public static void initialize(ExplosiveElytras plugin){
		if(!initialized){
			instance = new ConfigManager(plugin);
			initialized = true;
		}
	}
	
	public static boolean reload(){
		if(!initialized) return false;
		try{
			getInstance().loadConfig();
			return true;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static ConfigManager getInstance(){
		return ConfigManager.instance;
	}
	
	/* Configuration Accessors */
	
	public String getDeathMessage(String name){
		String s = config.getString("general_settings.death_message", "{0} had an explosive landing");
		MessageFormat m = new MessageFormat(s);
		return ChatColor.translateAlternateColorCodes('&', m.format(new Object[]{name}));
	}
	
	public List<String> getAllowedWorlds(){
		return this.config.getStringList("general_settings.allowed_worlds");
	}
	
	public List<ItemStack> getRequiredItems(){
		@SuppressWarnings("unchecked")
		List<String> stuff = (List<String>) config.getList("general_settings.required_items", new ArrayList<String>());
		List<ItemStack> ret = new ArrayList<ItemStack>();
		for(String s : stuff){
			String[] parts = s.split("\\|");
			try {
				
				Material m = Material.valueOf(parts[0]);
				ItemStack i = new ItemStack(m, Integer.parseInt(parts[1]));
				ret.add(i);
				
			} catch (Exception e) {
				plugin.getLogger().severe("Unknown item in required items list: " + stuff);
			}
		}
		return ret;
	}
	
	public boolean explosionMultiplier(){
		return config.getBoolean("general_settings.explosion_multiplier.enabled", true);
	}
	
	public float getPowerPerItem(){
		return Float.parseFloat(config.getString("general_settings.power_per_item", "4.0"));
	}
	
	public float getMaxPower(){
		return Float.parseFloat(config.getString("general_settings.explosion_multiplier.max_power", "16.0"));
	}
	
	public boolean consumeRequiredItems(){
		return config.getBoolean("general_settings.consume_on_explosion", true);
	}
	
	public boolean consumeElytra() {
		return config.getBoolean("explosion_settings.consume_elytra", false);
	}
	
	public boolean breakBlocks() {
		return config.getBoolean("explosion_settings.break_blocks", true);
	}
	
	public boolean setFire() {
		return config.getBoolean("explosion_settings.set_fire", true);
	}
	
	public boolean fireworksOnExplosion(){
		return config.getBoolean("explosion_settings.fireworks", true);
	}
	
	public int getMinHorizontalDamage(){
		return config.getInt("horizontal_impact_settings.minimum_damage_value", 1);
	}
	
	public int getMinVerticalDamage(){
		return config.getInt("vertical_impact_settings.minimum_damage_value", 1);
	}
	
	public double getSystemConfigVersion(){
		return this.configVersion;
	}
	
	public double getConfigVersion(){
		return config.getDouble("ConfigVersion", getSystemConfigVersion());
	}
	
}
