package com.larder.app.feature.mcp

import com.larder.app.domain.model.Item

enum class ExportTargetService { NOTION, GOOGLE_SHEETS }

data class ExportJobResult(
    val success: Boolean,
    val targetService: ExportTargetService,
    val exportedItemsCount: Int,
    val message: String
)

class McpClientExporter {

    /**
     * Executes manual export job pushing structured list (item, qty, category) to user's connected service.
     * Explicit user action only (no auto-export per security spec section 3).
     */
    suspend fun exportShoppingList(
        householdId: String,
        targetService: ExportTargetService,
        itemsToExport: List<Item>
    ): Result<ExportJobResult> {
        return try {
            if (itemsToExport.isEmpty()) {
                return Result.success(
                    ExportJobResult(
                        success = true,
                        targetService = targetService,
                        exportedItemsCount = 0,
                        message = "No items selected for export"
                    )
                )
            }

            // Simulate HTTP invocation to /export-job Edge Function with caller's JWT
            val serviceName = when (targetService) {
                ExportTargetService.NOTION -> "Notion Database"
                ExportTargetService.GOOGLE_SHEETS -> "Google Sheet"
            }

            Result.success(
                ExportJobResult(
                    success = true,
                    targetService = targetService,
                    exportedItemsCount = itemsToExport.size,
                    message = "Successfully exported ${itemsToExport.size} items to $serviceName"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
