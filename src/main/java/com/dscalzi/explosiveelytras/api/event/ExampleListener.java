/*
 * ExplosiveElytras
 * Copyright (C) 2017-2018 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.api.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Demonstrates usage of the API.
 * 
 * @author Daniel D. Scalzi
 *
 * @since 0.10.0
 */
public class ExampleListener implements Listener {

    private final Map<UUID, ExplosiveImpactEvent> EVENT_CACHE = new HashMap<UUID, ExplosiveImpactEvent>();

    @EventHandler
    public void onExplosiveImpact(ExplosiveImpactEvent e) {

        // Do not consume items with a certain lore.
        List<ItemStack> consumedItems = e.getConsumedItems();
        for (int i = 0; i < consumedItems.size(); i++) {
            ItemStack target = consumedItems.get(i);
            if (target.hasItemMeta()) {
                ItemMeta targetMeta = target.getItemMeta();
                if (targetMeta.hasLore()) {
                    loreLoop: for (final String s : targetMeta.getLore()) {
                        if (s.equals("Ancient Protection")) {

                            // Do not consume items with Ancient Protection.
                            consumedItems.remove(i);
                            break loreLoop;

                        }
                    }
                }
            }
        }

        // Do not deal damage if the player has armor with
        // a certain lore.
        final Player p = e.getPlayer();
        itemLoop: for (ItemStack i : p.getInventory().getArmorContents()) {
            if (i.hasItemMeta()) {
                ItemMeta targetMeta = i.getItemMeta();
                if (targetMeta.hasLore()) {
                    for (final String s : targetMeta.getLore()) {
                        if (s.equals("Guardian Blessing")) {

                            // Do not do damage to players with a Guardian Blessing.
                            // We cache this event because damage can only be edited
                            // through the ExplosiveDamageEvent.
                            EVENT_CACHE.put(p.getUniqueId(), e);
                            break itemLoop;

                        }
                    }
                }
            }
        }

    }

    @EventHandler
    public void onExplosiveDamage(ExplosiveDamageEvent e) {

        final Player p = e.getPlayer();
        if (EVENT_CACHE.containsKey(p.getUniqueId())) {

            // Cached events do not do damage. See above method.
            e.setExplosionDamage(0);
            e.setImpactDamage(0);

            EVENT_CACHE.remove(p.getUniqueId());

        }

    }

}
