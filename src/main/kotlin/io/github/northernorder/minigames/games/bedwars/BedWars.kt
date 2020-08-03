package io.github.northernorder.minigames.games.bedwars

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import io.github.northernorder.minigames.Minigames
import io.github.northernorder.minigames.common.Minigame
import io.github.northernorder.minigames.utils.Countdown
import io.github.northernorder.minigames.utils.LocationUtils
import io.github.northernorder.minigames.utils.MCPlayer
import io.github.northernorder.minigames.utils.TextUtils
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerRespawnEvent

class BedWars : Minigame<GameMap, Player>("BedWars", GameMap::class) {
    companion object {
        val breakables = listOf(
            *Color.values().map { c -> c.bed }.toTypedArray(),
            *Color.values().map { c -> c.wool }.toTypedArray(),
            Material.END_STONE,
            Material.OBSIDIAN
        )
    }

    private val spawners = mutableListOf<Spawner>()
    private var finals = 0

    override fun mapLoadComplete() {
        setSpawners()
    }

    override fun afterStart() {
        players.values.forEach(this::afterPlayerSpawn)
        startSpawners()
    }

    override fun atEndOfGame() {
        for (player in players.values) {
            val mcPlayer = player.mcPlayer
            if (!player.dead) {
                Bukkit.broadcastMessage("${mcPlayer.name} won the game!")
            }
            mcPlayer.gameMode = GameMode.CREATIVE
            mcPlayer.sendMessage("Your stats:")
            mcPlayer.spigot().sendMessage(*ComponentBuilder("Kills: ${player.kills}").bold(true).create())
            mcPlayer.spigot().sendMessage(*ComponentBuilder("Beds destroyed: ${player.bedsBroken}").bold(true).create())
            mcPlayer.spigot().sendMessage(*ComponentBuilder("Final kills: ${player.finalKills}").bold(true).create())
        }
    }

    private fun startSpawners() {
        spawners.forEach { s -> s.schedule() }
    }

    override fun reset() {
        finals = 0
        setSpawners()
        currentMap!!.teams.forEach { (_, team) -> team.reset() }
        players.clear()
        unregisterListeners()
    }

    override fun join(mcPlayer: MCPlayer, args: Array<out String>) {
        if (args.isEmpty() || !currentMap!!.teams.keys.stream().anyMatch { t -> t == args[0] }) {
            mcPlayer.sendMessage("Unknown team color")
            return
        }

        val id = mcPlayer.uniqueId

        if (players.containsKey(id)) {
            mcPlayer.sendMessage("Already part of the next game")
            return
        }

        val team = currentMap!!.teams[args[0]] ?: return
        val player = Player(mcPlayer, team, team.spawn!!.toBukkit())
        team.players.add(player)
        players[id] = player

        mcPlayer.sendMessage("You will now be part of the next game")
        mcPlayer.gameMode = GameMode.SPECTATOR
    }

    override fun joinArgs(args: Array<out String>): MutableList<String> {
        if (args.isEmpty() || !currentMap!!.teams.keys.stream().anyMatch { t -> t == args[0] })
            return currentMap!!.teams.keys.toMutableList()
        return mutableListOf()
    }

    override fun leave(mcPlayer: MCPlayer) {
        val id = mcPlayer.uniqueId
        val player = players[id] ?: return
        player.team.players.remove(player)
        players.remove(id)
        mcPlayer.sendMessage("You are no longer part of the next game")
    }

    private fun mapSpawner(name: String): Pair<String, (location: Location) -> Unit> {
        return name to { location -> currentMap?.spawners?.set(name, LocationUtils.fromBukkit(location)) }
    }

    private fun baseSpawner(name: String): Pair<String, (location: Location) -> Unit> {
        val fullName = "spawner${TextUtils.toTitleCase(name)}"
        val team = currentMap?.teams?.get(name)
        return fullName to { location -> team?.spawner = LocationUtils.fromBukkit(location) }
    }

    private fun baseSpawn(name: String): Pair<String, (location: Location) -> Unit> {
        val fullName = "base${TextUtils.toTitleCase(name)}"
        val team = currentMap?.teams?.get(name)
        return fullName to { location -> team?.spawn = LocationUtils.fromBukkit(location) }
    }

    override fun setMapLocations() {
        mapLocations.putAll(mutableMapOf(
            *(1..5).map { i ->
                mapSpawner("emerald$i")
            }.toTypedArray(),
            *(1..5).map { i ->
                mapSpawner("diamond$i")
            }.toTypedArray(),
            *Color.nameList.map { name ->
                baseSpawn(name)
            }.toTypedArray(),
            *Color.nameList.map { name ->
                baseSpawner(name)
            }.toTypedArray(),
            "spectatorSpawn" to { location -> currentMap?.spectatorSpawn = LocationUtils.fromBukkit(location) }
        ))
    }

    override fun setTestables() {
        testables.putAll(mutableMapOf(
            *Color.nameList.map { name ->
                val base = "base${TextUtils.toTitleCase(name)}"
                val team = currentMap!!.teams[name]
                base to { player: MCPlayer -> player.teleport(team!!.spawn!!.toBukkit()) }
            }.toTypedArray(),
            "spawners" to { _ ->
                spawners.forEach { spawner -> spawner.drop() }
                true
            }
        ))
    }

