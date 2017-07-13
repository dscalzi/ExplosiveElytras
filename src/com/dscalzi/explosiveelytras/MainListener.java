/*
 * ExplosiveElytras
 * Copyright (C) 2017 Daniel D. Scalzi
 * See License.txt for license information.
 */
package com.dscalzi.explosiveelytras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
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

import com.dscalzi.explosiveelytras.managers.ConfigManager;

public class MainListener implements Listener{

	private final ExplosiveElytras plugin;
	private final ConfigManager cm;
	private Map<UUID, Long> cache = new HashMap<UUID, Long>();
	
	public MainListener(ExplosiveElytras plugin){
		cm = ConfigManager.getInstance();
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onCollision(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			if(!p.hasPermission("explosiveelytras.explode")) return;
			if(!(e.getCause() == DamageCause.FLY_INTO_WALL || (e.getCause() == DamageCause.FALL && cache.containsKey(p.getUniqueId()) && (System.currentTimeMillis()-cache.get(p.getUniqueId())) < 200))) return;
			
			boolean breakBlocks = true;
			
			if(plugin.usingWorldGuard()) {
				boolean stopInTheNameOfWorldGuard = false;
				com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = com.sk89q.worldguard.bukkit.WGBukkit.getPlugin();
				com.sk89q.worldguard.protection.managers.RegionManager rm = wg.getRegionManager(p.getWorld());
				if(rm != null) {
					boolean canBuild = rm.getApplicableRegions(p.getLocation()).testState(wg.wrapPlayer(p), com.sk89q.worldguard.protection.flags.DefaultFlag.BUILD);
					Collection<com.sk89q.worldguard.protection.flags.StateFlag.State> states = rm.getApplicableRegions(p.getLocation()).queryAllValues(null, com.sk89q.worldguard.protection.flags.DefaultFlag.TNT);
					for(com.sk89q.worldguard.protection.flags.StateFlag.State s : states) {
						if(s == com.sk89q.worldguard.protection.flags.StateFlag.State.DENY) {
							stopInTheNameOfWorldGuard = true;
						}
					}
					if(!canBuild && !p.hasPermission("explosiveelytras.override.worldguard")){
						stopInTheNameOfWorldGuard = true;
					}
				}
				breakBlocks = !wg.getGlobalStateManager().get(p.getWorld()).blockTNTBlockDamage;
				
				if(stopInTheNameOfWorldGuard || wg.getGlobalStateManager().get(p.getWorld()).blockTNTExplosions) {
					return;
				}
			}
			
			PlayerInventory inv = p.getInventory();
			List<ItemStack> requirements = cm.getRequiredItems();
			float powerMultiplier = cm.getPowerPerItem();
			float maxPower = cm.explosionMultiplier() ? cm.getMaxPower() : powerMultiplier;
			List<ItemStack> matches = new ArrayList<ItemStack>();
			List<ItemStack> consumed = new ArrayList<ItemStack>();
			float finalPower = 0;
			if(requirements.size() > 0){
				for(ItemStack is : requirements){
					int amt = 0;
					for(ItemStack i : inv.getContents())
						if(i != null && i.isSimilar(is))
							amt += i.getAmount();
					//Check offhand
					if(inv.getItemInOffHand() != null && inv.getItemInOffHand().isSimilar(is) && inv.getItemInOffHand().getAmount() >= is.getAmount())
						amt += inv.getItemInOffHand().getAmount();
					//Check armor
					for(ItemStack i : inv.getArmorContents())
						if(i != null && i.isSimilar(is) && i.getAmount() >= is.getAmount())
							amt += i.getAmount();
					if(amt > 0){
						ItemStack match = new ItemStack(is);
						match.setAmount(amt);
						matches.add(match);
					}
				}
				
				if(matches.size() == 0) return;
				
				for(ItemStack i : matches){
					if(i.getAmount()*powerMultiplier > maxPower){
						int canUse = (int)((maxPower-finalPower)/powerMultiplier);
						finalPower = maxPower;
						ItemStack partial = new ItemStack(i);
						partial.setAmount(canUse);
						consumed.add(partial);
						break;
					} else {
						finalPower += i.getAmount()/powerMultiplier;
						consumed.add(i);
					}
				}
				
			}
			if(e.getCause() == DamageCause.FLY_INTO_WALL){
				if(!shouldExplode(p)) return;
				if(e.getFinalDamage() < cm.getMinHorizontalDamage()) return;
				cache.put(p.getUniqueId(), System.currentTimeMillis());
				if(cm.consumeRequiredItems()) consumed.forEach(i -> removeItem(i, inv));
				p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), finalPower, true, breakBlocks);
				if(cm.fireworksOnExplosion()){
					Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
					FireworkMeta fm = fw.getFireworkMeta();
					fm.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL_LARGE).withColor(Color.RED).withFade(Color.BLACK).build());
					fm.setPower(0);
					fw.setFireworkMeta(fm);
					fw.detonate();
				}
				e.setCancelled(true);
			}
			if((e.getCause() == DamageCause.FALL && cache.containsKey(p.getUniqueId()) && (System.currentTimeMillis()-cache.get(p.getUniqueId())) < 200)){
				if(!shouldExplode(p)) return;
				if(e.getFinalDamage() < cm.getMinVerticalDamage()) return;
				cache.put(p.getUniqueId(), System.currentTimeMillis());
				if(cm.consumeRequiredItems()) consumed.forEach(i -> removeItem(i, inv));
				p.getWorld().createExplosion(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), finalPower, true, breakBlocks);
				if(cm.fireworksOnExplosion()){
					Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
					FireworkMeta fm = fw.getFireworkMeta();
					fm.addEffect(FireworkEffect.builder().flicker(false).with(Type.BALL_LARGE).withColor(Color.RED).withFade(Color.BLACK).build());
					fm.setPower(0);
					fw.setFireworkMeta(fm);
					fw.detonate();
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onGlide(EntityToggleGlideEvent e){
		if(e.getEntity() instanceof Player){
			Player p = (Player)e.getEntity();
			if(!p.hasPermission("explosiveelytras.explode")) return;
			if(!e.isGliding())
				cache.put((p).getUniqueId(), System.currentTimeMillis());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent e){
		if(!e.getEntity().hasPermission("explosiveelytras.explode")) return;
		if(cache.containsKey(e.getEntity().getUniqueId())){
			if((System.currentTimeMillis()-cache.get(e.getEntity().getUniqueId())) > 200){
				cache.remove(e.getEntity().getUniqueId());
				return;
			}
			DamageCause dc = e.getEntity().getLastDamageCause().getCause();
			if(dc == DamageCause.BLOCK_EXPLOSION){
				e.setDeathMessage(cm.getDeathMessage(e.getEntity().getName()));
			}
			cache.remove(e.getEntity().getUniqueId());
		}
	}
	
	private boolean shouldExplode(Player player){
		String world = player.getWorld().getName();
		return cm.getAllowedWorlds().contains(world);
	}
	
	private void removeItem(ItemStack item, PlayerInventory _inv) {
    	int requestedDeletion = item.getAmount();
    	    	
    	for(Map.Entry<Integer,? extends ItemStack> entry : _inv.all(item.getType()).entrySet()) {
    		ItemStack i = entry.getValue();
    		if(item.isSimilar(i)){
    			if(requestedDeletion>=i.getAmount()) {
    				requestedDeletion -= i.getAmount();
    				
    				_inv.clear(entry.getKey());
    			} else {
    				if(requestedDeletion <= 0) break;
    				
    				ItemStack rep = entry.getValue();
    				rep.setAmount(rep.getAmount()-requestedDeletion);
    				_inv.setItem(entry.getKey(), rep);
    				
    				requestedDeletion = 0;
    				
    				break;
    			}
    		}
    	}
    	if(requestedDeletion > 0){
    		if(_inv.getItemInOffHand().isSimilar(item)){
    			ItemStack reduced = _inv.getItemInOffHand();
    			int reducedNumber = reduced.getAmount()-requestedDeletion <= 0 ? 0 : reduced.getAmount()-requestedDeletion;
    			requestedDeletion -= item.getAmount() <= 0 ? 0 : item.getAmount();
    			reduced.setAmount(reducedNumber);
    			_inv.setItemInOffHand(reduced);
    		}
    	}
    	if(requestedDeletion > 0){
    		ItemStack[] aC = _inv.getArmorContents();
    		for(int z=0; z<_inv.getArmorContents().length; ++z){
    			ItemStack i = _inv.getArmorContents()[z];
    			if(i != null && i.isSimilar(item)){
    				int reducedNumber = i.getAmount()-requestedDeletion <= 0 ? 0 : i.getAmount()-requestedDeletion;
    				requestedDeletion -= item.getAmount() <= 0 ? 0 : item.getAmount();
    				i.setAmount(reducedNumber);
    				aC[z] = i;
    				if(requestedDeletion <= 0) break;
    			}
    			_inv.setArmorContents(aC);
    		}
    	}
    }
	
}
