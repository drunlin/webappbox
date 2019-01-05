package com.github.drunlin.webappbox.common

import android.os.Build

val ACTION_NEW = "${Build.ID}.ACTION_NEW"
val ACTION_EDIT = "${Build.ID}.ACTION_EDIT"

val EXTRA_ID = "id"
val EXTRA_UUID = "uuid"
val EXTRA_SHORTCUT_DUPLICATE = "duplicate"

val REQUEST_PICK_PICTURE = 0
val REQUEST_LOCATION_SETTINGS = 1
val REQUEST_GET_CONTENT = 2

val PERMISSIONS_REQUEST_LOCATION = 0
val PERMISSIONS_REQUEST_STORAGE = 1

val ARGUMENT_ID = "id"
val ARGUMENT_URL = "url"
val ARGUMENT_COLOR = "color"

val BUNDLE_UA = "ua"
val BUNDLE_ICON = "icon"
val BUNDLE_COUNT = "count"
val BUNDLE_IMAGE = "image"

val STATE_SUPER = "super"
val STATE_CHILDREN = "children"
val STATE_COLOR = "color"

val PREF_COLOR = "status_bar_color"
val PREF_USER_AGENT = "user_agent"
val PREF_ENABLE_JS = "enable_js"
val PREF_ORIENTATION = "screen_orientation"
val PREF_FULL_SCREEN = "full_screen"
val PREF_CLEAR_DATA = "clear_data"
val PREF_CLEAR_CACHE = "clear_cache"
val PREF_LAUNCH_MODE = "launch_mode"
val PREF_TEXT_ZOOM = "text_zoom"
