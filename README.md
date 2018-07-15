![Logo](https://i.imgur.com/PQAl7si.png)

[<img src="https://ci.appveyor.com/api/projects/status/v982bn9k4lbgfu3s?retina=true" height="20.74px"></img>](https://ci.appveyor.com/project/dscalzi/explosiveelytras) [![](https://img.shields.io/github/license/dscalzi/ExplosiveElytras.svg)](https://github.com/dscalzi/ExplosiveElytras/blob/master/LICENSE) ![](https://img.shields.io/badge/Spigot-1.9.x--1.13.x-orange.svg) ![](https://img.shields.io/badge/Java-8+-ec2025.svg) [![](https://discordapp.com/api/guilds/211524927831015424/widget.png)](https://discordapp.com/invite/Fcrh6PT)

ExplosiveElytras is a light-weight plugin built using the Spigot API. This plugin causes explosions to occur when a player impacts an object at a high velocity. These explosions only occur if the player has a certain item in their inventory, such as tnt. This plugin is highly configurable, allowing you to customize the conditions which trigger explosions.

These explosions can only be triggered if a player takes damage. This means that explosions can only occur if a player is in survival mode. Vertical impacts only cause explosions if fall damage is enabled as well.

A Developer API is provided and actively maintained. More details on this are provided below.

---

# Feature List

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

You can find more extensive details on the [wiki](https://github.com/dscalzi/ExplosiveElytras/wiki).

---

# Building and Contributing

If you would like to contribute to ExplosiveElytras, feel free to submit a pull request. The project does not use a specific code style, however please keep to the conventions used throughout the code.

To build this project you will need [Maven](https://maven.apache.org/), or an IDE which supports it, and to run the following command:

```shell
mvn clean install
```

---

# Developer API

If you want to hook explosive elytras into your own plugin or simply want to extend functionality, you may use the provided API. The api is currently in its infancy, therefore if you feel anthing is missing or should be changed, please [let us know](https://github.com/dscalzi/ExplosiveElytras/issues).

**Download Latest**: [![bintray](https://api.bintray.com/packages/dscalzi/maven/ExplosiveElytras/images/download.svg)](https://bintray.com/dscalzi/maven/ExplosiveElytras/_latestVersion)

*Javadocs are not hosted, however they are provided on the maven repository.*

### Maven

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

### Gradle

```gradle

repositories {
    jcenter()
}

dependencies {
    compile 'com.dscalzi:ExplosiveElytras:VERSION'
}
```

### Example Usage

```java
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
```

---

# Links

* [Spigot Resource Page](https://www.spigotmc.org/resources/explosiveelytras.43493/)
* [Dev Bukkit Page](https://dev.bukkit.org/projects/explosiveelytras)
* [Suggest Features or Report Bugs](https://github.com/dscalzi/ExplosiveElytras/issues)