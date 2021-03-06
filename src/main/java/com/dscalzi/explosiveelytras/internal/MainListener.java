/*
 * ExplosiveElytras
 * Copyright (C) 2017-2019 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;

import com.dscalzi.explosiveelytras.api.event.ExplosiveDamageEvent;
import com.dscalzi.explosiveelytras.api.event.ExplosiveImpactEvent;
import com.dscalzi.explosiveelytras.api.event.ExplosiveImpactEvent.ImpactType;
import com.dscalzi.explosiveelytras.internal.managers.ConfigManager;

public class MainListener implements Listener {

    private final ExplosiveElytrasPlugin plugin;
    private final ConfigManager cm;
    private Map<UUID, Long> cache = new HashMap<>();
    private Map<UUID, Pair<ExplosiveImpactEvent, Double>> damageCache = new HashMap<>();

    public MainListener(ExplosiveElytrasPlugin plugin) {
        cm = ConfigManager.getInstance();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCollision(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            // Permission Check
            if (!p.hasPermission("explosiveelytras.explode")) {
                return;
            }

            // Redirect damage from previous impact into explosion.
            // Assures our custom death message is sent.
            if (e.getCause() == DamageCause.BLOCK_EXPLOSION) {
                if (damageCache.containsKey(p.getUniqueId())) {
                    ExplosiveDamageEvent event = new ExplosiveDamageEvent(p, e.getFinalDamage(),
                            damageCache.get(p.getUniqueId()).getValue(), damageCache.get(p.getUniqueId()).getKey());
                    damageCache.remove(p.getUniqueId());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }
                    e.setDamage(event.getEffectiveDamage());
                }
                return;
            }

            // Check if the impact type is valid;
            ImpactType type = null;
            if (e.getCause() == DamageCause.FLY_INTO_WALL) {
                if (e.getFinalDamage() < cm.getMinHorizontalDamage()) {
                    return;
                }
                type = ImpactType.HORIZONTAL;
            } else if (e.getCause() == DamageCause.FALL && cache.containsKey(p.getUniqueId())
                    && (System.currentTimeMillis() - cache.get(p.getUniqueId())) < 200) {
                if (e.getFinalDamage() < cm.getMinVerticalDamage()) {
                    return;
                }
                type = ImpactType.VERTICAL;
            } else {
                return;
            }

            // Check to see if the player's current world
            // has elytra explosions enabled.
            String world = p.getWorld().getName();
            boolean allowed = false;
            for (final String s : cm.getAllowedWorlds()) {
                if (s.equalsIgnoreCase(world)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                return;
            }

            boolean breakBlocks = cm.breakBlocks();
            boolean setFire = cm.setFire();

            // Check WorldGuard protections
            // if the plugin is enabled.
            if (plugin.usingWorldGuard()) {
                // Worldguard only overrides the native value if breakBlocks
                // is enabled on our end.
                if (breakBlocks) {
                    breakBlocks = WorldGuardWrapper.breakBlocks(plugin, p);
                }
                if (WorldGuardWrapper.cancelExplosion(plugin, p)) {
                    return;
                }
            }

            final PlayerInventory inv = p.getInventory();
            final List<ItemStack> requirements = cm.getRequiredItems();
            List<ItemStack> consumed = new ArrayList<>();
            float finalPower = 0;
            if (requirements.size() > 0) {

                final float powerMultiplier = cm.getPowerPerItem();
                float maxPower = cm.explosionMultiplier() ? cm.getMaxPower() : powerMultiplier;
                List<ItemStack> matches = new ArrayList<>();

                // Check for matching required items
                // in player's inventory.
                for (ItemStack is : requirements) {
                    int amt = 0;
                    for (ItemStack i : inv.getContents()) {
                        if (i != null && i.isSimilar(is)) {
                            amt += i.getAmount();
                        }
                    }
                    // Check offhand
                    if (inv.getItemInOffHand() != null && inv.getItemInOffHand().isSimilar(is)
                            && inv.getItemInOffHand().getAmount() >= is.getAmount()) {
                        amt += inv.getItemInOffHand().getAmount();
                    }
                    // Check armor
                    for (ItemStack i : inv.getArmorContents()) {
                        if (i != null && i.isSimilar(is) && i.getAmount() >= is.getAmount()) {
                            amt += i.getAmount();
                        }
                    }
                    if (amt > 0) {
                        ItemStack match = new ItemStack(is);
                        match.setAmount(amt);
                        matches.add(match);
                    }
                }
                if (matches.size() == 0)
                    return;

                // Calculate the explosion power
                // based on the found required items.
                for (ItemStack i : matches) {
                    if (i.getAmount() * powerMultiplier > maxPower) {
                        int canUse = (int) ((maxPower - finalPower) / powerMultiplier);
                        finalPower = maxPower;
                        ItemStack partial = new ItemStack(i);
                        partial.setAmount(canUse);
                        consumed.add(partial);
                        break;
                    } else {
                        finalPower += (i.getAmount() * powerMultiplier);
                        consumed.add(i);
                    }
                }

            }

            // Check if the elytra should be consumed.
            if (cm.consumeElytra()) {
                if (inv.getChestplate() != null && inv.getChestplate().getType() == Material.ELYTRA) {
                    consumed.add(inv.getChestplate());
                }
            }

            // Create default firework.
            Firework fw = null;
            if (cm.fireworksOnExplosion()) {
                fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                FireworkMeta fm = fw.getFireworkMeta();
                fm.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL_LARGE).withColor(Color.RED)
                        .withFade(Color.BLACK).build());
                fm.setPower(0);
                fw.setFireworkMeta(fm);
            }
            ExplosiveImpactEvent event = new ExplosiveImpactEvent(p, type, setFire, breakBlocks, consumed, fw,
                    e.getFinalDamage(), finalPower);

            // Dispatch the event.
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            consumed = event.getConsumedItems();
            if (cm.consumeRequiredItems())
                consumed.forEach(i -> removeItem(i, inv));
            cache.put(p.getUniqueId(), System.currentTimeMillis());
            damageCache.put(p.getUniqueId(), new Pair<>(event, event.getImpactDamage()));
            p.getWorld().createExplosion(event.getLocation().getX(), event.getLocation().getY(),
                    event.getLocation().getZ(), event.getExplosionPower(), event.getSetFire(), event.getBreakBlocks());
            if (cm.fireworksOnExplosion() && event.hasFirework()) {
                event.getFirework().detonate();
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (!p.hasPermission("explosiveelytras.explode")) {
                return;
            }
            if (!e.isGliding()) {
                cache.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (!e.getEntity().hasPermission("explosiveelytras.explode")) {
            return;
        }
        if (cache.containsKey(e.getEntity().getUniqueId())) {
            if ((System.currentTimeMillis() - cache.get(e.getEntity().getUniqueId())) > 200) {
                cache.remove(e.getEntity().getUniqueId());
                return;
            }
            DamageCause dc = e.getEntity().getLastDamageCause().getCause();
            if (dc == DamageCause.BLOCK_EXPLOSION) {
                e.setDeathMessage(cm.getDeathMessage(e.getEntity().getName()));
            }
            cache.remove(e.getEntity().getUniqueId());
        }
    }

    private void removeItem(ItemStack item, PlayerInventory _inv) {
        int requestedDeletion = item.getAmount();

        for (Map.Entry<Integer, ? extends ItemStack> entry : _inv.all(item.getType()).entrySet()) {
            ItemStack i = entry.getValue();
            if (item.isSimilar(i)) {
                if (requestedDeletion >= i.getAmount()) {
                    requestedDeletion -= i.getAmount();

                    _inv.clear(entry.getKey());
                } else {
                    if (requestedDeletion <= 0)
                        break;

                    ItemStack rep = entry.getValue();
                    rep.setAmount(rep.getAmount() - requestedDeletion);
                    _inv.setItem(entry.getKey(), rep);

                    requestedDeletion = 0;

                    break;
                }
            }
        }
        if (requestedDeletion > 0) {
            if (_inv.getItemInOffHand().isSimilar(item)) {
                ItemStack reduced = _inv.getItemInOffHand();
                int reducedNumber = Math.max(0, reduced.getAmount() - requestedDeletion);
                requestedDeletion -= Math.max(0, item.getAmount());
                reduced.setAmount(reducedNumber);
                _inv.setItemInOffHand(reduced);
            }
        }
        if (requestedDeletion > 0) {
            ItemStack[] aC = _inv.getArmorContents();
            for (int z = 0; z < _inv.getArmorContents().length; ++z) {
                ItemStack i = _inv.getArmorContents()[z];
                if (i != null && i.isSimilar(item)) {
                    int reducedNumber = Math.max(0, i.getAmount() - requestedDeletion);
                    requestedDeletion -= Math.max(0, item.getAmount());
                    i.setAmount(reducedNumber);
                    aC[z] = i;
                    if (requestedDeletion <= 0)
                        break;
                }
                _inv.setArmorContents(aC);
            }
        }
    }

}
