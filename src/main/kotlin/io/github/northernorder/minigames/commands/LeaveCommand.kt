package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.command.CommandSender

class LeaveCommand : Command("leave") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true

        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true

        minigame.leave(player)
        return true
    }
}