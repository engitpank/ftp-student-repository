# FTP-STUDENT-REPOSITORY

___

## [__Исполняемый jar-файл клиента__]()

## Инструкция по сборке проекта

1. Клонируем проект в рабочую директорию
   `https://github.com/engitpank/ftp-student-repository.git`
2. Переходим в директорию проекта и создаём папку build
3. Компилируем исходных код в папку build: `javac -d build -sourcepath src/ src/main/ru/student_repository/CliApp.java`
4. Переходим в директорию build и создаём MANIFEST.MF со следующим
   содержимым: `Main-Class: main.ru.student_repository.CliApp содержимое MANIFEST.MF`
5. Собираем
   jar-файл: `jar cmvf MANIFEST.MF App.jar main/ru/student_repository/*.class main/ru/student_repository/**/*.class main/ru/student_repository/**/**/*.class`
6. Запускаем jar-файл: `java -jar App.jar`

## Инструкция по работе с приложением

При запуске можно указать следующие аргументы:

* __-user__ username
* __-pass__ password
* __-host__ 194.87.147.97:21 или c портом по умолчанию 194.87.147.97
* __-f__ filename.json - файл, расположенный на ftp-сервере. По умолчанию index.json
* __-adm__ 5050 - порт для активного соединения. По умолчанию используется пассивное соединение.

Команда без аргументов `java -jar App.jar` предложит ввести минимальные данные: username, password, host. Или введите
exit для выхода.

После авторизации появится список доступных команд. Сначала отправляется запрос с командой, а потом подаётся аргумент:

`Enter command:` `add`

`Add student with name:` `Bob`

`Student #31: Bob saved success`

[## Исполняемый jar-файл автотестов]()

## Инструкция по запуску тестов и кратким обоснованием тестов


