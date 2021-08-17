package com.devkazonovic.projects.mytasks.help

import com.devkazonovic.projects.mytasks.MyTasksApplication

object MyIntent {

    private val PACKAGE_NAME = MyTasksApplication.myPackageName

    val EXTRA_EXACT_ALARM_TIME = "$PACKAGE_NAME.EXTRA_EXACT_ALARM_TIME"
    val EXTRA_EXACT_ALARM_TITLE = "$PACKAGE_NAME.EXTRA_EXACT_ALARM_TITLE"
    val EXTRA_EXACT_ALARM_DETAIL = "$PACKAGE_NAME.EXTRA_EXACT_ALARM_DETAIL"
    val EXTRA_EXACT_ALARM_REQUEST_CODE = "$PACKAGE_NAME.EXTRA_EXACT_ALARM_REQUEST_CODE"
    val EXTRA_EXACT_ALARM_ID = "$PACKAGE_NAME.EXTRA_EXACT_ALARM_ID"
    val EXTRA_NOTIFICATION_ID = "$PACKAGE_NAME.EXTRA_NOTIFICATION_ID"


    val ACTION_SET_EXACT = "$PACKAGE_NAME.ACTION_SET_EXACT_ALARM"
    val ACTION_NOTIFICATION_DISMISS = "$PACKAGE_NAME.ACTION_NOTIFICATION_DISMISS"

    val ACTION_SET_REPETITIVE_EXACT = "$PACKAGE_NAME.ACTION_SET_REPETITIVE_EXACT_ALARM"
}