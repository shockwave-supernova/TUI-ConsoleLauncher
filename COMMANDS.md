# Руководство по сборке, отладке и командам (`com.tui.next`)

## 1. Сборка APK

Для сборки полной версии `fdroidDebug` (включающей модули телефонных звонков и SMS):

```bash
chmod +x gradlew
./gradlew assembleFdroidDebug
```

Для полной пересборки с очисткой кэша:

```bash
./gradlew clean assembleFdroidDebug
```

Путь к собранному APK:
`app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk`

---

## 2. Установка и отладка через ADB

```bash
# Установка поверх существующей версии с сохранением данных
adb install -r app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk

# Выдача разрешения MANAGE_EXTERNAL_STORAGE через ADB (опционально)
adb shell appops set com.tui.next MANAGE_EXTERNAL_STORAGE allow

# Просмотр логов приложения в реальном времени
adb logcat | grep "com.tui.next"

# Удаление приложения
adb uninstall com.tui.next
```

---

## 3. Примеры использования команд T-UI Next

### Запуск Gemini и приложений
```text
$ gemini
$ Settings
$ Chrome
```

### Настройка темы и прозрачности
```text
$ theme -preset cyberpunk
$ config -set system_wallpaper true
$ config -fontsize 15
$ restart
```

### Работа с музыкой, вибрацией и RSS
```text
$ music -ls
$ music -play
$ music -next
$ vibrate 300
$ vibrate 0,200,100,200
$ rss -add 1 3600 https://news.ycombinator.com/rss
$ rss -l 1
```

### Работа с файлами и редактором
```text
$ ls
$ tuixt notes.txt
$ open document.pdf
$ share photo.jpg
```
