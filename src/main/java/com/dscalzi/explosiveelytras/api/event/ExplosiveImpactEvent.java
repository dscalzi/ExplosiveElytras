/*
 * ExplosiveElytras
 * Copyright (C) 2017-2019 Daniel D. Scalzi
 * See LICENSE for license information.
 */
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
 * Event thrown when a player explosively collides with an object. This event
 * can be used to modify properties of the collision or cancel it all together.
 * This event is called before the explosion is created, allowing you to also
 * modify the properties of the explosion. Following the explosion, an
 * {@link ExplosiveDamageEvent} is dispatched. That event deals with applying
 * damage to the player immediately after the explosive impact.
 * 
 * @author Daniel D. Scalzi
 * 
 * @since 0.8.0
 */
public class ExplosiveImpactEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    // Data
    private final Player player;
    private final Location location;
    private final ImpactType impactType;

    private boolean cancelled = false;
    private boolean setFire;
    private boolean breakBlocks;
    private List<ItemStack> consumedItems;
    private Firework firework;
    private float explosionPower;
    private double impactDamage;

    /**
     * Create an ExplosiveImpactEvent
     * 
     * @param player
     *            The player involved in the collision.
     * @param impactType
     *            The impact type.
     * @param setFire
     *            If the explosion will set fire.
     * @param breakBlocks
     *            If the explosion will break blocks.
     * @param consumedItems
     *            The items which will be consumed by the explosion.
     * @param firework
     *            The firework which will be detonated by this event.
     * @param impactDamage
     *            The damage value caused by the impact.
     * @param explosionPower
     *            The power of the resulting explosion.
     * 
     * @since 0.8.0
     */
    public ExplosiveImpactEvent(Player player, ImpactType impactType, boolean setFire, boolean breakBlocks,
            List<ItemStack> consumedItems, Firework firework, double impactDamage, float explosionPower) {
        super();
        this.player = player;
        this.impactType = impactType;
        this.location = player.getLocation();
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
        this.consumedItems = consumedItems;
        this.firework = firework;
        this.explosionPower = explosionPower;
        this.impactDamage = impactDamage;
    }

    /**
     * This method will return the power yield of the explosion caused by the
     * collision. The power of a single TNT explosion is 4, for reference.
     * 
     * @return The power yield of the explosion.
     * 
     * @since 0.8.0
     */
    public float getExplosionPower() {
        return explosionPower;
    }

    /**
     * This method will modify the yield of the explosion caused by the collision.
     * The power of a single TNT explosion is 4, for reference.
     * 
     * @param explosionPower
     *            The new power yield of the explosion.
     * 
     * @since 0.8.0
     */
    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
    }

    /**
     * This method will return if this event has a non-null value for the firework
     * property. A call to this method is effectively the same as checking: <br>
     * <br>
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
     * This method will return the firework object which will be detonated after the
     * collision. The value returned by this method may be <code>null</code>.
     * 
     * @return Possibly <code>null</code> firework object which will be detonated
     *         after the collision.
     * 
     * @since 0.8.0
     */
    public Firework getFirework() {
        return firework;
    }

    /**
     * This method allows you to set the firework which will be detonated after the
     * collision. If you do not want a firework to be detonated, you may set the
     * value to <code>null</code>. Please note that the firework will only be
     * detonated if the configuration option <code>fireworks_on_explosion</code> is
     * set to true.
     * 
     * @param firework
     *            The firework object that will be detonated after the collision.
     *            Accepts null values.
     * 
     * @since 0.8.0
     */
    public void setFirework(Firework firework) {
        this.firework = firework;
    }

    /**
     * Get a preview of the impact's damage value. This is the damage value caused
     * <strong>ONLY</strong> by the impact. Its value will be propagated to an
     * {@link ExplosiveDamageEvent} where it will be combined with the explosion
     * damage value and applied to the player.
     * 
     * @return The damage value caused by the impact.
     * 
     * @since 0.8.0
     */
    public double getImpactDamage() {
        return this.impactDamage;
    }

    /**
     * Set the new damage value caused by the impact <strong>only</strong>. This new
     * value will be propagated to the {@link ExplosiveDamageEvent}, where it will
     * be combined with the explosion damage and applied to the player.
     * 
     * @param damage
     *            The new damage value of the impact.
     * 
     * @since 0.8.0
     */
    public void setImpactDamage(double damage) {
        this.impactDamage = damage;
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
     * @param setFire
     *            If true, the explosion will set fire.
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
     * @param breakBlocks
     *            If true, the explosion will break blocks.
     * 
     * @since 0.8.0
     */
    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    /**
     * Get a list of the items which will be consumed by this explosion. Note that
     * these items will only be removed from the player's inventory if the
     * configuration option <code>consume_on_explosion</code> is set to true.
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
     * will not change the final power of the explosion, it will only change which
     * items will be removed from the player's inventory. Note that these items will
     * only be removed from the player's inventory if the configuration option
     * <code>consume_on_explosion</code> is set to true.
     * 
     * @param consumedItems
     *            A list containing the items to be consumed by the explosion.
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
     * Get the location of the collision. This is the location on which the
     * explosion will be detonated.
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
     * Change the cancelled status of this event. If the event is set to cancelled,
     * the explosive collision and all effects related to it will be cancelled.
     * 
     * @param cancel
     *            True to cancel the event, false to uncancel.
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
         * This impact type represents when a player impacts a surface vertically (like
         * the ground, or the top of a block) while gliding.
         * 
         * @since 0.8.0
         */
        VERTICAL(),
        /**
         * This impact type represents when a player impacts a surface horizontally
         * (like a wall, or the side of a block) while gliding.
         * 
         * @since 0.8.0
         */
        HORIZONTAL();

    }
}
