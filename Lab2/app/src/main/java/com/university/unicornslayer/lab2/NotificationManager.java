package com.university.unicornslayer.lab2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationManager {

    private Context mContext;
    private DateFormat mFullDateFormat;

    public NotificationManager(Context context) {
        mContext = context;
        mFullDateFormat = new SimpleDateFormat(mContext.getString(R.string.full_date_format));
    }

    public boolean smartScheduleNote(Note note) {
        if (!note.notifyMe)
            return false;

        Date today = new Date();
        if (note.date.before(today))
            return false;

        scheduleNote(note);
        return true;
    }

    public void scheduleNote(Note note) {

        Notification notification = createNotification(note);

        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, note.id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                note.id,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        long realTimeOffset = SystemClock.elapsedRealtime() - System.currentTimeMillis();
        long futureInMillis = filterSecondsAndMilsFromDate(note.date).getTime() + realTimeOffset;

        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification createNotification(Note note) {

        long[] vibrate = { 0, 100, 200 };
        Notification.Builder builder = new Notification.Builder(mContext)
                .setContentTitle("Note on " + mFullDateFormat.format(note.date))
                .setContentText(note.content)
                .setSmallIcon(R.drawable.scheduler_logo)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(vibrate);

        return builder.build();
    }

    private Date filterSecondsAndMilsFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public void cancelNotification(Integer noteId) {
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                noteId,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
}
