# Stream Video Token Generation Examples

This document provides examples for implementing token generation in your Supabase backend.

## Option 1: Using Supabase Edge Functions (Recommended)

Create an Edge Function that generates tokens:

```typescript
// supabase/functions/get-stream-token/index.ts
import { serve } from "https://deno.land/std@0.177.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  const authHeader = req.headers.get("authorization")!;
  const supabaseClient = createClient(
    Deno.env.get("SUPABASE_URL") ?? "",
    Deno.env.get("SUPABASE_ANON_KEY") ?? "",
    {
      global: {
        headers: { Authorization: authHeader },
      },
    }
  );

  // Get the authenticated user
  const {
    data: { user },
  } = await supabaseClient.auth.getUser();

  if (!user) {
    return new Response(
      JSON.stringify({ error: "Unauthorized" }),
      { status: 401, headers: corsHeaders }
    );
  }

  // Generate Stream Video token using your Stream API key and secret
  const streamApiKey = Deno.env.get("STREAM_API_KEY");
  const streamApiSecret = Deno.env.get("STREAM_API_SECRET");

  // You'll need to use a JWT library to sign the token
  // For Deno, you can use: https://github.com/timonson/djwt
  const payload = {
    user_id: user.id,
    iat: Math.floor(Date.now() / 1000),
    exp: Math.floor(Date.now() / 1000) + 3600, // 1 hour expiration
  };

  // Sign the JWT (this is a placeholder - implement proper JWT signing)
  const token = generateJWT(payload, streamApiSecret);

  return new Response(
    JSON.stringify({ token }),
    { headers: { ...corsHeaders, "Content-Type": "application/json" } }
  );
});

function generateJWT(payload: any, secret: string): string {
  // Implement JWT signing here
  // This is a complex operation - use a proper library
  throw new Error("Implement JWT signing with your Stream secret");
}
```

## Option 2: Using Supabase SQL RPC (PostgreSQL PL/pgSQL)

```sql
CREATE OR REPLACE FUNCTION get_stream_video_token(user_id UUID)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  v_token TEXT;
  v_payload JSONB;
BEGIN
  -- Create the token payload
  v_payload := jsonb_build_object(
    'user_id', user_id::text,
    'iat', EXTRACT(EPOCH FROM now())::bigint,
    'exp', EXTRACT(EPOCH FROM now() + interval '1 hour')::bigint
  );

  -- For production, you'll need to call an external service or use pgcrypto
  -- This is a simplified example
  v_token := encode(convert_to(v_payload::text, 'UTF8'), 'base64');

  RETURN json_build_object(
    'token', v_token
  );
END;
$$;
```

**Note**: PostgreSQL doesn't have built-in JWT signing. You'll need to either:
1. Use an Edge Function (Option 1 - Recommended)
2. Use a PostgREST call to an external service
3. Use pg_cron to pre-generate tokens

## Option 3: Call Stream API Directly (Simplest for Testing)

Pre-generate tokens and store them:

```sql
-- Create a table to store tokens
CREATE TABLE stream_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES auth.users(id),
  token TEXT NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP DEFAULT now(),
  UNIQUE(user_id)
);

-- Create RPC function that fetches or generates token
CREATE OR REPLACE FUNCTION get_stream_video_token(user_id UUID)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
  v_token TEXT;
  v_expires_at TIMESTAMP;
BEGIN
  -- Check if we have a valid token
  SELECT token, expires_at INTO v_token, v_expires_at
  FROM stream_tokens
  WHERE user_id = $1 AND expires_at > now();

  -- If token doesn't exist or expired, return error
  -- (In production, you'd generate a new one here)
  IF v_token IS NULL THEN
    RAISE EXCEPTION 'No valid token found';
  END IF;

  RETURN json_build_object('token', v_token);
END;
$$;
```

Then, use a backend service to generate tokens and store them in the table:

```python
# Python example using Stream SDK
import jwt
from datetime import datetime, timedelta
import os

def generate_stream_token(user_id: str) -> str:
    """Generate a Stream Video token for a user."""
    stream_api_key = os.getenv("STREAM_API_KEY")
    stream_api_secret = os.getenv("STREAM_API_SECRET")
    
    payload = {
        "user_id": user_id,
        "iat": datetime.utcnow(),
        "exp": datetime.utcnow() + timedelta(hours=1)
    }
    
    token = jwt.encode(
        payload,
        stream_api_secret,
        algorithm="HS256"
    )
    
    return token
```

## Configuration

In your `RoomDataAndroidModule.kt`, make sure to set your Stream API key:

```kotlin
val roomDataAndroidModule = module {
    single {
        StreamVideoConnectionManager(
            context = androidContext(),
            apiKey = "YOUR_STREAM_API_KEY"  // Get from dashboard.getstream.io
        )
    }
    // ... rest of configuration
}
```

## Testing the Token Generation

1. Deploy your token generation function
2. Call it with a valid Supabase auth token
3. Verify you get a valid JWT token back
4. Try logging in and creating a room in your app

## Resources

- [Stream API Documentation](https://getstream.io/documentation/)
- [JWT.io - JWT Token Debugger](https://jwt.io)
- [Supabase Edge Functions](https://supabase.com/docs/guides/functions)
- [Supabase RPC Calls](https://supabase.com/docs/reference/javascript/rpc)

