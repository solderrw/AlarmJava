package com.example.alarmjava20;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listViewAlarms;
    private AlarmAdapter adapter;
    private MyAlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationHelper.createNotificationChannel(this);

        listViewAlarms = findViewById(R.id.listViewAlarms);
        alarmManager = new MyAlarmManager(this);

        Button buttonXmlNew = findViewById(R.id.buttonXml_new);
        buttonXmlNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        });

        updateAlarmList();
    }

    private void updateAlarmList() {
        List<Alarm> alarms = alarmManager.getAlarms();
        adapter = new AlarmAdapter(this, alarms, alarmManager);
        listViewAlarms.setAdapter(adapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            int alarmId = data.getIntExtra("alarmId", 0);
            int hour = data.getIntExtra("hour", 0);
            int minute = data.getIntExtra("minute", 0);
            boolean isEnabled = data.getBooleanExtra("isEnabled", true);
            Uri selectedMusicUri = data.getParcelableExtra("selectedMusicUri");
            Integer[] selectedDaysArray = (Integer[]) data.getSerializableExtra("selectedDays");
            boolean allDaysSelected = data.getBooleanExtra("allDaysSelected", true);


              // Обновляем список сразу после получения результатов
            List<Alarm> alarms = alarmManager.getAlarms();
            for (Alarm alarm : alarms) {
                if (alarm.getId() == alarmId) {
                    alarm.setHour(hour);
                    alarm.setMinute(minute);
                    alarm.setEnabled(isEnabled);
                    alarm.setSelectedMusicUri(selectedMusicUri);
                    alarm.setSelectedDays(new ArrayList<>(Arrays.asList(selectedDaysArray)));
                    alarm.setAllDaysSelected(allDaysSelected);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }

    }


}