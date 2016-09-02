package com.mlp.elrond.fsociety;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class ManageNotifications extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleAlarms(context);
    }
    static void scheduleAlarms(Context context) {
        AlarmManager notification_alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent notification_intent = new Intent(context, ScheduledService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notification_intent, 0);

        /*
        Calendar firingCal = Calendar.getInstance();
        firingCal.set(Calendar.YEAR, 2016);
        firingCal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        firingCal.set(Calendar.DATE, 2);
        firingCal.set(Calendar.HOUR, 3);
        firingCal.set(Calendar.MINUTE, 44);
        firingCal.set(Calendar.SECOND, 0);
        long intendedTime = firingCal.getTimeInMillis();
                notification_alarm.setRepeating
                (AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        */

        if(new ManageSharedPref().loadTvShows(context).size() != 0) {
            List<TvShows> watchListShows = new ManageSharedPref().loadTvShows(context);
            Date tvShowDate = watchListShows.get(0).getOnAirDate();
            long onAirTime = tvShowDate.getTime();
            notification_alarm.setRepeating(AlarmManager.RTC, onAirTime, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
    }

    public static class ScheduledService extends IntentService {
        public ScheduledService() {
            super("ScheduledService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.d(getClass().getSimpleName(), "Just fired awesome notification!");
            raiseNotification();
        }

        private void raiseNotification(){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle("Awesome Title")
                    .setContentText("Awesome Text")
                    .setSmallIcon(android.R.drawable.stat_sys_warning);

            NotificationManager mgr= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mgr.notify(442268189, builder.build());
        }

    }

}
