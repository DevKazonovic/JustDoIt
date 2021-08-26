package com.devkazonovic.projects.mytasks.service

class TaskNotificationManagerTest {

/*    private lateinit var context: Context
    private lateinit var taskNotificationDataSource: TaskNotificationDataSource
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var taskNotificationManager :TaskNotificationManager
    private lateinit var rxScheduler: IRxScheduler


    @Before
    fun setUp() {
        context = mock()
        taskNotificationDataSource = mock()
        notificationManager = mock()

        rxScheduler = RxScheduler(
            TestScheduler(),
            TestScheduler()
        )
        taskNotificationManager = TaskNotificationManager(
            context,notificationManager,taskNotificationDataSource,rxScheduler
        )
    }


    @Test
    fun createNotification(){
        //Given
        whenever(taskNotificationManager.createAlarmNotification(
            0, task.id, task.title, task.detail,"10:00"
        )).thenReturn(
            notification
        )

        //When
        taskNotificationManager.showDueDateNotification(
            0, task.id, task.title, task.detail,"10:00"
        )

        //Verify
        inOrder(taskNotificationDataSource,notificationManager) {
            verify(taskNotificationDataSource).insertNotification(taskNotification)
            verify(taskNotificationDataSource).updateNotification(taskNotification_showing)
            verify(notificationManager).notify(0, any())
        }

    }


    companion object{
        val task = Task(
            id = 0,
            title = "Task1",
            detail = "Detail",
            listID = 0,
            reminderDate = 1627981200000,
            pendingIntentRequestCode = 0
        )

        val taskNotification = TaskNotification(
            0,
            TaskNotificationState.NOT_NOTIFY
        )
        val taskNotification_showing = TaskNotification(
            0,
            TaskNotificationState.SHOWING
        )

        val notification = mock<NotificationCompat.Builder>()
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_round_timer)
            .setContentTitle(task.title)
            .setContentText("10:00, ${task.detail}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(mock())
            .setContentIntent(mock())
            .build()

    }*/
}