package com.example.alarmjava20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TimeAcceptanceActivity extends AppCompatActivity {
    private int initialHourOfDay;
    private int initialMinute;

    private TextView textViewTime;
    private Button buttonAccept;
    private Button buttonPostpone;
    private ImageView imageViewCaptcha;
    private EditText editTextCaptcha;
    private MyAlarmManager alarmManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_control_layout);

        textViewTime = findViewById(R.id.textViewTime);
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonPostpone = findViewById(R.id.buttonPostpone);
        imageViewCaptcha = findViewById(R.id.imageViewCaptcha);
        editTextCaptcha = findViewById(R.id.editTextCaptcha);

        // Инициализация MyAlarmManager
        alarmManager = new MyAlarmManager(this);
        updateTextViewTime();

        // Генерируем и отображаем капчу
        Bitmap captchaBitmap = CaptchaGenerator.generateCaptchaBitmap();
        imageViewCaptcha.setImageBitmap(captchaBitmap);

        // Добавляем обработчик нажатия на кнопку "Accept"
        buttonAccept.setOnClickListener(view -> {
            // Проверяем, соответствует ли введенный текст капче
            String enteredCaptcha = editTextCaptcha.getText().toString();
            String captcha = CaptchaGenerator.getLastGeneratedCaptcha();
            if (enteredCaptcha.equals(captcha)) {
                // Останавливаем музыку только при успешной проверке капчи
                AlarmMediaPlayer.getInstance(TimeAcceptanceActivity.this).stop();

                // Возвращаемся в MainActivity
                Intent mainIntent = new Intent(TimeAcceptanceActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Закрываем текущую активность
            } else {
                // Обработка неверной капчи
                // В данном примере просто выводим сообщение об ошибке в консоль
                System.out.println("Неверная капча! Пожалуйста, повторите ввод.");

                // Очищаем поле ввода капчи
                editTextCaptcha.setText("");

                // Обновляем изображение капчи
                Bitmap newCaptchaBitmap = CaptchaGenerator.generateCaptchaBitmap();
                imageViewCaptcha.setImageBitmap(newCaptchaBitmap);
            }
        });

        // Добавляем обработчик нажатия на кнопку "Postpone"
        buttonPostpone.setOnClickListener(view -> {
            // Останавливаем музыку
            AlarmMediaPlayer.getInstance(TimeAcceptanceActivity.this).stop();

            // Получаем данные из Intent
            int alarmId = getIntent().getIntExtra("alarmId", -1);
            int hourOfDay = getIntent().getIntExtra("hourOfDay", -1);
            int minute = getIntent().getIntExtra("minute", -1);

            // Прибавляем пять минут
            minute += 1;
            if (minute >= 60) {
                minute -= 60;
                hourOfDay += 1;
                if (hourOfDay == 24) {
                    hourOfDay = 0;
                }
            }

            // Создаем новый объект Alarm с измененным временем
            Alarm newAlarm = new Alarm(alarmId, hourOfDay, minute);

            // Переносим новый будильник в MyAlarmManager
            alarmManager.scheduleAlarm(newAlarm);


            // Возвращаемся в MainActivity
            Intent mainIntent = new Intent(TimeAcceptanceActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish(); // Закрываем текущую активность
        });

        editTextCaptcha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Закрываем клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextCaptcha.getWindowToken(), 0);


                handleCaptcha();

                return true; // Событие обработано
            }
            return false; // Событие не обработано
        });

        editTextCaptcha.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Закрываем клавиатуру
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextCaptcha.getWindowToken(), 0);


                handleCaptcha();

                return true; // Событие обработано
            }
            return false; // Событие не обработано
        });

    }

    private void handleCaptcha() {
        String enteredCaptcha = editTextCaptcha.getText().toString();
        String captcha = CaptchaGenerator.getLastGeneratedCaptcha();
        if (enteredCaptcha.equals(captcha)) {
            AlarmMediaPlayer.getInstance(TimeAcceptanceActivity.this).stop();
            Intent mainIntent = new Intent(TimeAcceptanceActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        } else {
            System.out.println("Неверная капча! Пожалуйста, повторите ввод.");
            editTextCaptcha.setText("");
            Bitmap newCaptchaBitmap = CaptchaGenerator.generateCaptchaBitmap();
            imageViewCaptcha.setImageBitmap(newCaptchaBitmap);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Обновляем текущий Intent
        updateTextViewTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTextViewTime();
    }

    private void updateTextViewTime() {
        // Получаем Intent, который вызвал эту активность
        Intent intent = getIntent();

        // Получаем данные из Intent
        int alarmId = intent.getIntExtra("alarmId", -1);
        int hourOfDay = intent.getIntExtra("hourOfDay", -1);
        int minute = intent.getIntExtra("minute", -1);

        // Теперь вы можете использовать полученные значения, например, для отображения в TextView
        if (alarmId != -1 && hourOfDay != -1 && minute != -1) {
            String timeText = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            textViewTime.setText(timeText);
        }

    }


}
