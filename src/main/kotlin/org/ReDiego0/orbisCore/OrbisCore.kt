package org.ReDiego0.orbisCore

import org.ReDiego0.orbisCore.combat.CombatListener
import org.ReDiego0.orbisCore.commands.OrbisCommand
import org.ReDiego0.orbisCore.config.ClassRegistry
import org.ReDiego0.orbisCore.ether.EtherTask
import org.ReDiego0.orbisCore.instances.InstanceManager
import org.ReDiego0.orbisCore.instances.InstanceRegistry
import org.ReDiego0.orbisCore.items.ItemProvider
import org.ReDiego0.orbisCore.party.PartyManager
import org.ReDiego0.orbisCore.player.MobLootListener
import org.ReDiego0.orbisCore.player.OrbisPapiExpansion
import org.ReDiego0.orbisCore.player.PlayerListener
import org.ReDiego0.orbisCore.player.PlayerManager
import org.ReDiego0.orbisCore.skills.SkillManager
import org.ReDiego0.orbisCore.skills.implemented.FlechaPerforante
import org.ReDiego0.orbisCore.skills.implemented.GolpeSismico
import org.ReDiego0.orbisCore.skills.implemented.RayoArcano
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class OrbisCore : JavaPlugin() {

    lateinit var playerManager: PlayerManager
        private set
    lateinit var classRegistry: ClassRegistry
        private set
    lateinit var itemProvider: ItemProvider
        private set
    lateinit var instanceRegistry: InstanceRegistry
        private set
    lateinit var partyManager: PartyManager
        private set
    lateinit var instanceManager: InstanceManager
        private set
    lateinit var skillManager: SkillManager
        private set

    override fun onEnable() {
        logger.info("Iniciando OrbisCore...")

        classRegistry = ClassRegistry(this)
        classRegistry.loadClasses()

        skillManager = SkillManager(this)

        // Registro de skills
        // Vanguardia
        skillManager.registerSkill(GolpeSismico(this))

        // Tejedor
        skillManager.registerSkill((RayoArcano(this)))

        // Cazador
        skillManager.registerSkill(FlechaPerforante(this))

        skillManager.reloadSkillsConfig()

        itemProvider = ItemProvider(this)

        instanceRegistry = InstanceRegistry(this)
        instanceRegistry.loadInstances()

        playerManager = PlayerManager(this)
        server.pluginManager.registerEvents(PlayerListener(playerManager), this)

        server.pluginManager.registerEvents(CombatListener(this), this)
        server.pluginManager.registerEvents(MobLootListener(this), this)

        getCommand("orbis")?.setExecutor(OrbisCommand(this))

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            OrbisPapiExpansion(this).register()
        }

        EtherTask(this).runTaskTimer(this, 0L, 20L)

        logger.info("&eOrbisCore cargado correctamente.")
    }

    override fun onDisable() {
        Bukkit.getScheduler().cancelTasks(this)

        if (::playerManager.isInitialized) {
            playerManager.saveAll()
        }
    }
}