package com.larder.app.data.remote.supabase

object SupabaseConfig {
    // Configurable Supabase credentials
    var supabaseUrl: String = "https://your-supabase-project.supabase.co"
    var supabaseAnonKey: String = "your-anon-key-here"

    // Default Storage Buckets
    const val BUCKET_RECEIPTS = "receipt-scans"
    const val BUCKET_SHELF = "shelf-photos"

    // Time-limited signed URL expiration (default: 60 minutes = 3600 seconds)
    const val SIGNED_URL_EXPIRES_IN_SECONDS = 3600

    fun isConfigured(): Boolean {
        return supabaseUrl != "https://your-supabase-project.supabase.co" && 
               supabaseAnonKey != "your-anon-key-here"
    }
}
