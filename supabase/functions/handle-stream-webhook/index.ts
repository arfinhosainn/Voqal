import pako from "npm:pako@2";

Deno.serve(async (req) => {
  try {
    const bytes = new Uint8Array(await req.arrayBuffer());
    const isGzip = (req.headers.get("content-encoding") || "").includes("gzip");
    const text = isGzip ? pako.ungzip(bytes, { to: "string" }) : new TextDecoder().decode(bytes);
    const event = JSON.parse(text);
    const roomId = event.call_cid?.split(":")[1];
    if (!roomId) return new Response("OK", { status: 200 });

    const url = Deno.env.get("SUPABASE_URL")!;
    const key = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
    const h = { "Content-Type": "application/json", "apikey": key, "Authorization": `Bearer ${key}` };

    switch (event.type) {
      case "call.session_participant_left": {
        const uid = event.participant?.user?.id;
        if (!uid) break;
        await fetch(
          `${url}/rest/v1/room_sessions?room_id=eq.${roomId}&user_id=eq.${uid}&left_at=is.null`,
          { method: "PATCH", headers: h, body: JSON.stringify({ left_at: new Date().toISOString() }) },
        );
        break;
      }
      case "call.session_ended":
      case "call.ended": {
        await fetch(
          `${url}/rest/v1/room_sessions?room_id=eq.${roomId}&left_at=is.null`,
          { method: "PATCH", headers: h, body: JSON.stringify({ left_at: new Date().toISOString() }) },
        );
        await fetch(`${url}/rest/v1/rooms?id=eq.${roomId}`, { method: "DELETE", headers: h });
        break;
      }
    }
  } catch (e) {
    console.error("Error:", e);
  }
  return new Response("OK", { status: 200 });
});
