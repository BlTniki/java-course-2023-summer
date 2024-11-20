![Bot](https://github.com/sanyarnd/java-course-2023-backend-template/actions/workflows/bot.yml/badge.svg)
![Scrapper](https://github.com/sanyarnd/java-course-2023-backend-template/actions/workflows/scrapper.yml/badge.svg)

# Link Tracker
![Logo](logo.jpg)

ФИО: `Шакаримов Дмитрий Даулетович`

Бот: https://t.me/Scrapper9000Bot

Приложение для отслеживания обновлений контента по ссылкам.
При появлении новых событий отправляется уведомление в Telegram.

Проект написан на `Java 21` с использованием `Spring Boot 3`.

Проект состоит из 2-х приложений:
* Bot
* Scrapper

Для работы требуется БД `PostgreSQL`. Присутствует опциональная зависимость на `Kafka`.
Реализовано:
- работа с бд на выбор: jdbc, jooq, jpa
- тг клиент и REST бек
- различные стратегии retry и rate limiting при помощи bucket4j
- асинхронное сообщение при помощи  Kafka
- использование инструментов мониторинга (Prometheus, Grafana)
- сборка контейнеров
- покрытие интеграционными и юнит тестами с использованием wiremock
автоматическая сборка и прогон тестами в github action
