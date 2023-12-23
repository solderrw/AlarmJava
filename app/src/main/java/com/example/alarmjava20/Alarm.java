package com.example.alarmjava20;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private boolean isEnabled;
    private Uri selectedMusicUri;
    private List<Integer> selectedDays;
    private boolean allDaysSelected;


    public Alarm(int id, int hour, int minute) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.isEnabled = true;
        this.selectedDays = new ArrayList<>();
        this.allDaysSelected = true; // Изначально не все дни выбраны
    }

    public int getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Uri getSelectedMusicUri() {
        return selectedMusicUri;
    }

    public void setSelectedMusicUri(Uri selectedMusicUri) {
        this.selectedMusicUri = selectedMusicUri;
    }

    public List<Integer> getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(List<Integer> selectedDays) {
        this.selectedDays = selectedDays;
    }

    public boolean isAllDaysSelected() {
        return allDaysSelected;
    }

    public void setAllDaysSelected(boolean allDaysSelected) {
        this.allDaysSelected = allDaysSelected;
    }
}
