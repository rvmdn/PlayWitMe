package com.playwithme.assistant.core.common

/**
 * Application-wide constants for PlayWitMe.
 *
 * Group constants by feature area for discoverability.
 * Do NOT use magic numbers or strings elsewhere — always reference these.
 */
object Constants {

    // ─── App ──────────────────────────────────────────────────────────────
    const val APP_NAME = "PlayWitMe"
    const val DATABASE_NAME = "playwithme.db"
    const val DATABASE_VERSION = 1

    // ─── Preferences (DataStore keys) ─────────────────────────────────────
    const val PREFS_NAME = "playwithme_prefs"

    // ─── Overlay ──────────────────────────────────────────────────────────
    const val OVERLAY_DEFAULT_WIDTH_DP = 300
    const val OVERLAY_DEFAULT_HEIGHT_DP = 200
    const val OVERLAY_MIN_WIDTH_DP = 150
    const val OVERLAY_MIN_HEIGHT_DP = 80
    const val OVERLAY_DEFAULT_ALPHA = 0.9f
    const val OVERLAY_SNAP_THRESHOLD_DP = 40

    // ─── Service ──────────────────────────────────────────────────────────
    const val OVERLAY_SERVICE_NOTIFICATION_ID = 1001
    const val CAPTURE_SERVICE_NOTIFICATION_ID = 1002
    const val OCR_SERVICE_NOTIFICATION_ID = 1003
    const val NOTIFICATION_CHANNEL_ID = "playwithme_service_channel"
    const val NOTIFICATION_CHANNEL_NAME = "PlayWitMe Assistant"

    // ─── MediaProjection ──────────────────────────────────────────────────
    const val MEDIA_PROJECTION_REQUEST_CODE = 100
    const val CAPTURE_FRAME_RATE = 10 // frames per second for OCR — battery efficient
    const val CAPTURE_WIDTH_PX = 1280 // downscaled for OCR performance
    const val CAPTURE_HEIGHT_PX = 720

    // ─── OCR ──────────────────────────────────────────────────────────────
    const val OCR_FRAME_THROTTLE_MS = 1000L / CAPTURE_FRAME_RATE
    const val OCR_MAX_IMAGE_DIMENSION = 1280

    // ─── Timer (seconds) ──────────────────────────────────────────────────
    const val LORD_RESPAWN_SECONDS = 360
    const val TURTLE_RESPAWN_SECONDS = 90
    const val BUFF_DURATION_SECONDS = 90

    // ─── Navigation Routes ─────────────────────────────────────────────────
    const val ROUTE_HOME = "home"
    const val ROUTE_HERO_LIST = "hero_list"
    const val ROUTE_HERO_DETAIL = "hero_detail/{heroId}"
    const val ROUTE_ASSISTANT = "assistant"
    const val ROUTE_BUILD = "build"
    const val ROUTE_OVERLAY_CONTROL = "overlay_control"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_ANALYTICS = "analytics"
    const val ROUTE_DRAFT = "draft"

    // ─── Intent Actions ───────────────────────────────────────────────────
    const val ACTION_START_OVERLAY = "com.playwithme.assistant.START_OVERLAY"
    const val ACTION_STOP_OVERLAY = "com.playwithme.assistant.STOP_OVERLAY"
    const val ACTION_START_CAPTURE = "com.playwithme.assistant.START_CAPTURE"
    const val ACTION_STOP_CAPTURE = "com.playwithme.assistant.STOP_CAPTURE"

    // ─── Intent Extras ────────────────────────────────────────────────────
    const val EXTRA_MEDIA_PROJECTION_RESULT_CODE = "media_projection_result_code"
    const val EXTRA_MEDIA_PROJECTION_DATA = "media_projection_data"
    const val EXTRA_HERO_ID = "hero_id"

    // ─── WorkManager Tags ─────────────────────────────────────────────────
    const val WORK_TAG_HERO_SYNC = "hero_sync"
    const val WORK_TAG_DB_CLEANUP = "db_cleanup"

    // ─── Network ──────────────────────────────────────────────────────────
    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val NETWORK_MAX_RETRIES = 3
}
