/*
 * ExplosiveElytras
 * Copyright (C) 2017-2019 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.internal;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.dscalzi.explosiveelytras.internal.managers.ConfigManager;
import com.dscalzi.explosiveelytras.internal.managers.MessageManager;

public class ExplosiveElytrasPlugin extends JavaPlugin {

    @SuppressWarnings("unused")
    private MetricsLite metrics;

    @Override
    public void onEnable() {
        ConfigManager.initialize(this);
        MessageManager.initialize(this);
        if (usingWorldGuard()) {
            this.getLogger().info("WorldGuard found, enabling support.");
        }
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
        this.getCommand("explosiveelytras").setExecutor(new MainExecutor());
        metrics = new MetricsLite(this);
    }

    public boolean usingWorldGuard() {
        Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
        if(wg == null) {
            return false;
        }
        String version = wg.getDescription().getVersion();
        try {
            final int wgMajor = Integer.parseInt(version.split("\\.")[0]);
            if(wgMajor >= 7){
                return true;
            } else {
                getLogger().warning("ExplosiveElytras only supports WorldGuard 7+, you are running version " + version);
                getLogger().warning("WorldGuard integration will be unavailable.");
                getLogger().warning("Please upgrade your server or revert to ExplosiveElytras v0.10.2");
                return false;
            }
        } catch (NumberFormatException e) {
            getLogger().severe("WorldGuard Version " + wg.getDescription().getVersion());
            getLogger().severe("FAILED TO READ WORLDGUARD VERSION, SHUTTING DOWN!");
            this.getPluginLoader().disablePlugin(this);
            return true;
        }
    }

}
