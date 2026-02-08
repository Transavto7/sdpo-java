# CLAUDE.md

Этот файл содержит руководство для Claude Code (claude.ai/code) при работе с кодом в данном репозитории.

## ВАЖНЫЕ ПРАВИЛА РАБОТЫ

### Выполнение команд в терминале

**ОБЯЗАТЕЛЬНОЕ ПРАВИЛО:** Перед выполнением ЛЮБЫХ команд в терминале или через Bash tool необходимо:

1. **Показать команду** которую планируется выполнить
2. **Описать на русском языке:**
   - Что эта команда делает
   - Зачем она нужна для выполнения текущей задачи
   - Какие изменения она может внести в систему
3. **Спросить разрешение** на выполнение у пользователя
4. **Дождаться подтверждения** от пользователя перед выполнением

**Исключения:** Это правило применяется ко ВСЕМ командам без исключений, включая:
- Команды сборки (gradle, npm)
- Команды git
- Команды чтения файлов через bash
- Любые другие команды в терминале

**Формат запроса разрешения:**
```
Планирую выполнить команду:
`команда здесь`

Описание: [что делает команда и зачем она нужна]

Можно ли выполнить эту команду?
```

## Обзор проекта

СДПО (SDPO) - это Java-приложение на базе Spring Boot для проведения предрейсовых медицинских осмотров водителей и технических осмотров транспортных средств. Приложение работает на Windows терминалах с подключенными медицинскими приборами (алкометр, термометр, тонометр) и периферийными устройствами (камера, принтер).

### Стек технологий
- **Backend**: Spring Boot 2.7.6, Java 11
- **Frontend**: Vue.js 3, Vue Router 4, Vuex 4
- **Сборка**: Gradle, Vue CLI
- **Интеграция с периферией**: JNA, JSSC (последовательные порты), JavaCV/OpenCV (камеры), нативные C++ библиотеки

### Профили
- `develop` - режим разработки, не требует подключения к периферии, запуск на localhost:8080
- `production` - боевой режим, требует административных прав, автоматически открывает браузер (Edge)

## Команды сборки и запуска

### Backend (Spring Boot)

**Режим разработки:**
```bash
./gradlew bootRun -Dprofile=develop
```

**Сборка для production:**
```bash
./gradlew bootJar -Dprofile=production
```

**Запуск JAR:**
```bash
java -jar build/libs/sdpo-java-<version>.jar
```

**Запуск тестов:**
```bash
./gradlew test
```

### Frontend (Vue.js)

**Development server** (запускается на порту 8090):
```bash
cd src/main/frontend
npm run serve
```

**Сборка для разработки:**
```bash
cd src/main/frontend
npm run build:dev
```

**Сборка для production** (результат в `src/main/resources/static`):
```bash
cd src/main/frontend
npm run build:prod
```

**Режим отслеживания изменений:**
```bash
cd src/main/frontend
npm run watch
```

**Линтер:**
```bash
cd src/main/frontend
npm run lint
```

### Инструменты проверки качества кода

**PMD статический анализ:**
```bash
./gradlew pmdMain
```

**Checkstyle:**
```bash
./gradlew checkstyleMain
```

**SpotBugs:**
```bash
./gradlew spotbugsMain
```

Все инструменты проверки качества настроены с `ignoreFailures = true` для удобства разработки.

## Архитектура

### Структура Backend

Java backend следует domain-driven структуре, организованной по функциональным модулям:

**Основные модули:**
- `Core/` - Инфраструктурный код
  - `Core/Framework/` - Spring конфигурация (WebSocket, CORS, Executor)
  - `Core/Network/` - HTTP клиент, коммуникация с API
  - `Core/FileSystem/` - Операции с файлами

**Доменные модули:**
- `Inspections/` - Процессы проведения осмотров
  - `Inspections/Drivers/` - Логика осмотра водителей
  - `Inspections/Employees/` - Логика осмотра сотрудников
  - `Inspections/Technical/` - Технический осмотр транспортных средств
  - Каждый домен обычно содержит: Http (контроллеры), Services, Queries, Repository, Storages

- `Settings/` - Конфигурация приложения
  - `Settings/Http/` - Контроллеры настроек
  - `Settings/Repository/` - Персистентность настроек
  - `Settings/CoreConfigurations/` - Абстракции конфигурации (FileConfiguration, MemoryConfiguration)

**Вспомогательные модули:**
- `helper/` - Хелперы для периферийных устройств (AlcometerHelper, ThermometerHelper, TonometerHelper, CameraHelpers, BrowserHelpers, PrinterHelpers)
- `lib/` - Платформо-специфичные интеграции (BluetoothServices, COMPortsServices)
- `storage/` - Классы хранения данных (DriverStorage, EmployeeStorage, InspectionStorage и др.)
- `task/` - Фоновые задачи (SaveStoreInspectionTask, MediaMakeTask)
- `websocket/` - WebSocket endpoints (AlcometrStatusEndPoint, VideoEndpoint)
- `util/` - Утилиты (SdpoLog, сервисы портов)

