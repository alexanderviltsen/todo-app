# Todo API Documentation

## Обзор добавленных методов

В приложение добавлены следующие улучшения:

### Логирование
- Добавлено подробное логирование во всех слоях (Service, Controller)
- Логи каждого метода включают информацию о выполняемых операциях
- Различные уровни логов: INFO, WARN, ERROR

### Временные метки
- `createdAt` - время создания задачи
- `updatedAt` - время последнего обновления
- `completedAt` - время завершения задачи

---

## API Endpoints

### 1. Получить все задачи
```http
GET /tasks
```
Возвращает список всех задач.

---

### 2. Получить задачу по ID ✨ NEW
```http
GET /tasks/{id}
```

**Параметры:**
- `id` (path) - ID задачи

**Ответ:**
- 200 OK - задача найдена
- 404 Not Found - задача не найдена

---

### 3. Получить завершенные задачи ✨ NEW
```http
GET /tasks/completed
```
Возвращает только завершенные задачи.

---

### 4. Получить незавершенные задачи ✨ NEW
```http
GET /tasks/incomplete
```
Возвращает только незавершенные задачи.

---

### 5. Получить задачи по дню недели ✨ NEW
```http
GET /tasks/day/{dayOfWeek}
```

**Параметры:**
- `dayOfWeek` (path) - День недели (например: Monday, Tuesday)

**Пример:**
```http
GET /tasks/day/Monday
```

---

### 6. Создать новую задачу
```http
POST /tasks
Content-Type: application/json

{
  "description": "Описание задачи",
  "dayOfWeek": "Monday"
}
```

**Ответ:** 201 Created

---

### 7. Обновить описание задачи ✨ NEW
```http
PATCH /tasks/{id}/description
Content-Type: application/json

{
  "description": "Новое описание"
}
```

**Параметры:**
- `id` (path) - ID задачи

**Ответ:** 200 OK - обновленная задача

---

### 8. Отметить задачу как выполненную
```http
PATCH /tasks/{id}/complete
```

**Ответ:** 204 No Content

---

### 9. Отменить выполнение задачи ✨ NEW
```http
PATCH /tasks/{id}/uncomplete
```

Позволяет вернуть задачу в статус "не выполнена".

**Ответ:** 204 No Content

---

### 10. Удалить задачу ✨ NEW
```http
DELETE /tasks/{id}
```

**Параметры:**
- `id` (path) - ID задачи

**Ответ:** 204 No Content

---

### 11. Удалить все завершенные задачи ✨ NEW
```http
DELETE /tasks/completed
```

Удаляет все задачи со статусом "completed".

**Ответ:**
```json
{
  "deleted": 5
}
```

---

### 12. Получить статистику ✨ NEW
```http
GET /tasks/statistics
```

Возвращает статистику по задачам.

**Ответ:**
```json
{
  "total": 10,
  "completed": 3,
  "incomplete": 7
}
```

---

## Примеры использования

### Получить все незавершенные задачи на понедельник
```bash
curl http://localhost:8080/tasks/day/Monday
```

### Обновить описание задачи
```bash
curl -X PATCH http://localhost:8080/tasks/1/description \
  -H "Content-Type: application/json" \
  -d '{"description": "Купить продукты на неделю"}'
```

### Отменить выполнение
```bash
curl -X PATCH http://localhost:8080/tasks/1/uncomplete
```

### Удалить задачу
```bash
curl -X DELETE http://localhost:8080/tasks/1
```

### Получить статистику
```bash
curl http://localhost:8080/tasks/statistics
```

---

## Структура модели Task

```java
{
  "id": 1,
  "description": "Описание задачи",
  "dayOfWeek": "Monday",
  "completed": false,
  "createdAt": "2024-01-21T10:30:00",
  "updatedAt": "2024-01-21T15:45:00",
  "completedAt": null
}
```

---

## Логирование

Каждый метод содержит подробное логирование:

```
INFO  [TaskService] [getAllTasks] Запрос всех задач
INFO  [TaskService] [getAllTasks] Найдено задач: 10
INFO  [TaskController] GET /tasks - Запрос всех задач
```

Логи включают:
- Начало операции
- Параметры запроса
- Результат выполнения
- Ошибки и предупреждения

---

## Созданные методы в TaskService

1. `getAllTasks()` - получить все задачи
2. `getTaskById(Long id)` - получить задачу по ID ✨
3. `getCompletedTasks()` - получить завершенные задачи ✨
4. `getIncompleteTasks()` - получить незавершенные задачи ✨
5. `getTasksByDayOfWeek(String dayOfWeek)` - получить задачи по дню ✨
6. `addTask(TaskRequestDto dto)` - добавить задачу
7. `updateTaskDescription(Long id, String newDescription)` - обновить описание ✨
8. `completeTask(Long id)` - отметить как выполненную
9. `uncompleteTask(Long id)` - отменить выполнение ✨
10. `deleteTask(Long id)` - удалить задачу ✨
11. `deleteAllCompletedTasks()` - удалить все завершенные ✨
12. `getStatistics()` - получить статистику ✨

✨ - новые методы
