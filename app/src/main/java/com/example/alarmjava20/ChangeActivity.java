    package com.example.alarmjava20;

    import android.content.Intent;
    import android.media.Ringtone;
    import android.media.RingtoneManager;
    import android.net.Uri;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.TextView;
    import android.widget.TimePicker;

    import androidx.appcompat.app.AlertDialog;
    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.List;

    public class ChangeActivity extends AppCompatActivity {

        private static final int PICK_RINGTONE_REQUEST_CODE = 1;
        private static final int PICK_DAYS_REQUEST_CODE = 2;

        private TimePicker timePickerChange;
        private MyAlarmManager alarmManager;
        private int alarmId;
        private Uri selectedMusicUri;
        private List<Integer> selectedDays;
        private TextView textViewSelectedDayChange;
        private boolean allDaysSelected;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_alarm);

            timePickerChange = findViewById(R.id.timePickerChange);
            timePickerChange.setIs24HourView(true);

            alarmId = getIntent().getIntExtra("alarmId", 0);
            int hour = getIntent().getIntExtra("hour", 0);
            int minute = getIntent().getIntExtra("minute", 0);
            boolean isEnabled = getIntent().getBooleanExtra("isEnabled", true);
            selectedMusicUri = getIntent().getParcelableExtra("selectedMusicUri");
            Integer[] selectedDaysArray = (Integer[]) getIntent().getSerializableExtra("selectedDays");
            allDaysSelected = getIntent().getBooleanExtra("allDaysSelected", false);



            timePickerChange.setHour(hour);
            timePickerChange.setMinute(minute);

            alarmManager = new MyAlarmManager(this);

            Button buttonChooseMusic = findViewById(R.id.buttonChooseMusic);
            Button buttonChooseDaysChange = findViewById(R.id.buttonChooseDaysChange);
            Button buttonChangeTime = findViewById(R.id.buttonChangeTime);
            TextView textViewSelectedMusic = findViewById(R.id.textViewSelectedMusic);
            textViewSelectedDayChange = findViewById(R.id.textViewSelectedDayChange);

            // Получаем переданные дни недели и устанавливаем соответствующий текст в textViewSelectedDayChange
            if (selectedDaysArray != null && selectedDaysArray.length > 0) {
                selectedDays = Arrays.asList(selectedDaysArray);
                updateSelectedDaysText();
            }

            if (selectedMusicUri != null) {
                Ringtone ringtone = RingtoneManager.getRingtone(ChangeActivity.this, selectedMusicUri);
                String ringtoneTitle = ringtone.getTitle(ChangeActivity.this);
                textViewSelectedMusic.setText("Текущий рингтон: " + ringtoneTitle);
            } else {
                textViewSelectedMusic.setText("Текущий рингтон: Нет");
            }

            buttonChooseMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите музыку для будильника");
                    startActivityForResult(intent, PICK_RINGTONE_REQUEST_CODE);
                }
            });

            buttonChooseDaysChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Открываем диалог для выбора дней недели
                    showDaysDialog();
                }
            });

            buttonChangeTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hour = timePickerChange.getHour();
                    int minute = timePickerChange.getMinute();
                    boolean isEnabled = getIntent().getBooleanExtra("isEnabled", true);

                    if (!isEnabled) {
                        isEnabled = true;
                    }

                    Alarm alarm = alarmManager.getAlarmById(alarmId);
                    if (alarm != null) {
                        alarm.setHour(hour);
                        alarm.setMinute(minute);
                        alarm.setEnabled(isEnabled);
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
                        allDaysSelected = selectedDays.size() == DaysOfWeek.DAYS_IN_WEEK;
                        alarm.setAllDaysSelected(allDaysSelected);

                        alarmManager.saveAlarms();
                        alarmManager.updateNotificationForAlarm(alarm);
                        alarmManager.scheduleAlarm(alarm);
                    }

                    Intent intent = new Intent();
                    intent.putExtra("alarmId", alarmId);
                    intent.putExtra("hour", hour);
                    intent.putExtra("minute", minute);
                    intent.putExtra("isEnabled", isEnabled);
                    intent.putExtra("selectedMusicUri", selectedMusicUri);
                    intent.putExtra("selectedDays", selectedDays.toArray(new Integer[0])); // Передаем выбранные дни
                    intent.putExtra("allDaysSelected", allDaysSelected); // Передаем флаг allDaysSelected
                    setResult(RESULT_OK, intent);
                    finish();

                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_RINGTONE_REQUEST_CODE && resultCode == RESULT_OK) {
                selectedMusicUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                TextView textViewSelectedMusic = findViewById(R.id.textViewSelectedMusic);

                if (selectedMusicUri != null) {
                    Ringtone ringtone = RingtoneManager.getRingtone(this, selectedMusicUri);
                    String ringtoneTitle = ringtone.getTitle(this);
                    textViewSelectedMusic.setText("Текущий рингтон: " + ringtoneTitle);
                } else {
                    textViewSelectedMusic.setText("Текущий рингтон: Нет");
                }
            }
        }

        private void showDaysDialog() {
            LayoutInflater inflater = LayoutInflater.from(ChangeActivity.this);
            View checkBoxView = inflater.inflate(R.layout.checkbox_dialog, null);

            final CheckBox checkBoxSunday = checkBoxView.findViewById(R.id.checkBoxSunday);
            final CheckBox checkBoxMonday = checkBoxView.findViewById(R.id.checkBoxMonday);
            final CheckBox checkBoxTuesday = checkBoxView.findViewById(R.id.checkBoxTuesday);
            final CheckBox checkBoxWednesday = checkBoxView.findViewById(R.id.checkBoxWednesday);
            final CheckBox checkBoxThursday = checkBoxView.findViewById(R.id.checkBoxThursday);
            final CheckBox checkBoxFriday = checkBoxView.findViewById(R.id.checkBoxFriday);
            final CheckBox checkBoxSaturday = checkBoxView.findViewById(R.id.checkBoxSaturday);

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

            AlertDialog.Builder builder = new AlertDialog.Builder(ChangeActivity.this);
            builder.setView(checkBoxView)
                    .setTitle("Выберите дни недели")
                    .setPositiveButton("OK", (dialog, which) -> {
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

                        updateSelectedDaysText(); // Обновляем текст после выбора дней
                    })
                    .setNegativeButton("Отмена", (dialog, which) -> {
                        // Отмена выбора
                    });
            builder.create().show();
        }

        private void updateSelectedDaysText() {
            if (selectedDays.isEmpty()) {
                textViewSelectedDayChange.setText("Некоторые дни");
            } else if (selectedDays.size() == DaysOfWeek.DAYS_IN_WEEK) {
                textViewSelectedDayChange.setText("Каждый день");
            } else {
                StringBuilder selectedDaysBuilder = new StringBuilder("Выбранные дни: ");
                for (int day : selectedDays) {
                    selectedDaysBuilder.append(DaysOfWeek.getDayName(day)).append(" ");
                }
                textViewSelectedDayChange.setText(selectedDaysBuilder.toString().trim());
            }
        }
    }
