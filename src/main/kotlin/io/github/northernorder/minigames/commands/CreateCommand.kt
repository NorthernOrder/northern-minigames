package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CreateCommand : Command("create") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true

        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true

        minigame.createMap()
        Bukkit.broadcastMessage(String.format("Creating a new map for %s", minigame.name))
        return true
    }
}