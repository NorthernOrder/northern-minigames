package io.github.northernorder.minigames

import io.github.northernorder.minigames.commands.*
import io.github.northernorder.minigames.common.Command
import io.github.northernorder.minigames.common.Minigame
import io.github.northernorder.minigames.games.bedwars.BedWars
import io.github.northernorder.minigames.utils.FileUtils
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Minigames : JavaPlugin() {
    companion object {
        lateinit var instance: Minigames
        var currentMinigame: Minigame<*, *>? = null
        val minigames: MutableMap<String, Minigame<*, *>> = HashMap()

        fun runConsoleCommand(command: String) {
            instance.server.dispatchCommand(instance.server.consoleSender, command)
        }

        fun registerMinigame(minigame: Minigame<*, *>) {
            minigames[minigame.name] = minigame
            minigame.loadData()
        }

        fun registerCommand(command: Command) {
            val pluginCommand = instance.getCommand(command.name) ?: return
            pluginCommand.setExecutor(command)
        }

        fun registerListener(listener: Listener) {
            instance.server.pluginManager.registerEvents(listener, instance)
        }

        fun unloadCurrentMinigame() {
            currentMinigame?.unload()
            currentMinigame = null
        }
    }

    override fun onEnable() {
        instance = this
        FileUtils.setupPluginFolder()
        registerMinigames()
        registerCommands()
    }

    override fun onDisable() {
        // nothing to put here yet
    }

    private fun registerMinigames() {
        registerMinigame(BedWars())
    }

    private fun registerCommands() {
        registerCommand(CreateCommand())
        registerCommand(JoinCommand())
        registerCommand(LeaveCommand())
        registerCommand(ResetCommand())
        registerCommand(SaveCommand())
        registerCommand(SelectCommand())
        registerCommand(SetCommand())
        registerCommand(StartCommand())
        registerCommand(TestCommand())
    }
}