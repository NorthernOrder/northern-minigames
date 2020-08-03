package io.github.northernorder.minigames.common

import io.github.northernorder.minigames.Minigames
import io.github.northernorder.minigames.utils.MCPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

abstract class Command(val name: String) : CommandExecutor, TabCompleter {
    fun isNotPlayer(sender: CommandSender): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only usable by players")
            return true
        }
        return false
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (Minigames.currentMinigame?.running == true) {
            sender.sendMessage("Cannot use plugin commands while a game is running")
            return true
        }
        return command(sender, command, label, args)
    }

    abstract fun command(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean

    fun getCurrentMinigame(player: MCPlayer?): Minigame<*, *>? {
        if (Minigames.currentMinigame == null) {
            player?.sendMessage("No minigame selected")
            return null
        }
        return Minigames.currentMinigame
    }

    fun isMapSelected(player: MCPlayer?): Boolean {
        if (Minigames.currentMinigame?.currentMap == null) {
            player?.sendMessage("No map selected")
            return false
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return mutableListOf()
    }
}