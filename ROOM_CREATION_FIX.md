# Room Creation Fix - Backend Setup Required

## Problem
When clicking "Let's Go" to create a room, the app was failing with `RoomCallError.NOT_CONNECTED` because users were never being connected to the Stream Video service before attempting to join a room.

## Solution Implemented

### Client-Side Changes
The app now automatically connects users to Stream Video when the Room screen appears:

1. **StreamVideoConnectionManager** - Manages Stream Video client lifecycle
2. **StreamRoomConnectionRepository** - Handles user connection logic:
   - Fetches user profile from Supabase (name, avatar)
   - Requests a Stream Video token from your backend
   - Connects the user to Stream Video
   - Only connects once; skips if already connected

3. **RoomViewModel** - Automatically calls `ensureUserConnected()` in `init`
4. **Koin DI** - All dependencies properly injected

## Backend Setup Required

### 1. Get Your Stream API Key
- Go to [getstream.io](https://getstream.io)
- Create a project and get your API key
- Update `/shared/src/androidMain/kotlin/app/voqal/com/di/RoomDataAndroidModule.kt`:
  ```kotlin
  apiKey = "YOUR_STREAM_API_KEY"  // Replace with your actual key
  ```

### 2. Create Supabase RPC Function
Create this function in your Supabase database to generate Stream Video tokens:

```sql
CREATE OR REPLACE FUNCTION get_stream_video_token(user_id UUID)
RETURNS JSON
SECURITY DEFINER
SET search_path = public
LANGUAGE plpgsql
AS $$
DECLARE
  stream_api_key TEXT;
  stream_api_secret TEXT;
  token_payload JSON;
  token TEXT;
BEGIN
  -- Get your Stream credentials from environment or secrets
  stream_api_key := current_setting('app.stream_api_key');
  stream_api_secret := current_setting('app.stream_api_secret');
  
  -- Create token payload (JWT format)
  token_payload := json_build_object(
    'user_id', user_id::text,
    'iat', EXTRACT(EPOCH FROM NOW())::int,
    'exp', (EXTRACT(EPOCH FROM NOW()) + 3600)::int
  );
  
  -- TODO: Generate JWT token using stream_api_key and stream_api_secret
  -- For now, return a placeholder that needs to be replaced with actual implementation
  token := 'YOUR_GENERATED_JWT_TOKEN';
  
  RETURN json_build_object('token', token);
END;
$$;
```

**IMPORTANT**: You'll need to:
1. Store your Stream API Key and Secret securely (use Supabase Secrets or environment variables)
2. Implement proper JWT token generation using your Stream credentials
3. Use a library like `jsonwebtoken` or similar to sign tokens

### 3. Alternative: Use Stream Dashboard Token Generation
If you prefer not to implement token generation in Supabase, you can:
1. Use Stream's admin endpoints to pre-generate tokens
2. Store tokens in a `stream_tokens` table with expiration
3. Have the RPC function fetch from that table

## Stream SDK Documentation
For help with Stream API key setup and token generation:
- [Stream Docs](https://getstream.io/docs/)
- [Stream Chat React Documentation](https://getstream.io/chat/react/docs/)
- [Stream Video Android Documentation](https://getstream.io/video/docs/android/)

## Testing
Once the backend is set up:
1. Login through the onboarding flow
2. You should see an "Initialize connection" phase
3. Click "Let's Go" to create a room
4. If successful, you'll be able to join the room

## Error Handling
If connection fails, you'll see an error message explaining the issue:
- `NOT_CONNECTED` - User not authenticated with Supabase
- `UNKNOWN` - Backend RPC function failed or token generation failed
- `JOIN_FAILED` - Failed to join the room with Stream API

## Common Issues

### Issue: "Cannot find function get_stream_video_token"
**Solution**: Make sure the RPC function is created in your Supabase database

### Issue: "Invalid token"
**Solution**: Verify that:
- Your Stream API key and secret are correct
- JWT token is signed properly
- Token expiration is set correctly

### Issue: Still getting NOT_CONNECTED
**Solution**: Check that:
- User is logged in (Supabase auth)
- Profile exists in the `profiles` table
- Backend RPC function is working

## Files Modified
- `/shared/src/commonMain/kotlin/app/voqal/com/feature/room/presentation/RoomViewModel.kt`
- `/shared/src/androidMain/kotlin/app/voqal/com/feature/room/StreamVideoConnectionManager.kt` (NEW)
- `/shared/src/androidMain/kotlin/app/voqal/com/feature/room/data/SupabaseStreamRoomConnectionRepository.kt` (NEW)
- `/shared/src/commonMain/kotlin/app/voqal/com/feature/room/domain/StreamRoomConnectionRepository.kt` (NEW)
- `/shared/src/androidMain/kotlin/app/voqal/com/di/RoomDataAndroidModule.kt`
- `/shared/src/commonMain/kotlin/app/voqal/com/feature/room/di/RoomPresentationModule.kt`

