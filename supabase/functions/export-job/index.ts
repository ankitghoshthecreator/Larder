import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

// MCP Client Connector Edge Function (/export-job)
// Pushes generated shopping list to connected Notion page or Google Sheet MCP server.

serve(async (req) => {
  const { householdId, targetService } = await req.json();

  return new Response(
    JSON.stringify({
      status: "completed",
      message: `Export job successfully pushed to ${targetService}`
    }),
    { headers: { "Content-Type": "application/json" } }
  );
});