**Точки входа:**
- `SdpoApplication.java` - Главный Spring Boot application класс
- `Sdpo.java` - Компонент инициализации ядра

### Структура Frontend

Vue.js 3 приложение с Vue Router и Vuex:

**Ключевые директории:**
- `src/main/frontend/src/pages/` - Компоненты страниц
  - `inspection/` - Многошаговый процесс осмотра водителя (Step-driver, Step-photo, Step-tonometer, Step-thermometer, Step-alcometer, Step-sleep, Step-helth, Step-result)
  - `employee-inspection/` - Страницы осмотра сотрудников
  - `techical-inspection/` - Страницы технического осмотра
  - `settings/` - Страницы настроек и авторизации
  - `print/` - Страницы печати
  - `driver/` - Страницы связанные с водителями

- `src/main/frontend/src/components/` - Переиспользуемые Vue компоненты
- `src/main/frontend/src/helpers/` - Frontend утилиты
- `router.js` - Определение маршрутов с метаданными для навигации по шагам
- `store.js` - Управление состоянием через Vuex

**Результат сборки:**
Frontend собирается в `src/main/resources/static/` и отдаётся через Spring Boot.

### Интеграция с периферийными устройствами

Приложение интегрируется с физическим оборудованием:

**Методы коммуникации:**
- **Последовательные порты (COM)**: Алкометр, термометр через библиотеку JSSC
- **Bluetooth**: Обнаружение алкометра через пользовательские C++ DLL (`blecpp.dll`, `com-win-cpp.dll`)
- **Камера**: Захват видео через JavaCV/OpenCV, библиотека webcam-capture
- **Принтер**: Печать через Java AWT PrinterJob

**Платформо-специфичный код:**
- Нативные Windows библиотеки в `src/main/cpp/`
- `lib/BluetoothServices/WindowsBluetooth.java` - Интеграция с Windows Bluetooth
- `lib/COMPortsServices/WindowsCOMPorts.java` - Работа с COM портами в Windows
- Mock реализации для разработки (MockBluetooth)

### Процесс осмотра

Процесс осмотра следует паттерну многошагового визарда, определённому в метаданных роутера:
1. Информация о водителе (`step-driver`)
2. Фотография (`step-photo`)
3. Тип рейса (`step-ride`)
4. Артериальное давление (`step-tonometer`)
5. Температура (`step-thermometer`)
6. Тест на алкоголь (`step-alcometer`)
7. Вопрос о качестве сна (`step-sleep`)
8. Вопрос о здоровье (`step-helth`)
9. Результат (`step-result` или `step-result-employee`)

Каждый шаг имеет метаданные `visible`, `next`, `prev` и `number` для навигации и условного отображения.

### Конфигурация

**Настройки приложения:**
- Конфигурация подключения по умолчанию: `~/sdpo/configs/connect.json` (Linux) или `C:\Users\<Username>\AppData\Roaming\sdpo\configs\connect.json` (Windows)
- Настройки управляются через `SettingsContainer` и `SettingsFactory`
- Файлы конфигурации: абстракция `FileConfiguration`

**Управление портами и процессами:**
- Порт по умолчанию: 8080
- При запуске убивает существующий процесс на порту 8080 (`PortWorker.killProcessOnPort()`)
- Требует прав администратора в production режиме

### Поток данных

1. Backend загружает данные о водителях/сотрудниках/медиках/печатях из API при запуске
2. Данные кешируются в Storage классах (DriverStorage, EmployeeStorage и др.)
3. WebSocket endpoints предоставляют обновления статуса устройств в реальном времени
4. Фоновые задачи периодически сохраняют данные осмотров
5. Frontend взаимодействует через REST API и WebSocket

## Заметки для разработки

### Работа с периферийными устройствами

При изменении кода интеграции с устройствами:
- Сначала проверьте профиль: профиль `develop` использует mock реализации
- Хелперы устройств находятся в `helper/` (AlcometerHelper, ThermometerHelper и др.)
- Коммуникация через последовательный порт в `util/port/PortService/`
- Bluetooth коммуникация использует нативные C++ библиотеки через JNA

### Разработка Frontend

- Горячая перезагрузка frontend: используйте `npm run serve` в `src/main/frontend/`
- Production сборки должны идти в `src/main/resources/static/` через `npm run build:prod`
- Метаданные роутера контролируют видимость шагов на основе настроек
- Vuex store в `store.js` управляет глобальным состоянием

### Тестирование

Приложение имеет минимальное тестовое покрытие на данный момент (`SdpoApplicationTests.java`). При добавлении функций тесты не строго обязательны, но могут быть добавлены при необходимости.

### Русский язык

Этот codebase использует русский язык для:
- Пользовательских сообщений и текста UI
- Комментариев в коде (смешанный русский/английский)
- Сообщений в логах
- Git commit сообщений

Сохраняйте эту конвенцию при добавлении нового кода или сообщений.
