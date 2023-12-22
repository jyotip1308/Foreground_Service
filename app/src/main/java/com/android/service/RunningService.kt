package com.android.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService

class RunningService: Service() {

    private var countDownTimer: CountDownTimer? = null
    private var currentTime: Long = 300 // Initial time in seconds (e.g., 5 minutes)
    private val notificationId = 1 // Unique ID for the notification
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    override fun onBind(p0:Intent?) : IBinder?{
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        notificationManager = getSystemService()!!
        notificationBuilder = createNotificationBuilder()

        when(intent?.action){
            Actions.START.toString() -> startCountdown()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Run is active")
            .setOnlyAlertOnce(true) // To prevent the sound from playing every time
            .setContentIntent(contentIntent)
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(currentTime * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime = millisUntilFinished / 1000
                updateNotification(currentTime)
            }

            override fun onFinish() {
                stopSelf()
            }
        }
        countDownTimer?.start()
    }

    private fun stopCountdown() {
        countDownTimer?.cancel()
    }

    private fun updateNotification(currentTime: Long) {
       /* val formattedTime = String.format("%02d:%02d", currentTime / 60, currentTime % 60)

        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Run is active")
            .setContentText("Elapsed time: $formattedTime")
            .build()
        startForeground(1, notification)*/


        val formattedTime = String.format("%02d:%02d", currentTime / 60, currentTime % 60)

        if (notificationBuilder == null) {
            // Create a new notification builder if it doesn't exist
            notificationBuilder = NotificationCompat.Builder(this, "running_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Run is active")
        }

        // Update the notification content
        notificationBuilder?.setContentText("Elapsed time: $formattedTime")

        // Get the notification instance
        val notification = notificationBuilder?.build()

        // Update the existing notification
        startForeground(notificationId, notification)
    }

    override fun onDestroy() {
        stopCountdown()
        super.onDestroy()
    }

    enum class Actions{
        START, STOP
    }
}