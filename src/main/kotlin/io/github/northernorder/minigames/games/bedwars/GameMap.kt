package io.github.northernorder.minigames.games.bedwars

import io.github.northernorder.minigames.common.IGameMap
import io.github.northernorder.minigames.utils.Location
import java.util.stream.Collectors

data class GameMap(override var name: String? = null) : IGameMap {
    override var spectatorSpawn: Location? = null
    val spawners = mutableMapOf<String, Location>()
    val teams = Color.nameList.stream()
        .map { name -> name to Team(Color.fromName(name)!!) }
        .collect(Collectors.toList()).toMap()
}