    private fun setSpawners() {
        spawners.clear()
        currentMap!!.spawners.forEach { (name, location) ->
            spawners.add(Spawner(location.toBukkit(), Money.fromName(name.slice(0 until name.length - 1))!!, this))
        }
        currentMap!!.teams.values.forEach { t ->
            t.spawner ?: return@forEach
            spawners.add(Spawner(t.spawner!!.toBukkit(), Money.IRON, this, t))
            spawners.add(Spawner(t.spawner!!.toBukkit(), Money.GOLD, this, t))
            spawners.add(Spawner(t.spawner!!.toBukkit(), Money.EMERALD, this, t))
        }
    }

    override fun registerListeners() {
        Minigames.registerListener(this)
    }

    override fun unregisterListeners() {
        BlockBreakEvent.getHandlerList().unregister(this)
        PlayerDeathEvent.getHandlerList().unregister(this)
        PlayerRespawnEvent.getHandlerList().unregister(this)
        PlayerPostRespawnEvent.getHandlerList().unregister(this)
        InventoryClickEvent.getHandlerList().unregister(this)
        PlayerInteractEntityEvent.getHandlerList().unregister(this)
    }

    private fun afterPlayerSpawn(player: Player) {
        val mcPlayer = player.mcPlayer
        mcPlayer.teleport(player.team.spawn!!.toBukkit())
        mcPlayer.gameMode = GameMode.SURVIVAL
        player.giveDefaultInventory()
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        val block = e.block

        if (!breakables.any { t -> t == block.type }) {
            e.isCancelled = true
            return
        }

        var colorOfBed: Color? = null
        for (color in Color.values()) {
            if (block.type == color.bed) {
                colorOfBed = color
                break
            }
        }

        if (colorOfBed == null) return

        players.forEach { (_, player) ->
            if (player.team.color == colorOfBed) {
                val destroyer = e.player
                Bukkit.broadcastMessage("${TextUtils.toTitleCase(player.team.color.name)} team's bed has been destroyed by ${destroyer.name}!")
                val destroyerData: Player = players[destroyer.uniqueId] ?: return@forEach
                destroyerData.bedsBroken++
                destroyerData.team.bedsBroken++
                player.team.bedBroken = true
            }
        }
    }

    @EventHandler
    fun onKilled(e: PlayerDeathEvent) {
        val killed = e.entity
        val inv = killed.inventory
        val killedPlayer: Player = players[killed.uniqueId] ?: return
        val killer = killed.killer
        if (killer == null) {
            if (killedPlayer.team.bedBroken) {
                killedPlayer.dead = true
                finals++
            }
        } else {
            val contents = inv.contents
            for (item in contents) {
                if (item == null) continue
                val type = item.type
                if (Money.items.contains(type)) {
                    val cloned = item.clone()
                    killer.inventory.addItem(cloned)
                }
            }
            val killerPlayer: Player = players[killer.uniqueId] ?: return
            if (killedPlayer.team.bedBroken) {
                killedPlayer.dead = true
                killerPlayer.finalKills++
                killerPlayer.team.finalKills++
                finals++
            }
            killerPlayer.kills++
            killerPlayer.team.kills++
        }
        inv.clear()
        if (finals == players.size - 1) {
            endOfGame()
        }
    }

    @EventHandler
    fun onPreRespawn(e: PlayerRespawnEvent) {
        val mcPlayer = e.player
        e.respawnLocation = currentMap!!.spectatorSpawn!!.toBukkit()
        mcPlayer.gameMode = GameMode.SPECTATOR
    }

    @EventHandler
    fun onRespawn(e: PlayerPostRespawnEvent) {
        val mcPlayer = e.player
        val player = players[mcPlayer.uniqueId] ?: return
        if (player.dead) {
            return
        }
        Countdown("Respawning in", {
            afterPlayerSpawn(player)
        }, listOf(mcPlayer)).schedule()
    }

    @EventHandler
    fun onInventory(e: InventoryClickEvent) {
        val mcPlayer = e.whoClicked as MCPlayer
        val inventoryName = mcPlayer.openInventory.title
        if (!inventoryName.contains("Shop")) {
            return
        }
        val clicked = e.currentItem ?: return
        val player = players[mcPlayer.uniqueId] ?: return
        e.result = Event.Result.DENY
        val purchase = clicked.clone()
        if (inventoryName == "Item Shop") {
            player.shop.buy(purchase, mcPlayer)
        } else {
            player.team.shop.buy(purchase, mcPlayer)
        }
    }

    @EventHandler
    fun onVillagerInteract(e: PlayerInteractEntityEvent) {
        val clicked = e.rightClicked
        if (clicked.type != EntityType.VILLAGER) {
            return
        }
        e.isCancelled = true
        val mcPlayer = e.player
        val player = players[mcPlayer.uniqueId] ?: return
        if (clicked.customName == "Item Shop") {
            mcPlayer.openInventory(player.shop.inv)
        } else if (clicked.customName == "Upgrade Shop") {
            mcPlayer.openInventory(player.team.shop.inv)
        }
    }
}