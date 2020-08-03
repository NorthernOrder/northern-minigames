package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.Minigames
import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class SelectCommand : Command("select") {
    companion object {
        val types = mutableListOf("minigame", "map", "spawn")
    }

    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true

        val player = sender as MCPlayer

        // /select
        // /select unknown
        if (args.isEmpty() || !types.stream().anyMatch { type -> type == args[0] }) {
            player.sendMessage("Not a known selection type")
            return true
        }

        // /select minigame <name>
        if (args[0] == "minigame") {
            // /select minigame
            // /select minigame unknown
            if (args.size == 1 || !Minigames.minigames.keys.stream().anyMatch { minigame -> args[1] == minigame }) {
                player.sendMessage("Not a known minigame")
                return true
            }
            // /select minigame known
            val minigame = Minigames.minigames[args[1]]!!
            Minigames.currentMinigame = minigame
            Bukkit.broadcastMessage(String.format("%s minigame selected", minigame.name))
            return true
        }

        // /select map <name>
        if (args[0] == "map") {
            // /select map cool - no minigame
            if (args.size == 2 && getCurrentMinigame(player) == null) return true
            // /select map
            // /select map unknown
            if (args.size == 1 || !Minigames.currentMinigame!!.maps.keys.stream().anyMatch { map -> args[1] == map }) {
                player.sendMessage("Not a known map")
                return true
            }
            // /select map known
            val minigame = Minigames.currentMinigame!!
            minigame.loadMap(args[1])
            Bukkit.broadcastMessage(String.format("%s map selected", minigame.currentMap!!.name))
            return true
        }

        // /select spawn
        if (args[0] == "spawn") {
            Minigames.currentMinigame ?: return true
            Minigames.unloadCurrentMinigame()
            val spawn = Bukkit.getWorld("world")!!.spawnLocation
            Bukkit.getOnlinePlayers().forEach { p -> p.teleport(spawn.add(0.0, 1.0, 0.0)) }
            Bukkit.broadcastMessage("Returning to spawn")
            return true
        }

        // /select impossible stuff
        player.sendMessage("Unknown args")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        if (args.isEmpty() || !types.stream().anyMatch { type -> type == args[0] })
            return types
        else if (args[0] == "minigame")
            return Minigames.minigames.keys.toMutableList()
        else if (args[0] == "map")
            return if (Minigames.currentMinigame != null) Minigames.currentMinigame!!.maps.keys.toMutableList() else mutableListOf()
        return mutableListOf()
    }
}