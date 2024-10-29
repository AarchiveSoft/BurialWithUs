package com.burialwithus

import net.botwithus.internal.scripts.ScriptDefinition
import net.botwithus.rs3.script.Execution
import net.botwithus.rs3.script.LoopingScript
import net.botwithus.rs3.script.config.ScriptConfig
import net.botwithus.rs3.game.movement.Movement
import net.botwithus.rs3.game.movement.TeleportData
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery
import net.botwithus.rs3.game.Coordinate
import net.botwithus.rs3.game.Distance
import net.botwithus.rs3.game.Client
import net.botwithus.rs3.util.RandomGenerator
import java.util.Random

class BurialArmorScript : LoopingScript(
    "Burial Armor Script",
    ScriptConfig("Burial Armor Script", true),  // Pass positional arguments
    ScriptDefinition(null, null, null) // Use null values if needed
) {
    internal val maxUpgradeLevels = mapOf(
        "iron" to 1,
        "steel" to 1,
        "mithril" to 2,
        "adamant" to 2,
        "rune" to 3,
        "necronium" to 4,
        "banite" to 4,
        "elder rune" to 5
    )

    internal var selectedBarType: String = "iron" // Default bar type
    internal var maxRandomDelay: Double = 1.0 // Default max delay in seconds
    internal var isScriptRunning = false
    internal val random = Random()

    // Coordinates for Artisan's Workshop and Draynor Lodestone
    private val workshopLocation = Coordinate(2440, 3161, 0)
    private val draynorLodestone = Coordinate(3105, 3298, 0)

    // Precalculate Draynor-to-Workshop distance
    private val draynorToWorkshopDistance = Distance.between(draynorLodestone, workshopLocation)

    override fun onLoop() {
        if (isScriptRunning) {
            if (navigateToWorkshop()) {
                selectRandomForgeAndSmelt()
                craftPlateArmor()
                upgradeArmorToMax()
                useRandomBurialAnvil()
            }
        }
        Execution.delay(500L) // Use Long type for delay
    }

    private fun navigateToWorkshop(): Boolean {
        val player = Client.getLocalPlayer() ?: return false
        val distanceToWorkshop = Distance.between(player.getCoordinate(), workshopLocation)

        return if (distanceToWorkshop > draynorToWorkshopDistance) {
            teleportToDraynor()
        } else {
            Movement.walkTo(workshopLocation.x, workshopLocation.y, true)
            true
        }
    }

    private fun teleportToDraynor(): Boolean {
        val teleport = TeleportData(TeleportData.LODESTONE, 0, draynorLodestone, "Draynor")
        return Client.getLocalPlayer()?.interact(teleport.optionName) ?: false
    }

    private fun selectRandomForgeAndSmelt() {
        val forge = SceneObjectQuery.newQuery().name("Forge").results().nearest()
        forge?.interact("Smelt")
        Execution.delayUntil(10000L) {
            ComponentQuery.newQuery(FORGE_INTERFACE_ID).results().size() > 0
        }
        applyRandomDelay()
        ComponentQuery.newQuery(FORGE_INTERFACE_ID).option("Smelt Bars").results().firstOrNull()?.interact()
        applyRandomDelay()
    }

    private fun craftPlateArmor() {
        val anvil = SceneObjectQuery.newQuery().name("Anvil").results().nearest()
        anvil?.interact("Forge")
        Execution.delayUntil(10000L) {
            ComponentQuery.newQuery(CRAFT_INTERFACE_ID).results().size() > 0
        }
        applyRandomDelay()
        ComponentQuery.newQuery(CRAFT_INTERFACE_ID).option("Craft $selectedBarType Plate Armor").results().firstOrNull()?.interact()
        applyRandomDelay()
    }

    private fun upgradeArmorToMax() {
        val maxLevel = maxUpgradeLevels[selectedBarType] ?: 1
        val anvil = SceneObjectQuery.newQuery().name("Anvil").results().nearest()
        for (i in 1..maxLevel) {
            anvil?.interact("Forge")
            Execution.delayUntil(10000L) {
                ComponentQuery.newQuery(CRAFT_INTERFACE_ID).results().size() > 0
            }
            applyRandomDelay()
            ComponentQuery.newQuery(CRAFT_INTERFACE_ID).option("Upgrade to $selectedBarType Plate Armor +$i").results().firstOrNull()?.interact()
            applyRandomDelay()
        }
    }

    private fun useRandomBurialAnvil() {
        val burialAnvil = SceneObjectQuery.newQuery().name("Burial Anvil").results().nearest()
        burialAnvil?.interact("Craft Burial Armor")
        Execution.delayUntil(10000L) {
            ComponentQuery.newQuery(BURIAL_INTERFACE_ID).results().size() > 0
        }
        applyRandomDelay()
        ComponentQuery.newQuery(BURIAL_INTERFACE_ID).option("Convert to Burial Armor").results().firstOrNull()?.interact()
        applyRandomDelay()
    }

    private fun applyRandomDelay() {
        val delay = RandomGenerator.nextDouble(0.0, maxRandomDelay)
        Execution.delay((delay * 1000).toLong())
    }

    // We use a custom GraphicsContext instead of displayMenu directly in this script
    init {
        this.sgc = BurialArmorGraphicsContext(this)
    }

    companion object {
        private const val FORGE_INTERFACE_ID = 678 // Replace with actual interface ID
        private const val CRAFT_INTERFACE_ID = 679 // Replace with actual interface ID
        private const val BURIAL_INTERFACE_ID = 680 // Replace with actual interface ID
    }
}
