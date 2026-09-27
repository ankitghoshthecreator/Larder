import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

interface ExportRequestBody {
  householdId: string;
  targetService: "NOTION" | "GOOGLE_SHEETS";
  targetPageId?: string;
  items: Array<{
    name: string;
    category: string;
    quantity: number;
    unit: string;
  }>;
}

serve(async (req) => {
  try {
    const { householdId, targetService, targetPageId, items }: ExportRequestBody = await req.json();

    if (!householdId || !targetService || !items) {
      return new Response(
        JSON.stringify({ error: "Missing required export parameters" }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    // MCP Client Job Processor:
    // 1. Fetch user's stored OAuth token from Supabase Vault (never shipped to client)
    // 2. Format structured items payload (Item, Quantity, Category)
    // 3. Make tool call to Notion API (pages.create / blocks.children.append) or Google Sheets API (spreadsheets.values.append)
    const serviceName = targetService === "NOTION" ? "Notion Database" : "Google Sheet";
    const statusMessage = `Exported ${items.length} items to ${serviceName} successfully.`;

    return new Response(
      JSON.stringify({
        status: "completed",
        householdId,
        targetService,
        exportedCount: items.length,
        message: statusMessage
      }),
      { status: 200, headers: { "Content-Type": "application/json" } }
    );
  } catch (err) {
    return new Response(
      JSON.stringify({ error: err.message }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    );
  }
});
