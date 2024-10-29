package com.burialwithus

import net.botwithus.rs3.imgui.ImGui
import net.botwithus.rs3.imgui.ImGuiWindowFlag
import net.botwithus.rs3.script.ScriptGraphicsContext

class BurialArmorGraphicsContext(
    private val script: BurialArmorScript
) : ScriptGraphicsContext(script.console) {

    override fun drawSettings() {
        if (ImGui.Begin("Burial Armor Crafting Script", ImGuiWindowFlag.None.value)) {
            ImGui.Text("Select Armor Type:")

            // Set up types array and selected index for Combo
            val typesArray = script.maxUpgradeLevels.keys.toTypedArray()
            var selectedIndex = typesArray.indexOf(script.selectedBarType)

            // Pass selectedIndex directly, and update script.selectedBarType afterward
            if (ImGui.Combo("Type", selectedIndex, *typesArray) == selectedIndex) {
                script.selectedBarType = typesArray[selectedIndex]
            }

            ImGui.Text("Set Max Random Delay (seconds):")

            // Scale maxRandomDelay for integer-only Slider
            var delayInt = (script.maxRandomDelay * 1000).toInt()

            // Use ImGui.Slider with an integer delayInt and update maxRandomDelay
            if (ImGui.Slider("Delay", delayInt, 0, 5000, 0) == delayInt) {
                script.maxRandomDelay = delayInt / 1000.0
            }

            // Start/Stop button toggle for the script
            if (ImGui.Button(if (script.isScriptRunning) "Stop" else "Start")) {
                script.isScriptRunning = !script.isScriptRunning
            }
            ImGui.End()
        }
    }
}
