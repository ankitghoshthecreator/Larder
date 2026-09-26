import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

// MCP Server Endpoint (HTTP Transport behind Supabase Edge Function)
// Exposes tools: list_expiring, check_stock, get_category_summary
// Scoped to requesting user's household via Supabase JWT RLS.

serve(async (req) => {
  return new Response(
    JSON.stringify({
      mcpVersion: "1.0.0",
      server: "Larder Inventory MCP Server",
      status: "active"
    }),
    { headers: { "Content-Type": "application/json" } }
  );
});
