![Logo](http://i.imgur.com/PQAl7si.png)

[![](http://ci.aventiumsoftworks.com/jenkins/job/ExplosiveElytras/badge/icon)](http://ci.aventiumsoftworks.com/jenkins/job/ExplosiveElytras/) [![](https://img.shields.io/badge/license-MIT-blue.svg)](https://bitbucket.org/AventiumSoftworks/explosiveelytras/src/393f97e61735ed5fe712a9232de8bd264c1f9e01/src/com/dscalzi/explosiveelytras/resources/License.txt?at=master&fileviewer=file-view-default) ![](https://img.shields.io/badge/Spigot-1.9--1.12-orange.svg) ![](https://img.shields.io/badge/Java-8+-ec2025.svg)

ExplosiveElytras is a light-weight plugin built using the Spigot API. This plugin causes explosions to occur when a player impacts an object at a high velocity. These explosions only occur if the player has a certain item in their inventory, such as tnt. This plugin is highly configurable, allowing you to customize the conditions which trigger explosions.

These explosions can only be triggered if a player takes damage. This means that explosions can only occur if a player is in survival mode. Vertical impacts only cause explosions if fall damage is enabled as well.

---

#Feature List

* Allow high speed elytra collisions to cause explosions.
* Configure which items a player must have in order to explode on impact.
    * Configure minimum required amounts for each of those items.
    * Choose whether or not the matching required item should be consumed on explosion.
* Configure the explosion power per required item.
* Enable/disable an explosion multiplier. If more required items are found in a player's inventory the explosion will be larger.
    * Configurable limit for the maximum explosion power.
* Configurable death message when a player dies in an explosive collision.
* Configurable values for both vertical and horizontal impacts:
    * Set a minimum damage value required to trigger explosions.
* Enable/disable fireworks on explosion.
* Metrics by [bStats](https://bstats.org/plugin/bukkit/ExplosiveElytras)
* WorldGuard Integration

You can find more extensive details on the [wiki](https://bitbucket.org/AventiumSoftworks/explosiveelytras/wiki/).

---

#Links

* [Spigot Resource Page](https://www.spigotmc.org/resources/explosiveelytras.43493/)
* [Dev Bukkit Page](https://dev.bukkit.org/projects/explosiveelytras)
* [Suggest Features or Report Bugs](https://bitbucket.org/AventiumSoftworks/explosiveelytras/issues)