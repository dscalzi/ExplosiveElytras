/*
 * ExplosiveElytras
 * Copyright (C) 2017-2019 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.internal;

import java.util.Collection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardWrapper {

    public static boolean breakBlocks(ExplosiveElytrasPlugin plugin, Player p) {
        return !WorldGuard.getInstance().getPlatform().getGlobalStateManager().get(BukkitAdapter.adapt(p.getWorld())).blockTNTBlockDamage;
    }

    public static boolean cancelExplosion(ExplosiveElytrasPlugin plugin, Player p) {
        boolean stopInTheNameOfWorldGuard = false;
        WorldGuardPlatform wg = WorldGuard.getInstance().getPlatform();
        RegionManager rm = wg.getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
        if (rm != null) {

            boolean canBuild = rm.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()))
                    .testState(WorldGuardPlugin.inst().wrapPlayer(p), Flags.BUILD);
            Collection<StateFlag.State> states = rm.getApplicableRegions(BukkitAdapter.asBlockVector(p.getLocation()))
                    .queryAllValues(null, Flags.TNT);
            for (StateFlag.State s : states) {
                if (s == StateFlag.State.DENY) {
                    stopInTheNameOfWorldGuard = true;
                    break;
                }
            }
            if (!canBuild && !p.hasPermission("explosiveelytras.bypass.worldguard")) {
                stopInTheNameOfWorldGuard = true;
            }
        }

        return stopInTheNameOfWorldGuard || wg.getGlobalStateManager().get(BukkitAdapter.adapt(p.getWorld())).blockTNTExplosions;
    }

}
