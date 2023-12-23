package com.example.alarmjava20;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyAlarmManager {
    private static final String TAG = "MyAlarmManager";
    private static final String PREF_NAME = "AlarmPref";
    private static final String KEY_ID = "id";
    private static final String KEY_HOUR = "hour";
    private static final String KEY_MINUTE = "minute";
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_SELECTED_MUSIC_URI = "selectedMusicUri";
    private static final String KEY_SELECTED_DAYS = "selectedDays";
    private static final String KEY_ALL_DAYS_SELECTED = "allDaysSelected";

    private final SharedPreferences preferences;
    private final List<Alarm> alarms;
    private final Context context;

    public MyAlarmManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        alarms = new ArrayList<>();
        loadAlarms();
    }
    public void updateAlarmTime(int alarmId, int newHour, int newMinute) {
        // Найдем будильник по ID
        Alarm alarm = getAlarmById(alarmId);
        if (alarm != null) {
            // Обновим время в найденном будильнике
            alarm.setHour(newHour);
            alarm.setMinute(newMinute);

            // Сохраним обновленный список будильников
            saveAlarms();

            // Обновим уведомление и пересоздадим расписание
            updateNotificationForAlarm(alarm);
        }
    }
    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
        saveAlarms();
    }

    public Alarm getAlarmById(int alarmId) {
        for (Alarm alarm : alarms) {
            if (alarm.getId() == alarmId) {
                return alarm;
            }
        }
        return null;
    }


    public void scheduleAlarm(Alarm alarm) {
        if (alarm.isEnabled()) {
            long timeInMillis = getAlarmTimeInMillis(alarm);
            cancelNotificationForAlarm(alarm);

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("alarmId", alarm.getId());
            intent.putExtra("selectedMusicUri", alarm.getSelectedMusicUri());
            intent.setAction("com.example.alarmjava20.NOTIFY_ALARM");

            // Проверяем, есть ли выбранные дни, и если есть, добавляем их в Intent
            List<Integer> selectedDays = alarm.getSelectedDays();
            if (!selectedDays.isEmpty() && !alarm.isAllDaysSelected()) {
                int[] daysArray = new int[selectedDays.size()];
                for (int i = 0; i < selectedDays.size(); i++) {
                    daysArray[i] = selectedDays.get(i);
                }
                intent.putExtra("selectedDays", daysArray);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, PendingIntent.FLAG_IMMUTABLE);
            int hourOfDay = alarm.getHour();
            int minute = alarm.getMinute();

            // ... (ваш текущий код)

            startActivityAtSpecifiedTime(alarm.getId(), timeInMillis, hourOfDay, minute);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (alarmManager.canScheduleExactAlarms()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(timeInMillis);
                    if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        timeInMillis = calendar.getTimeInMillis();
                    }
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }

    public void startActivityAtSpecifiedTime(int alarmId, long timeInMillis, int hourOfDay, int minute) {
        Log.d("startActivity", "alarmId: " + alarmId);
        Log.d("startActivity", "hourOfDay: " + hourOfDay);
        Log.d("startActivity", "minute: " + minute);

        Intent intent = new Intent(context, TimeAcceptanceActivity.class);
        intent.putExtra("alarmId", alarmId);
        intent.putExtra("hourOfDay", hourOfDay);
        intent.putExtra("minute", minute);

        // Добавляем флаг FLAG_UPDATE_CURRENT для обновления существующего PendingIntent
        int flags = PendingIntent.FLAG_UPDATE_CURRENT | Intent.FLAG_ACTIVITY_NEW_TASK;


        // Проверяем, что версия Android S (31) и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE;

            // Добавляем флаг, чтобы активность открывалась при заблокированном экране
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmId, intent, flags);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }



    public void removeAlarm(Alarm alarm) {
        alarms.remove(alarm);
        saveAlarms();
    }

    public void saveAlarms() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_ID, alarms.size());
        for (int i = 0; i < alarms.size(); i++) {
            Alarm alarm = alarms.get(i);
            editor.putInt(KEY_ID + i, alarm.getId());
            editor.putInt(KEY_HOUR + i, alarm.getHour());
            editor.putInt(KEY_MINUTE + i, alarm.getMinute());
            editor.putBoolean(KEY_ENABLED + i, alarm.isEnabled());
            editor.putString(KEY_SELECTED_MUSIC_URI + i, alarm.getSelectedMusicUri() != null ? alarm.getSelectedMusicUri().toString() : "");

            // Сохраняем выбранные дни
            List<Integer> selectedDays = alarm.getSelectedDays();
            StringBuilder selectedDaysString = new StringBuilder();
            for (int day : selectedDays) {
                selectedDaysString.append(day).append(" ");
            }
            editor.putString(KEY_SELECTED_DAYS + i, selectedDaysString.toString().trim());

            // Сохраняем флаг всех дней
            editor.putBoolean(KEY_ALL_DAYS_SELECTED + i, alarm.isAllDaysSelected());
        }
        editor.apply();
    }
    public void stopMusic() {
        AlarmMediaPlayer.getInstance(context).stop();
    }
    private void loadAlarms() {
        int size = preferences.getInt(KEY_ID, 0);
        for (int i = 0; i < size; i++) {
            int id = preferences.getInt(KEY_ID + i, 0);
            int hour = preferences.getInt(KEY_HOUR + i, 0);
            int minute = preferences.getInt(KEY_MINUTE + i, 0);
            boolean isEnabled = preferences.getBoolean(KEY_ENABLED + i, true);
            String selectedMusicUriString = preferences.getString(KEY_SELECTED_MUSIC_URI + i, "");
            Uri selectedMusicUri = !selectedMusicUriString.isEmpty() ? Uri.parse(selectedMusicUriString) : null;

            // Загружаем выбранные дни
            String selectedDaysString = preferences.getString(KEY_SELECTED_DAYS + i, "");
            List<Integer> selectedDays = new ArrayList<>();
            if (!selectedDaysString.isEmpty()) {
                String[] selectedDaysArray = selectedDaysString.split(" ");
                for (String day : selectedDaysArray) {
                    selectedDays.add(Integer.parseInt(day));
                }
            }

            // Загружаем флаг всех дней
            boolean allDaysSelected = preferences.getBoolean(KEY_ALL_DAYS_SELECTED + i, false);

            Alarm alarm = new Alarm(id, hour, minute);
            alarm.setEnabled(isEnabled);
            alarm.setSelectedMusicUri(selectedMusicUri);
            alarm.setSelectedDays(selectedDays);
            alarm.setAllDaysSelected(allDaysSelected);
            alarms.add(alarm);
        }
    }

    public void createNotificationForAlarm(Alarm alarm) {
        if (alarm.isEnabled()) {
            long timeInMillis = getAlarmTimeInMillis(alarm);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeInMillis);
            if (System.currentTimeMillis() >= calendar.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                timeInMillis = calendar.getTimeInMillis();
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("alarmId", alarm.getId());
            intent.putExtra("selectedMusicUri", alarm.getSelectedMusicUri());
            intent.setAction("com.example.alarmjava20.NOTIFY_ALARM");

            // Проверяем, есть ли выбранные дни, и если есть, добавляем их в Intent
            List<Integer> selectedDays = alarm.getSelectedDays();
            if (!selectedDays.isEmpty() && !alarm.isAllDaysSelected()) {
                int[] daysArray = new int[selectedDays.size()];
                for (int i = 0; i < selectedDays.size(); i++) {
                    daysArray[i] = selectedDays.get(i);
                }
                intent.putExtra("selectedDays", daysArray);
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }

    public void cancelNotificationForAlarm(Alarm alarm) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private long getAlarmTimeInMillis(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);

        // Добавляем выбранные дни недели к календарю
        List<Integer> selectedDays = alarm.getSelectedDays();
        if (!selectedDays.isEmpty() && !alarm.isAllDaysSelected()) {
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
            int daysToAdd = DaysOfWeek.calculateDaysToAdd(currentDay, selectedDays);
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);
        }

        return calendar.getTimeInMillis();
    }

    public void updateNotificationForAlarm(Alarm alarm) {
        if (alarm.isEnabled()) {
            cancelNotificationForAlarm(alarm);
            createNotificationForAlarm(alarm);
            scheduleAlarm(alarm);
        } else {
            cancelNotificationForAlarm(alarm);
        }
    }



}