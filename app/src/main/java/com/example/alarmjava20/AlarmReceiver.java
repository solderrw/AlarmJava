    package com.example.alarmjava20;

    import android.app.Notification;
    import android.app.NotificationChannel;
    import android.app.NotificationManager;
    import android.app.PendingIntent;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.net.Uri;
    import android.os.Build;

    public class AlarmReceiver extends BroadcastReceiver {
        private static final String CHANNEL_ID = "AlarmChannel";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.alarmjava20.NOTIFY_ALARM")) {
                int alarmId = intent.getIntExtra("alarmId", 0);

                MyAlarmManager alarmManager = new MyAlarmManager(context);
                Alarm alarm = alarmManager.getAlarmById(alarmId);

                if (alarm != null && alarm.isEnabled()) {
                    String title = "Будильник";
                    String formattedHour = String.format("%02d", alarm.getHour());
                    String formattedMinute = String.format("%02d", alarm.getMinute());

                    String content = "Время для будильника: " + formattedHour + ":" + formattedMinute;

                    // Добавляем URI музыки в Intent
                    Uri selectedMusicUri = alarm.getSelectedMusicUri();
                    intent.putExtra("selectedMusicUri", selectedMusicUri.toString());

                    // Заменяем Intent на новый с URI музыки и передачей данных для TimeAcceptanceActivity
                    intent = new Intent(context, TimeAcceptanceActivity.class);
                    intent.putExtra("alarmId", alarmId);
                    intent.putExtra("hourOfDay", alarm.getHour());
                    intent.putExtra("minute", alarm.getMinute());

                    // Создаем PendingIntent для запуска TimeAcceptanceActivity
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                    // Создаем уведомление
                    Notification notification = createNotification(context, title, content, pendingIntent);

                    // Отображаем уведомление
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(alarmId, notification);

                    // Воспроизводим музыку с использованием AlarmMediaPlayer
                    AlarmMediaPlayer alarmMediaPlayer = AlarmMediaPlayer.getInstance(context);
                    alarmMediaPlayer.play(selectedMusicUri);
                }
            }
        }

        private Notification createNotification(Context context, String title, String content, PendingIntent intent) {
            createNotificationChannel(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return new Notification.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setSmallIcon(R.drawable.icon2141470)
                        .setContentIntent(intent)
                        .setAutoCancel(true)
                        .build();
            } else {
                return new Notification.Builder(context)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setSmallIcon(R.drawable.icon2141470)
                        .setContentIntent(intent)
                        .setAutoCancel(true)
                        .build();
            }
        }

        private void createNotificationChannel(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Alarm Channel";
                String description = "Channel for alarm notifications";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
