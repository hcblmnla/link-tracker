![Build](https://github.com/central-university-dev/java-hcblmnla/actions/workflows/build.yaml/badge.svg)

# Link Tracker

Студент – Серов Даниил Алексеевич, проект сделан в рамках Академии Бэкенда.

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 23` с использованием `Spring Boot 3`.
Проект состоит из 2-х приложений:

* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.
