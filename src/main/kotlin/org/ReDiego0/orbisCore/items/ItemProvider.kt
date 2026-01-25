package org.ReDiego0.orbisCore.items

import dev.lone.itemsadder.api.CustomStack
import org.ReDiego0.orbisCore.OrbisCore
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemProvider(private val plugin: OrbisCore) {

    private val hookItemsAdder: Boolean by lazy {
        plugin.server.pluginManager.isPluginEnabled("ItemsAdder")
    }

    fun getItem(id: String, amount: Int = 1): ItemStack? {
        if (hookItemsAdder) {
            val iaItem = getItemsAdderStack(id)
            if (iaItem != null) {
                iaItem.amount = amount
                return iaItem
            }
        }

        val material = Material.matchMaterial(id)
        if (material != null) {
            return ItemStack(material, amount)
        }

        plugin.logger.warning("ItemProvider: No se encontró el ítem con ID '$id'.")
        return null
    }

    private fun getItemsAdderStack(id: String): ItemStack? {
        return try {
            CustomStack.getInstance(id)?.itemStack
        } catch (e: NoClassDefFoundError) {
            null
        } catch (e: Exception) {
            null
        }
    }
}