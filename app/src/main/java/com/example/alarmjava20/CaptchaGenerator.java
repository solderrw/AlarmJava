    package com.example.alarmjava20;

    import android.graphics.Bitmap;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;

    import java.security.SecureRandom;

    public class CaptchaGenerator {

        private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Убраны похожие символы для улучшения читаемости
        private static final int CAPTCHA_LENGTH = 6;  // Длина капчи
        private static final int IMAGE_WIDTH = 352;
        private static final int IMAGE_HEIGHT = 151;

        private static String lastGeneratedCaptcha; // Переменная для хранения последней сгенерированной капчи

        public static Bitmap generateCaptchaBitmap() {
            // Используем SecureRandom для криптографически безопасной генерации
            SecureRandom secureRandom = new SecureRandom();

            // Создаем изображение
            Bitmap captchaBitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(captchaBitmap);

            // Заполняем фон белым цветом
            canvas.drawColor(Color.WHITE);

            // Настраиваем Paint для текста и линий
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(36);
            paint.setAntiAlias(true);

            // Генерируем текст капчи с искажением
            StringBuilder captcha = new StringBuilder();
            for (int i = 0; i < CAPTCHA_LENGTH; i++) {
                int randomIndex = secureRandom.nextInt(CHARACTERS.length());
                char randomChar = CHARACTERS.charAt(randomIndex);
                captcha.append(randomChar);

                // Рисуем символы на изображении с добавлением искажения
                float x = i * (IMAGE_WIDTH / CAPTCHA_LENGTH) + 20 + secureRandom.nextInt(10); // Расположение символов с добавлением случайного смещения
                float y = IMAGE_HEIGHT / 2 + 10 + secureRandom.nextInt(20); // Изменение вертикального положения символов
                float rotation = secureRandom.nextInt(40) - 20; // Случайное вращение символов

                // Применяем вращение и рисуем символ
                canvas.save();
                canvas.rotate(rotation, x, y);
                canvas.drawText(String.valueOf(randomChar), x, y, paint);
                canvas.restore();
            }

            // Добавим более сильное искажение линиями на изображение
            for (int i = 0; i < 200; i++) {
                int startX = secureRandom.nextInt(IMAGE_WIDTH);
                int startY = secureRandom.nextInt(IMAGE_HEIGHT);
                int endX = startX + secureRandom.nextInt(40) - 20;
                int endY = startY + secureRandom.nextInt(40) - 20;
                paint.setStrokeWidth(secureRandom.nextFloat() * 3); // Изменение толщины линии
                paint.setColor(Color.rgb(secureRandom.nextInt(256), secureRandom.nextInt(256), secureRandom.nextInt(256))); // Случайный цвет линии
                canvas.drawLine(startX, startY, endX, endY, paint);
            }

            // Добавим еще больше шума на изображение
            for (int i = 0; i < 2000; i++) {
                int x = secureRandom.nextInt(IMAGE_WIDTH);
                int y = secureRandom.nextInt(IMAGE_HEIGHT);
                captchaBitmap.setPixel(x, y, Color.rgb(secureRandom.nextInt(256), secureRandom.nextInt(256), secureRandom.nextInt(256)));
            }

            lastGeneratedCaptcha = captcha.toString(); // Сохраняем последнюю сгенерированную капчу
            return captchaBitmap;
        }

        public static String getLastGeneratedCaptcha() {
            return lastGeneratedCaptcha;
        }
    }