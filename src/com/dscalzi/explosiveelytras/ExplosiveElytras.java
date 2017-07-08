/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras;

import org.bstats.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;

import com.dscalzi.explosiveelytras.managers.ConfigManager;
import com.dscalzi.explosiveelytras.managers.MessageManager;

public class ExplosiveElytras extends JavaPlugin {
	
	@SuppressWarnings("unused")
	private MetricsLite metrics;
	private boolean usingWorldGuard;
	
	@Override
	public void onEnable(){
		ConfigManager.initialize(this);
		MessageManager.initialize(this);
		setupWorldGuard();
		getServer().getPluginManager().registerEvents(new MainListener(this), this);
		this.getCommand("explosiveelytras").setExecutor(new MainExecutor());
		metrics = new MetricsLite(this);
	}
	
	@Override
	public void onDisable(){
		
	}
	
	private void setupWorldGuard() {
		usingWorldGuard = getServer().getPluginManager().getPlugin("WorldGuard") != null;
		this.getLogger().info("WorldGuard found, enabling support.");
	}
	
	public boolean usingWorldGuard() {
		return usingWorldGuard;
	}
	
}
