package com.example.alarmjava20;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AddAlarmActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button buttonAddAlarm;
    private Button buttonChooseMusic;
    private Button buttonChooseDays;
    private TextView textViewSelectedMusic;
    private TextView textViewSelectedDay;
    private MyAlarmManager alarmManager;
    private Uri selectedMusicUri;
    private List<Integer> selectedDays;

    private static final int PICK_RINGTONE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        buttonAddAlarm = findViewById(R.id.buttonAddAlarm);
        buttonChooseMusic = findViewById(R.id.buttonChooseMusic);
        buttonChooseDays = findViewById(R.id.buttonChooseDays);
        textViewSelectedMusic = findViewById(R.id.textViewSelectedMusic);
        textViewSelectedDay = findViewById(R.id.textViewSelectedDay);
        alarmManager = new MyAlarmManager(this);

        buttonAddAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                Calendar calendar = Calendar.getInstance();
                int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                int currentMinute = calendar.get(Calendar.MINUTE);

                if (hour < currentHour || (hour == currentHour && minute < currentMinute)) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                int alarmId = new Random().nextInt(10000);

                Alarm alarm = new Alarm(alarmId, hour, minute);

                // Проверяем, выбрана ли музыка
                if (selectedMusicUri == null) {
                    // Если не выбрана, получаем URI стандартного звука будильника
                    selectedMusicUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                }

                alarm.setSelectedMusicUri(selectedMusicUri);

                // Проверяем, выбраны ли какие-то дни
                if (selectedDays == null || selectedDays.isEmpty()) {
                    // Если ни один день не выбран, устанавливаем все дни
                    selectedDays = new ArrayList<>();
                    for (int day = DaysOfWeek.SUNDAY; day <= DaysOfWeek.SATURDAY; day++) {
                        selectedDays.add(day);
                    }
                }

                // Сохраняем выбранные дни
                alarm.setSelectedDays(selectedDays);

                // Проверяем, выбраны ли все дни
                boolean allDaysSelected = selectedDays.size() == DaysOfWeek.DAYS_IN_WEEK;
                alarm.setAllDaysSelected(allDaysSelected);

                alarmManager.addAlarm(alarm);
                alarmManager.scheduleAlarm(alarm);

                finish();
                Intent intent = new Intent(AddAlarmActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        buttonChooseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите музыку для будильника");
                startActivityForResult(intent, PICK_RINGTONE_REQUEST_CODE);
            }
        });

        buttonChooseDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(AddAlarmActivity.this);
                View checkBoxView = inflater.inflate(R.layout.checkbox_dialog, null);

                final CheckBox checkBoxSunday = checkBoxView.findViewById(R.id.checkBoxSunday);
                final CheckBox checkBoxMonday = checkBoxView.findViewById(R.id.checkBoxMonday);
                final CheckBox checkBoxTuesday = checkBoxView.findViewById(R.id.checkBoxTuesday);
                final CheckBox checkBoxWednesday = checkBoxView.findViewById(R.id.checkBoxWednesday);
                final CheckBox checkBoxThursday = checkBoxView.findViewById(R.id.checkBoxThursday);
                final CheckBox checkBoxFriday = checkBoxView.findViewById(R.id.checkBoxFriday);
                final CheckBox checkBoxSaturday = checkBoxView.findViewById(R.id.checkBoxSaturday);

                // Установите предыдущий выбор, если есть
                if (selectedDays != null && !selectedDays.isEmpty()) {
                    for (int day : selectedDays) {
                        switch (day) {
                            case DaysOfWeek.SUNDAY:
                                checkBoxSunday.setChecked(true);
                                break;
                            case DaysOfWeek.MONDAY:
                                checkBoxMonday.setChecked(true);
                                break;
                            case DaysOfWeek.TUESDAY:
                                checkBoxTuesday.setChecked(true);
                                break;
                            case DaysOfWeek.WEDNESDAY:
                                checkBoxWednesday.setChecked(true);
                                break;
                            case DaysOfWeek.THURSDAY:
                                checkBoxThursday.setChecked(true);
                                break;
                            case DaysOfWeek.FRIDAY:
                                checkBoxFriday.setChecked(true);
                                break;
                            case DaysOfWeek.SATURDAY:
                                checkBoxSaturday.setChecked(true);
                                break;
                        }
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(AddAlarmActivity.this);
                builder.setView(checkBoxView)
                        .setTitle("Выберите дни недели")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                selectedDays = new ArrayList<>();

                                if (checkBoxSunday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.SUNDAY);
                                }
                                if (checkBoxMonday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.MONDAY);
                                }
                                if (checkBoxTuesday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.TUESDAY);
                                }
                                if (checkBoxWednesday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.WEDNESDAY);
                                }
                                if (checkBoxThursday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.THURSDAY);
                                }
                                if (checkBoxFriday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.FRIDAY);
                                }
                                if (checkBoxSaturday.isChecked()) {
                                    selectedDays.add(DaysOfWeek.SATURDAY);
                                }

                                if (selectedDays.isEmpty()) {
                                    textViewSelectedDay.setText("Некоторые дни");
                                } else if (selectedDays.size() == DaysOfWeek.DAYS_IN_WEEK) {
                                    textViewSelectedDay.setText("Каждый день");
                                } else {
                                    StringBuilder selectedDaysBuilder = new StringBuilder("Выбранные дни: ");
                                    for (int day : selectedDays) {
                                        selectedDaysBuilder.append(DaysOfWeek.getDayName(day)).append(" ");
                                    }
                                    textViewSelectedDay.setText(selectedDaysBuilder.toString().trim());
                                }
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Отмена выбора
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedMusicUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (selectedMusicUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(this, selectedMusicUri);
                String ringtoneTitle = ringtone.getTitle(this);
                textViewSelectedMusic.setText("Выбранный рингтон: " + ringtoneTitle);
            } else {
                textViewSelectedMusic.setText("Выбран рингтон: Нет");
            }
        }
    }
}
