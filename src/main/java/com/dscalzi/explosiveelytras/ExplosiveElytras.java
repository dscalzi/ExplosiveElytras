/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

import com.dscalzi.explosiveelytras.managers.ConfigManager;
import com.dscalzi.explosiveelytras.managers.MessageManager;

public class ExplosiveElytras extends JavaPlugin {
	
	@SuppressWarnings("unused")
	private MetricsLite metrics;
	
	@Override
	public void onEnable(){
		ConfigManager.initialize(this);
		MessageManager.initialize(this);
		if(usingWorldGuard()) {
			this.getLogger().info("WorldGuard found, enabling support.");
		}
		getServer().getPluginManager().registerEvents(new MainListener(this), this);
		this.getCommand("explosiveelytras").setExecutor(new MainExecutor());
		metrics = new MetricsLite(this);
	}
	
	public boolean usingWorldGuard() {
		return getServer().getPluginManager().getPlugin("WorldGuard") != null;
	}
	
}
