package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.command.CommandSender

class SetCommand : Command("set") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true
        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true
        if (!isMapSelected(player)) return true
        val mapLocations = minigame.mapLocations

        if (args.isEmpty() || !mapLocations.keys.stream().anyMatch { location -> location == args[0] }) {
            player.sendMessage("Not a known location")
            return true
        }

        val location = mapLocations[args[0]] ?: return true
        location(player.eyeLocation)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val minigame = getCurrentMinigame(null) ?: return super.onTabComplete(sender, command, alias, args)
        val mapLocations = minigame.mapLocations.keys.toMutableList()
        if (args.isEmpty() || !mapLocations.stream().anyMatch { location -> location == args[0] }) {
            return mapLocations
        }
        return mutableListOf()
    }
}