package com.dscalzi.explosiveelytras.api.event;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Event thrown when a player explosively collides with an object.
 * This event can be used to modify properties of the collision or
 * cancel it all together. This event is called before the explosion
 * is created, allowing you to also modify the properties of the
 * explosion. The damage done by this event does not include damage caused
 * by the explosion. An event for this will be added in future versions.
 * 
 * @author Daniel D. Scalzi
 * 
 * @since 0.8.0
 */
public class ExplosiveImpactEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	
	//Data
	private final Player player;
	private final Location location;
	private final ImpactType impactType;
	
	private boolean cancelled = false;
	private boolean setFire;
	private boolean breakBlocks;
	private List<ItemStack> consumedItems;
	private Firework firework;
	@Deprecated
	private double impactDamage;
	private float explosionPower;
	
	private double finalDamage;

	/**
	 * Create an ExplosiveImpactEvent
	 * 
	 * @param player The player involved in the collision.
	 * @param impactType The impact type.
	 * @param setFire If the explosion will set fire.
	 * @param breakBlocks If the explosion will break blocks.
	 * @param consumedItems The items which will be consumed by the explosion.
	 * @param firework The firework which will be detonated by this event.
	 * @param impactDamage The final damage done to the player by the impact.
	 * @param explosionPower The power of the resulting explosion.
	 * 
	 * @since 0.8.0
	 */
	public ExplosiveImpactEvent(Player player, ImpactType impactType, boolean setFire, boolean breakBlocks, List<ItemStack> consumedItems, Firework firework, double impactDamage, float explosionPower) {
		super();
		this.player = player;
		this.impactType = impactType;
		this.location = player.getLocation();
		this.setFire = setFire;
		this.breakBlocks = breakBlocks;
		this.consumedItems = consumedItems;
		this.firework = firework;
		this.impactDamage = impactDamage;
		this.explosionPower = explosionPower;
		
		//Calculate Effective Damage
		this.finalDamage = impactDamage;
	}
	
	/**
	 * This method will return the power yield of the explosion caused by
	 * the collision. The power of a single TNT explosion is 4, for reference.
	 * 
	 * @return The power yield of the explosion.
	 * 
	 * @since 0.8.0
	 */
	public float getExplosionPower() {
		return explosionPower;
	}

	/**
	 * This method will modify the yield of the explosion caused by the
	 * collision. The power of a single TNT explosion is 4, for reference.
	 * 
	 * @param explosionPower The new power yield of the explosion.
	 * 
	 * @since 0.8.0
	 */
	public void setExplosionPower(float explosionPower) {
		this.explosionPower = explosionPower;
	}

	/**
	 * This method will return if this event has a non-null value for the firework
	 * property. A call to this method is effectively the same as checking:
	 * <br><br>
	 * <code> event.getFirework() != null</code>
	 * 
	 * @return True if the firework property is non-null, otherwise false.
	 * 
	 * @since 0.8.0
	 */
	public boolean hasFirework() {
		return this.firework != null;
	}
	
	/**
	 * This method will return the firework object which will be detonated after the collision.
	 * The value returned by this method may be <code>null</code>.
	 * 
	 * @return Possibly <code>null</code> firework object which will be detonated after the collision.
	 * 
	 * @since 0.8.0
	 */
	public Firework getFirework() {
		return firework;
	}
	
	/**
	 * This method allows you to set the firework which will be detonated after the collision.
	 * If you do not want a firework to be detonated, you may set the value to <code>null</code>.
	 * Please note that the firework will only be detonated if the configuration option
	 * <code>fireworks_on_explosion</code> is set to true.
	 * 
	 * @param firework The firework object that will be detonated after the collision. Accepts null values.
	 * 
	 * @since 0.8.0
	 */
	public void setFirework(Firework firework) {
		this.firework = firework;
	}
	
	/**
	 * This method will return the final damage done to the player as a result of <strong>ONLY</strong>
	 * the collision (impact).
	 * 
	 * @return The final damage done to the player as a result of the collision.
	 * 
	 * @since 0.8.0
	 * 
	 * @deprecated Use {@link #getFinalDamage() getFinalDamage()}.
	 * Sub-damage types will be removed from this event as the only damage accounted for will be that
	 * caused by the impact itself. A separate event will be triggered when the explosion occurs.<br>
	 * <strong>Will be removed in the next SEMVER MAJOR</strong>
	 */
	public double getImpactDamage() {
		return impactDamage;
	}

	/**
	 * Sets the impact damage. This will also change the final damage value.
	 * 
	 * @param impactDamage The new impact damage value.
	 * 
	 * @since 0.8.0
	 * 
	 * @deprecated Use {@link #setFinalDamage(double) setFinalDamage(double)}
	 * Sub-damage types will be removed from this event as the only damage accounted for will be that
	 * caused by the impact itself. A separate event will be triggered when the explosion occurs.<br>
	 * <strong>Will be removed in the next SEMVER MAJOR</strong>
	 */
	public void setImpactDamage(double impactDamage) {
		this.finalDamage -= this.impactDamage;
		this.impactDamage = impactDamage;
		this.finalDamage += impactDamage;
	}
	
	/**
	 * Get the final damage caused by the explosive impact. This damage originates from the
	 * impact <strong>ONLY</strong>.
	 * 
	 * @return The final damage done to the player due to the explosive impact.
	 * 
	 * @since 0.8.0
	 */
	public double getFinalDamage() {
		return this.finalDamage;
	}
	
	/**
	 * Set the final damage which is caused by the explosive impact. This value originates
	 * from the impact <strong>ONLY</strong>.
	 * 
	 * @param damage The new final damage value.
	 * 
	 * @since 0.8.0
	 */
	public void setFinalDamage(double damage) {
		this.finalDamage = damage;
	}
	
	/**
	 * Gets if the resulting explosion will set fire.
	 * 
	 * @return True if the explosion will set fire, otherwise false.
	 * 
	 * @since 0.9.0
	 */
	public boolean getSetFire() {
		return setFire;
	}

	/**
	 * Set if the resulting explosion should set fire.
	 * 
	 * @param setFire If true, the explosion will set fire.
	 * 
	 * @since 0.9.0
	 */
	public void setSetFire(boolean setFire) {
		this.setFire = setFire;
	}
	
	/**
	 * Gets if the resulting explosion will damage blocks.
	 * 
	 * @return True if the explosion will damage blocks, otherwise false.
	 * 
	 * @since 0.8.0
	 */
	public boolean getBreakBlocks() {
		return breakBlocks;
	}

	/**
	 * Set if the resulting explosion should break blocks.
	 * 
	 * @param breakBlocks If true, the explosion will break blocks.
	 * 
	 * @since 0.8.0
	 */
	public void setBreakBlocks(boolean breakBlocks) {
		this.breakBlocks = breakBlocks;
	}

	/**
	 * Get a list of the items which will be consumed by this explosion.
	 * Note that these items will only be removed from the player's inventory
	 * if the configuration option <code>consume_on_explosion</code> is set to
	 * true.
	 * 
	 * This list does not contain all of the matching required items, only the items
	 * which contribute to the final power of the explosion.
	 * 
	 * @return A list containing the items which will be consumed by the explosion.
	 * 
	 * @since 0.8.0
	 */
	public List<ItemStack> getConsumedItems() {
		return consumedItems;
	}

	/**
	 * Set the items which should be consumed by the explosion. Setting this value
	 * will not change the final power of the explosion, it will only change which items
	 * will be removed from the player's inventory. Note that these items will only be 
	 * removed from the player's inventory if the configuration option 
	 * <code>consume_on_explosion</code> is set to true.
	 * 
	 * @param consumedItems A list containing the items to be consumed by the explosion.
	 * 
	 * @since 0.8.0
	 */
	public void setConsumedItems(List<ItemStack> consumedItems) {
		this.consumedItems = consumedItems;
	}

	/**
	 * Get the player involved in the collision.
	 * 
	 * @return The player involved in the collision.
	 * 
	 * @since 0.8.0
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the type of impact which caused this explosion.
	 * 
	 * @return The impact type.
	 * 
	 * @since 0.8.0
	 */
	public ImpactType getImpactType() {
		return impactType;
	}

	/**
	 * Get the location of the collision. This is the location on which the explosion
	 * will be detonated.
	 * 
	 * @return The location of collision.
	 * 
	 * @since 0.8.0
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Get if this event has been canceled by another handler.
	 * 
	 * @return True if cancelled, otherwise false.
	 * 
	 * @since 0.8.0
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Change the cancelled status of this event. If the event is set to
	 * cancelled, the explosive collision and all effects related to it
	 * will be cancelled.
	 * 
	 * @param cancel True to cancel the event, false to uncancel.
	 * 
	 * @since 0.8.0
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
	
	@Override
	public HandlerList getHandlers() {
        return handlers;
    }
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	/**
	 * Enum to represent different types of impacts.
	 * 
	 * @author Daniel D. Scalzi
	 *
	 * @since 0.8.0
	 */
	public enum ImpactType {
		
		/**
		 * This impact type represents when a player impacts a surface
		 * vertically (like the ground, or the top of a block) while
		 * gliding.
		 * 
		 * @since 0.8.0
		 */
		VERTICAL(),
		/**
		 * This impact type represents when a player impacts a surface
		 * horizontally (like a wall, or the side of a block) while
		 * gliding.
		 * 
		 * @since 0.8.0
		 */
		HORIZONTAL();
		
	}
}
