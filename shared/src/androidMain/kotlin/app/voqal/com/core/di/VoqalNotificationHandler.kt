package app.voqal.com.core.di

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.getstream.video.android.core.notifications.DefaultNotificationIntentBundleResolver
import io.getstream.video.android.core.notifications.DefaultStreamIntentResolver
import io.getstream.video.android.core.notifications.StreamIntentResolver
import io.getstream.video.android.core.notifications.handlers.CompatibilityStreamNotificationHandler
import io.getstream.video.android.model.StreamCallId

class VoqalNotificationHandler(
    application: Application
) : CompatibilityStreamNotificationHandler(
    application = application,
    intentResolver = VoqalStreamIntentResolver(
        application,
        DefaultStreamIntentResolver(application, DefaultNotificationIntentBundleResolver())
    )
)

class VoqalStreamIntentResolver(
    private val context: Context,
    private val defaultResolver: StreamIntentResolver
) : StreamIntentResolver by defaultResolver {

    override fun searchOngoingCallPendingIntent(
        callId: StreamCallId,
        notificationId: Int,
        payload: Map<String, Any?>
    ): PendingIntent? {
        return createMainActivityPendingIntent(callId, notificationId)
    }

    override fun searchIncomingCallPendingIntent(
        callId: StreamCallId,
        notificationId: Int,
        payload: Map<String, Any?>
    ): PendingIntent? {
        return createMainActivityPendingIntent(callId, notificationId)
    }

    private fun createMainActivityPendingIntent(
        callId: StreamCallId,
        notificationId: Int
    ): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            putExtra("roomId", callId.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } ?: return null

        return PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
