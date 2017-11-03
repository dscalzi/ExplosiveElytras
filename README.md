![Logo](http://i.imgur.com/PQAl7si.png)

[![](http://ci.aventiumsoftworks.com/jenkins/job/ExplosiveElytras/badge/icon)](http://ci.aventiumsoftworks.com/jenkins/job/ExplosiveElytras/) [![](https://img.shields.io/badge/license-MIT-blue.svg)](https://bitbucket.org/AventiumSoftworks/explosiveelytras/src/393f97e61735ed5fe712a9232de8bd264c1f9e01/src/com/dscalzi/explosiveelytras/resources/License.txt?at=master&fileviewer=file-view-default) ![](https://img.shields.io/badge/Spigot-1.9--1.12-orange.svg) ![](https://img.shields.io/badge/Java-8+-ec2025.svg) [![](https://discordapp.com/api/guilds/211524927831015424/widget.png)](https://discordapp.com/invite/MkmRnhd)

ExplosiveElytras is a light-weight plugin built using the Spigot API. This plugin causes explosions to occur when a player impacts an object at a high velocity. These explosions only occur if the player has a certain item in their inventory, such as tnt. This plugin is highly configurable, allowing you to customize the conditions which trigger explosions.

These explosions can only be triggered if a player takes damage. This means that explosions can only occur if a player is in survival mode. Vertical impacts only cause explosions if fall damage is enabled as well.

A Developer API is provided and actively maintained. More details on this are provided below.

---

#Feature List

* Allow high speed elytra collisions to cause explosions.
* Configure which items a player must have in order to explode on impact.
    * Configure minimum required amounts for each of those items.
    * Choose whether or not the matching required item should be consumed on explosion.
* Configure the explosion power per required item.
* Enable/disable an explosion multiplier. If more required items are found in a player's inventory the explosion will be larger.
    * Configurable limit for the maximum explosion power.
* Configure details about the explosion itself.
    * If blocks should be destroyed.
    * If fire should be spread.
* Configurable death message when a player dies in an explosive collision.
* Configurable values for both vertical and horizontal impacts:
    * Set a minimum damage value required to trigger explosions.
* Enable/disable fireworks on explosion.
* Metrics by [bStats](https://bstats.org/plugin/bukkit/ExplosiveElytras)
* WorldGuard Integration
* Developer API with Maven and Gradle support.

You can find more extensive details on the [wiki](https://bitbucket.org/AventiumSoftworks/explosiveelytras/wiki/).

---

#Building and Contributing

If you would like to contribute to ExplosiveElytras, feel free to submit a pull request. The project does not use a specific code style, however please keep to the conventions used throughout the code.

To build this project you will need [Maven](https://maven.apache.org/), or an IDE which supports it, and to run the following command:

```shell
mvn clean install
```

---

#Developer API

If you want to hook explosive elytras into your own plugin or simply want to extend functionality, you may use the provided API. The api is currently in its infancy, therefore if you feel anthing is missing or should be changed, please [let us know](https://bitbucket.org/AventiumSoftworks/explosiveelytras/issues?status=new&status=open).

**Download Latest**: [![bintray](https://api.bintray.com/packages/dscalzi/maven/ExplosiveElytras/images/download.svg)](https://bintray.com/dscalzi/maven/ExplosiveElytras/_latestVersion)

*Javadocs are not hosted, however they are provided on the maven repository.*

###Maven

```XML
<repository>
    <id>jcenter</id>
    <name>jcenter-bintray</name>
    <url>http://jcenter.bintray.com</url>
</repository>

<dependency>
  <groupId>com.dscalzi</groupId>
  <artifactId>ExplosiveElytras</artifactId>
  <version>VERSION</version>
</dependency>
```

###Gradle

```gradle

repositories {
    jcenter()
}

dependencies {
    compile 'com.dscalzi:ExplosiveElytras:VERSION'
}
```

###Example Usage

```java
/**
* Example listener.
* 
* This listener will parse through the items consumed by the
* explosive collision. Any items with the lore "Ancient Protection"
* will be removed from the consumed list and will therefore be
* preserved.
*/
@EventHandler
public void onExplosiveImpact(ExplosiveImpactEvent e) {
	List<ItemStack> consumedItems = e.getConsumedItems();
	itemLoop:
	for(int i=0; i<consumedItems.size(); i++) {
		ItemStack target = consumedItems.get(i);
		if(target.hasItemMeta()) {
			ItemMeta targetMeta = target.getItemMeta();
			if(targetMeta.hasLore()) {
				for(final String s : targetMeta.getLore()) {
					if(s.equals("Ancient Protection")) {
						consumedItems.remove(i);
						continue itemLoop;
					}
				}
			}
		}
	}
}
```

---

#Links

* [Spigot Resource Page](https://www.spigotmc.org/resources/explosiveelytras.43493/)
* [Dev Bukkit Page](https://dev.bukkit.org/projects/explosiveelytras)
* [Suggest Features or Report Bugs](https://bitbucket.org/AventiumSoftworks/explosiveelytras/issues)