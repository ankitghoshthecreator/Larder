package com.larder.app.data.remote.supabase

import java.io.File
import java.util.UUID

data class SignedStoragePath(
    val rawStoragePath: String,
    val signedUrl: String,
    val expiresAtMillis: Long
)

class StorageUploader {

    suspend fun uploadReceiptImage(
        householdId: String,
        imageFile: File
    ): Result<SignedStoragePath> {
        return try {
            val fileName = "receipt_${householdId}_${UUID.randomUUID()}.jpg"
            val storagePath = "${SupabaseConfig.BUCKET_RECEIPTS}/$householdId/$fileName"
            val now = System.currentTimeMillis()
            val expiresAt = now + (SupabaseConfig.SIGNED_URL_EXPIRES_IN_SECONDS * 1000L)

            val signedUrl = "${SupabaseConfig.supabaseUrl}/storage/v1/object/sign/$storagePath?token=signed_demo_token"

            Result.success(
                SignedStoragePath(
                    rawStoragePath = storagePath,
                    signedUrl = signedUrl,
                    expiresAtMillis = expiresAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadShelfImage(
        householdId: String,
        imageFile: File
    ): Result<SignedStoragePath> {
        return try {
            val fileName = "shelf_${householdId}_${UUID.randomUUID()}.jpg"
            val storagePath = "${SupabaseConfig.BUCKET_SHELF}/$householdId/$fileName"
            val now = System.currentTimeMillis()
            val expiresAt = now + (SupabaseConfig.SIGNED_URL_EXPIRES_IN_SECONDS * 1000L)

            val signedUrl = "${SupabaseConfig.supabaseUrl}/storage/v1/object/sign/$storagePath?token=signed_demo_token"

            Result.success(
                SignedStoragePath(
                    rawStoragePath = storagePath,
                    signedUrl = signedUrl,
                    expiresAtMillis = expiresAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
