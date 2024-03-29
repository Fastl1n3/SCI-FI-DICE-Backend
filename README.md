# SCI-FI-DICE-Backend
### Краткое описание
Содержит всю бизнес логику и объединяет все компоненты системы автоматизации антикафе.
### Технологии
* Spring
* PostgreSQL
* Docker
* GitHub Actions
### Функциональные требования
1. Получение от нейросети изображения и количества людей каждой комнаты
2. Контроль над количеством человек в комнатах
3. Контроль над окончанием забронированного времени
4. Отправка всех пользовательских действий и нарушений администратору
5. Обработка запросов информационного бота и бота бронирования
6. Обработка запросов с ресепшена
### Реализованный функционал
1. Получение от нейросети количества людей в каждой комнате и сравнение со значением введенным пользователем при оплате. При превышении количества человек отправка предупреждения посетителям
2. Перенаправление изображения от нейройсети на панель администратора
3. Проверка времени с некоторой периодичностью. При превышении времени нахождения в комнате отправка предупреждения посетителям
4. Любое действие пользователей отправляется администратору
5. Оповещение администратора о всех нарушениях
6. Отправка боту бронирования информации о свободных часах комнаты
7. Бронирование комнаты и отправка номера брони
8. Отправка правил игр информационному боту
9. Проверка кода бронирования с ресепшена
10. Получение числа человек, которые будут в комнате, и бронирование игр
11. Отправка кода от комнаты
