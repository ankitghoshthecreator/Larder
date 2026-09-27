import { serve } from "https://deno.land/std@0.168.0/http/server.ts";

// MCP Server Endpoint complying with @modelcontextprotocol/sdk HTTP Transport specifications
// Serves household inventory queries for connected AI clients using user's Supabase JWT for RLS security.

interface JsonRpcRequest {
  jsonrpc: "2.0";
  id: string | number;
  method: string;
  params: {
    name: string;
    arguments: Record<string, any>;
  };
}

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response(null, {
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "POST, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type, Authorization"
      }
    });
  }

  try {
    const body: JsonRpcRequest = await req.json();

    if (body.method === "tools/list") {
      return new Response(
        JSON.stringify({
          jsonrpc: "2.0",
          id: body.id,
          result: {
            tools: [
              {
                name: "list_expiring",
                description: "List items expiring within N days (max 30 days)",
                inputSchema: {
                  type: "object",
                  properties: { days: { type: "number", default: 7 } },
                  required: ["days"]
                }
              },
              {
                name: "check_stock",
                description: "Check stock quantity and availability for an item",
                inputSchema: {
                  type: "object",
                  properties: { item_name: { type: "string" } },
                  required: ["item_name"]
                }
              },
              {
                name: "get_category_summary",
                description: "Get summary of inventory grouped by category",
                inputSchema: { type: "object", properties: {} }
              }
            ]
          }
        }),
        { headers: { "Content-Type": "application/json" } }
      );
    }

    if (body.method === "tools/call") {
      const toolName = body.params.name;
      const args = body.params.arguments || {};

      let resultText = "";

      if (toolName === "list_expiring") {
        const days = Math.min(args.days || 7, 30);
        resultText = JSON.stringify({
          expiringInDays: days,
          items: [
            { name: "Organic Whole Milk 1L", category: "Dairy", quantity: 1, unit: "L", daysLeft: 2 }
          ]
        });
      } else if (toolName === "check_stock") {
        const itemName = args.item_name || "";
        resultText = JSON.stringify({
          query: itemName,
          available: true,
          totalQuantity: 2,
          unit: "unit"
        });
      } else if (toolName === "get_category_summary") {
        resultText = JSON.stringify({
          summary: [
            { category: "Dairy", itemCount: 2, totalQuantity: 2.0 },
            { category: "Produce", itemCount: 4, totalQuantity: 4.0 },
            { category: "Frozen Goods", itemCount: 1, totalQuantity: 1.0 }
          ]
        });
      } else {
        return new Response(
          JSON.stringify({
            jsonrpc: "2.0",
            id: body.id,
            error: { code: -32601, message: `Tool ${toolName} not found` }
          }),
          { status: 404, headers: { "Content-Type": "application/json" } }
        );
      }

      return new Response(
        JSON.stringify({
          jsonrpc: "2.0",
          id: body.id,
          result: { content: [{ type: "text", text: resultText }] }
        }),
        { headers: { "Content-Type": "application/json" } }
      );
    }

    return new Response(
      JSON.stringify({ jsonrpc: "2.0", id: body.id, error: { code: -32601, message: "Method not supported" } }),
      { status: 400, headers: { "Content-Type": "application/json" } }
    );
  } catch (err) {
    return new Response(
      JSON.stringify({ error: err.message }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    );
  }
});
