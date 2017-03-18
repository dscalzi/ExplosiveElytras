/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras;

import org.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import com.dscalzi.explosiveelytras.managers.ConfigManager;
import com.dscalzi.explosiveelytras.managers.MessageManager;

public class ExplosiveElytras extends JavaPlugin{
	
	@SuppressWarnings("unused")
	private Metrics metrics;
	
	@Override
	public void onEnable(){
		ConfigManager.initialize(this);
		MessageManager.initialize(this);
		getServer().getPluginManager().registerEvents(new MainListener(), this);
		this.getCommand("explosiveelytras").setExecutor(new MainExecutor());
		metrics = new Metrics(this);
	}
	
	@Override
	public void onDisable(){
		
	}
	
}
