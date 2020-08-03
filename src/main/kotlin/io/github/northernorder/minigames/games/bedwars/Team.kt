package io.github.northernorder.minigames.games.bedwars

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.northernorder.minigames.utils.Location


class Team(val color: Color) {
    @JsonIgnore
    val players = mutableListOf<Player>()

    @JsonIgnore
    val shop = TeamShop(this)
    var spawn: Location? = null
    var spawner: Location? = null

    @JsonIgnore
    var kills = 0

    @JsonIgnore
    var bedsBroken = 0

    @JsonIgnore
    var finalKills = 0

    @JsonIgnore
    var bedBroken = false

    @JsonIgnore
    var upgrades = Upgrades()

    data class Upgrades(
        var sharpness: Boolean = false,
        var protection: Int = 0,
        var forge: Int = 0,
        var heal: Boolean = false,
        var trap: Boolean = false
    ) {
        fun reset() {
            sharpness = false
            protection = 0
            forge = 0
            heal = false
            trap = false
        }
    }

    fun reset() {
        players.clear()
        shop.reset()
        upgrades.reset()
        kills = 0
        bedsBroken = 0
        finalKills = 0
        bedBroken = false
    }
}