package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.Minigames
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Spawner(var location: Location, var type: Money, private var game: BedWars, private var team: Team? = null) :
    Runnable {
    private var time = 2
    private var tier = 1
    private var runs = 0

    init {
        if (team != null) setTeamTime() else setTime()
    }

    override fun run() {
        if (!game.running) return
        if (team != null) {
            setTeamTime()
        } else {
            setTier()
            setTime()
        }
        drop()
        schedule()
    }

    fun drop() {
        if (type == Money.EMERALD && team != null && team!!.upgrades.forge < 3) return
        val material: Material = when (type) {
            Money.IRON -> Material.IRON_INGOT
            Money.GOLD -> Material.GOLD_INGOT
            Money.EMERALD -> Material.EMERALD
            Money.DIAMOND -> Material.DIAMOND
        }
        game.world.dropItem(location, ItemStack(material))
    }

    private fun setTier() {
        if (tier == 3) return
        when (type) {
            Money.DIAMOND -> {
                if (tier == 1 && runs == 10) {
                    tier++
                } else if (tier == 2 && runs == 45) {
                    tier++
                }
            }
            Money.EMERALD -> {
                if (tier == 1 && runs == 6) {
                    tier++
                } else if (tier == 2 && runs == 15) {
                    tier++
                }
            }
            else -> {
                return; }
        }
    }

    private fun setTime() {
        time = when (type) {
            Money.DIAMOND -> calculateTime(20 * 30, tier - 1)
            Money.EMERALD -> calculateTime(20 * 50, tier - 1)
            else -> 2
        }
    }

    private fun setTeamTime() {
        time = when (type) {
            Money.IRON -> calculateTime((20 * 0.5).toInt(), team!!.upgrades.forge)
            Money.GOLD -> calculateTime(20 * 10, team!!.upgrades.forge)
            Money.EMERALD -> calculateTime(20 * 60, team!!.upgrades.forge)
            else -> 2
        }
    }

    fun schedule() {
        runs++
        Bukkit.getScheduler().scheduleSyncDelayedTask(Minigames.instance, this, time.toLong())
    }

    private fun calculateTime(defaultTime: Int, tier: Int): Int {
        return (defaultTime * (1 - 0.1 * tier)).toInt()
    }
}