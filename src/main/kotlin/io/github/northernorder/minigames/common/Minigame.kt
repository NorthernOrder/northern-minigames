package io.github.northernorder.minigames.common

import io.github.northernorder.minigames.utils.Countdown
import io.github.northernorder.minigames.utils.FileUtils
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.Listener
import java.nio.file.Paths
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class Minigame<M : IGameMap, P : BasePlayer>(val name: String, private val mapClass: KClass<M>) : Listener {
    val world: World = Bukkit.getWorld("world")!!
    val players: MutableMap<UUID, P> = mutableMapOf()
    val maps: MutableMap<String, M> = mutableMapOf()
    val mapLocations = mutableMapOf<String, (location: Location) -> Unit>()
    val testables = mutableMapOf<String, (player: MCPlayer) -> Any>()
    var currentMap: M? = null
    var running = false

    fun startGame() {
        if (players.entries.size < 2) return
        running = true
        registerListeners()
        Bukkit.broadcastMessage(String.format("A %s game is starting", name))
        for (player in Bukkit.getOnlinePlayers()) {
            player.inventory.clear()
            player.gameMode = GameMode.SPECTATOR
        }
        val playerList = players.values.stream().map { p -> p.mcPlayer }.collect(Collectors.toList())
        Countdown("Starting in", { afterStart() }, playerList).schedule()
    }

    abstract fun afterStart()

    fun endOfGame() {
        running = false
        atEndOfGame()
        reset()
    }

    abstract fun atEndOfGame()

    fun loadData() {
        val minigameFolder = FileUtils.getPluginFolderRelativePath("games", this.name)
        if (!minigameFolder.exists()) minigameFolder.mkdir()
        val mapsFolder = Paths.get(minigameFolder.path, "maps").toFile()
        if (!mapsFolder.exists()) mapsFolder.mkdir()
        val mapFiles = mapsFolder.listFiles() ?: return
        for (file in mapFiles) {
            val map = FileUtils.loadFile(file, mapClass.java) ?: continue
            maps[map.name!!] = map
        }
    }

    fun createMap(): Boolean {
        if (currentMap != null) return false
        currentMap = mapClass.createInstance()
        setTestables()
        setMapLocations()
        return true
    }

    fun loadMap(name: String) {
        val map = maps[name] ?: return
        currentMap = map
        setTestables()
        setMapLocations()
        mapLoadComplete()
    }

    abstract fun mapLoadComplete()

    fun saveMap(name: String? = null): Boolean {
        // error - no map is set
        if (currentMap == null) return false
        // error - no name when saving a new map
        if ((currentMap!!.name == null && name == null)) return false
        var shouldSave = false
        // save an already existing file
        if (currentMap!!.name != null && name == null) {
            shouldSave = true
        }
        // new file with a name
        if (currentMap!!.name == null && name != null) {
            shouldSave = true
            currentMap!!.name = name
        }

        if (!shouldSave) return false

        val path = FileUtils.getPluginFolderRelativePath(
            "games",
            this.name,
            "maps",
            String.format("%s.json", currentMap!!.name!!)
        )
        FileUtils.saveFile(currentMap, path)
        if (!maps.containsKey(currentMap!!.name)) maps[currentMap!!.name!!] = currentMap!!
        mapLoadComplete()

        return true
    }

    abstract fun reset()

    abstract fun join(mcPlayer: MCPlayer, args: Array<out String>)

    abstract fun joinArgs(args: Array<out String>): MutableList<String>

    abstract fun leave(mcPlayer: MCPlayer)

    fun unload() {
        reset()
        currentMap = null
    }

    abstract fun setMapLocations()
    abstract fun setTestables()

    abstract fun registerListeners()
    abstract fun unregisterListeners()
}