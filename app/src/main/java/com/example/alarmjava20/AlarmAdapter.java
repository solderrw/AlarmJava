        package com.example.alarmjava20;

        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.Switch;
        import android.widget.TextView;

        import androidx.annotation.NonNull;

        import java.util.ArrayList;
        import java.util.List;

        public class AlarmAdapter extends ArrayAdapter<Alarm> {

            private MyAlarmManager alarmManager;
            private Context context;

            public AlarmAdapter(Context context, List<Alarm> alarms, MyAlarmManager alarmManager) {
                super(context, R.layout.alarm_item, alarms);
                this.alarmManager = alarmManager;
                this.context = context;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_item, parent, false);
                }

                TextView textView = convertView.findViewById(R.id.textViewTime);
                TextView textViewLabel = convertView.findViewById(R.id.textViewLabel); // Новый TextView
                Switch switchAlarm = convertView.findViewById(R.id.switchAlarm);
                Button buttonChange = convertView.findViewById(R.id.buttonChange);
                Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

                final Alarm alarm = getItem(position);

                if (alarm != null) {
                    String formattedTime = String.format("%02d:%02d", alarm.getHour(), alarm.getMinute());
                    textView.setText(formattedTime);
                    switchAlarm.setChecked(alarm.isEnabled());

                    // Устанавливаем текст для нового TextView
                    updateTextViewLabel(textViewLabel, alarm.getSelectedDays());

                    switchAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            alarm.setEnabled(isChecked);
                            alarmManager.saveAlarms();

                            AlarmMediaPlayer alarmMediaPlayer = AlarmMediaPlayer.getInstance(context);

                            if (isChecked) {
                                alarmManager.createNotificationForAlarm(alarm);
                            } else {
                                alarmManager.cancelNotificationForAlarm(alarm);

                                if (alarmMediaPlayer.isPlaying()) {
                                    alarmMediaPlayer.stop();
                                }
                            }
                        }
                    });

                        buttonChange.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ChangeActivity.class);
                                intent.putExtra("alarmId", alarm.getId());
                                intent.putExtra("hour", alarm.getHour());
                                intent.putExtra("minute", alarm.getMinute());
                                intent.putExtra("isEnabled", alarm.isEnabled());
                                intent.putExtra("selectedMusicUri", alarm.getSelectedMusicUri());



                                // Передаем массив выбранных дней
                                Integer[] selectedDaysArray = new Integer[alarm.getSelectedDays().size()];
                                intent.putExtra("selectedDays", alarm.getSelectedDays().toArray(selectedDaysArray));

                                intent.putExtra("allDaysSelected", alarm.isAllDaysSelected());
                                ((MainActivity) context).startActivityForResult(intent, 1);
                            }
                        });




                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlarmMediaPlayer alarmMediaPlayer = AlarmMediaPlayer.getInstance(context);

                            if (alarmMediaPlayer != null) {
                                alarmMediaPlayer.stop();
                                alarmMediaPlayer.release();
                            }

                            // Очистите выбранные дни недели
                            alarm.setSelectedDays(null); // или alarm.setSelectedDays(new ArrayList<>());

                            // Удалите будильник
                            alarmManager.removeAlarm(alarm);

                            // Обновите адаптер
                            notifyDataSetChanged();
                        }
                    });
                }

                return convertView;
            }

            private void updateTextViewLabel(TextView textViewLabel, List<Integer> selectedDays) {
                if (selectedDays == null || selectedDays.isEmpty()) {
                    textViewLabel.setText("Каждый день");
                } else if (selectedDays.size() == DaysOfWeek.DAYS_IN_WEEK) {
                    textViewLabel.setText("Каждый день");
                } else {
                    StringBuilder selectedDaysBuilder = new StringBuilder("Выбранные дни: ");
                    for (int day : selectedDays) {
                        selectedDaysBuilder.append(DaysOfWeek.getDayName(day)).append(" ");
                    }
                    textViewLabel.setText(selectedDaysBuilder.toString().trim());
                }

            }
        }