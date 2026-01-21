// API helper functions
const API_BASE = '/tasks';

const api = {
    // Получить все задачи
    getAllTasks: async () => {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error('Ошибка загрузки задач');
        return await response.json();
    },

    // Получить задачу по ID
    getTaskById: async (id) => {
        const response = await fetch(`${API_BASE}/${id}`);
        if (!response.ok) throw new Error('Задача не найдена');
        return await response.json();
    },

    // Получить завершенные задачи
    getCompletedTasks: async () => {
        const response = await fetch(`${API_BASE}/completed`);
        if (!response.ok) throw new Error('Ошибка загрузки');
        return await response.json();
    },

    // Получить незавершенные задачи
    getIncompleteTasks: async () => {
        const response = await fetch(`${API_BASE}/incomplete`);
        if (!response.ok) throw new Error('Ошибка загрузки');
        return await response.json();
    },

    // Получить задачи по дню недели
    getTasksByDay: async (dayOfWeek) => {
        const response = await fetch(`${API_BASE}/day/${dayOfWeek}`);
        if (!response.ok) throw new Error('Ошибка загрузки');
        return await response.json();
    },

    // Создать новую задачу
    createTask: async (description, dayOfWeek) => {
        const response = await fetch(API_BASE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ description, dayOfWeek })
        });
        if (!response.ok) throw new Error('Ошибка создания задачи');
        return await response.json();
    },

    // Обновить описание задачи
    updateTaskDescription: async (id, description) => {
        const response = await fetch(`${API_BASE}/${id}/description`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ description })
        });
        if (!response.ok) throw new Error('Ошибка обновления');
        return await response.json();
    },

    // Отметить задачу как выполненную
    completeTask: async (id) => {
        const response = await fetch(`${API_BASE}/${id}/complete`, {
            method: 'PATCH'
        });
        if (!response.ok) throw new Error('Ошибка выполнения');
    },

    // Отменить выполнение задачи
    uncompleteTask: async (id) => {
        const response = await fetch(`${API_BASE}/${id}/uncomplete`, {
            method: 'PATCH'
        });
        if (!response.ok) throw new Error('Ошибка отмены');
    },

    // Удалить задачу
    deleteTask: async (id) => {
        const response = await fetch(`${API_BASE}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Ошибка удаления');
    },

    // Удалить все завершенные задачи
    deleteCompletedTasks: async () => {
        const response = await fetch(`${API_BASE}/completed`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('Ошибка удаления');
        return await response.json();
    },

    // Получить статистику
    getStatistics: async () => {
        const response = await fetch(`${API_BASE}/statistics`);
        if (!response.ok) throw new Error('Ошибка загрузки статистики');
        return await response.json();
    }
};

// Helper функции
const utils = {
    // Форматирование даты
    formatDate: (dateString) => {
        if (!dateString) return '-';
        const date = new Date(dateString);
        return date.toLocaleString('ru-RU', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    // Показать уведомление об успехе
    showSuccess: (message) => {
        const alert = document.createElement('div');
        alert.className = 'success';
        alert.textContent = message;
        document.querySelector('.content').prepend(alert);
        setTimeout(() => alert.remove(), 3000);
    },

    // Показать ошибку
    showError: (message) => {
        const alert = document.createElement('div');
        alert.className = 'error';
        alert.textContent = message;
        document.querySelector('.content').prepend(alert);
        setTimeout(() => alert.remove(), 5000);
    },

    // Показать индикатор загрузки
    showLoading: (container) => {
        container.innerHTML = '<div class="loading">Загрузка...</div>';
    },

    // Показать пустое состояние
    showEmpty: (container, message) => {
        container.innerHTML = `
            <div class="empty-state">
                <h3>${message}</h3>
                <p>Добавьте новую задачу, чтобы начать</p>
            </div>
        `;
    }
};
