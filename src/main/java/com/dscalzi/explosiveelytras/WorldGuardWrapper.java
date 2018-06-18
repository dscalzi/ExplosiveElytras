/*
 * ExplosiveElytras
 * Copyright (C) 2017-2018 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras;

import java.util.Collection;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardWrapper {

    public static boolean breakBlocks(ExplosiveElytras plugin, Player p) {
        return !WGBukkit.getPlugin().getGlobalStateManager().get(p.getWorld()).blockTNTBlockDamage;
    }

    public static boolean cancelExplosion(ExplosiveElytras plugin, Player p) {
        boolean stopInTheNameOfWorldGuard = false;
        WorldGuardPlugin wg = WGBukkit.getPlugin();
        RegionManager rm = wg.getRegionManager(p.getWorld());
        if (rm != null) {
            boolean canBuild = rm.getApplicableRegions(p.getLocation()).testState(wg.wrapPlayer(p), DefaultFlag.BUILD);
            Collection<StateFlag.State> states = rm.getApplicableRegions(p.getLocation()).queryAllValues(null,
                    DefaultFlag.TNT);
            for (StateFlag.State s : states) {
                if (s == StateFlag.State.DENY) {
                    stopInTheNameOfWorldGuard = true;
                }
            }
            if (!canBuild && !p.hasPermission("explosiveelytras.bypass.worldguard")) {
                stopInTheNameOfWorldGuard = true;
            }
        }

        return stopInTheNameOfWorldGuard || wg.getGlobalStateManager().get(p.getWorld()).blockTNTExplosions;
    }

}
