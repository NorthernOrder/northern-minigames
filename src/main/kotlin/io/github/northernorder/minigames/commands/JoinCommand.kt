package io.github.northernorder.minigames.commands

import io.github.northernorder.minigames.common.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player as MCPlayer

class JoinCommand : Command("join") {
    override fun command(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (isNotPlayer(sender)) return true

        val player = sender as MCPlayer

        val minigame = getCurrentMinigame(player) ?: return true

        if (isMapSelected(player)) {
            minigame.join(player, args)
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val minigame = getCurrentMinigame(null) ?: return mutableListOf()
        return minigame.joinArgs(args)
    }
}