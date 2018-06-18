/*
 * ExplosiveElytras
 * Copyright (C) 2017-2018 Daniel D. Scalzi
 * See LICENSE for license information.
 */
package com.dscalzi.explosiveelytras.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event thrown immediately after an explosive impact occurs and right before
 * the damage values are applied to the player.
 * 
 * @author Daniel D. Scalzi
 *
 * @since 0.10.0
 */
public class ExplosiveDamageEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private Player player;
    private double explosionDamage;
    private double impactDamage;
    private ExplosiveImpactEvent origin;

    public ExplosiveDamageEvent(Player player, double explosionDamage, double impactDamage,
            ExplosiveImpactEvent origin) {
        this.player = player;
        this.explosionDamage = explosionDamage;
        this.impactDamage = impactDamage;
        this.origin = origin;
    }

    /**
     * Get the player involved in the collision. This player is the target of the
     * damage.
     * 
     * @return The player involved in the collision who is the target of the damage.
     * 
     * @since 0.10.0
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Get the damage value caused by the explosion following the collision
     * <strong>only</strong>.
     * 
     * @return The damage value caused by the explosion following the collision.
     * 
     * @since 0.10.0
     */
    public double getExplosionDamage() {
        return this.explosionDamage;
    }

    /**
     * Set the damage value of the explosion following the collision.
     * 
     * @param explosionDamage
     *            The new damage value caused by the explosion.
     * 
     * @since 0.10.0
     */
    public void setExplosionDamage(double explosionDamage) {
        this.explosionDamage = explosionDamage;
    }

    /**
     * Get the damage value caused by the impact <strong>only</strong>.
     * 
     * @return The damage value caused by the impact.
     * 
     * @since 0.10.0
     */
    public double getImpactDamage() {
        return this.impactDamage;
    }

    /**
     * Set the damage value of the impact.
     * 
     * @param impactDamage
     *            The new damage value of the impact.
     * 
     * @since 0.10.0
     */
    public void setImpactDamage(double impactDamage) {
        this.impactDamage = impactDamage;
    }

    /**
     * Get the effective damage which will be applied to the player. This is
     * equivalent to {@link #getImpactDamage() getImpactDamage()} +
     * {@link #getExplosionDamage() getExplosionDamage()}.
     * 
     * @return The effective damage which will be applied to the player.
     * 
     * @since 0.10.0
     */
    public double getEffectiveDamage() {
        return this.explosionDamage + this.impactDamage;
    }

    /**
     * Returns a snapshot of the ExplosiveImpactEvent which triggered this event.
     * This value should not be modified, as it will have no effect at this point.
     * The object is provided simply to give assurances to API developers.
     * 
     * @return The ExplosiveImpactEvent which triggered this event.
     * 
     * @since 0.10.0
     */
    public ExplosiveImpactEvent getOrigin() {
        return this.origin;
    }

    /**
     * Get if this event has been canceled by another handler.
     * 
     * @return True if cancelled, otherwise false.
     * 
     * @since 0.10.0
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Change the cancelled status of this event. If the event is set to cancelled,
     * the damage will not be applied to the player.
     * 
     * @param cancel
     *            True to cancel the event, false to uncancel.
     * 
     * @since 0.10.0
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = true;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
