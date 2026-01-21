let currentFilter = 'all';
let allTasks = [];

// Загрузить задачи
async function loadTasks() {
    const container = document.getElementById('tasks-container');
    utils.showLoading(container);

    try {
        allTasks = await api.getAllTasks();
        renderTasks();
    } catch (error) {
        utils.showError('Ошибка загрузки задач: ' + error.message);
        container.innerHTML = '';
    }
}

// Отрисовать задачи
function renderTasks() {
    const container = document.getElementById('tasks-container');
    
    let tasksToShow = allTasks;
    
    // Применить фильтр
    if (currentFilter === 'completed') {
        tasksToShow = allTasks.filter(t => t.completed);
    } else if (currentFilter === 'incomplete') {
        tasksToShow = allTasks.filter(t => !t.completed);
    }

    if (tasksToShow.length === 0) {
        utils.showEmpty(container, 'Задач не найдено');
        return;
    }

    container.innerHTML = tasksToShow.map(task => createTaskElement(task)).join('');
}

// Создать HTML элемент задачи
function createTaskElement(task) {
    const completedClass = task.completed ? 'completed' : '';
    const dayNames = {
        'Monday': 'Пн',
        'Tuesday': 'Вт',
        'Wednesday': 'Ср',
        'Thursday': 'Чт',
        'Friday': 'Пт',
        'Saturday': 'Сб',
        'Sunday': 'Вс'
    };

    return `
        <div class="task-item ${completedClass}">
            <div class="task-info">
                <div class="task-description">${task.description}</div>
                <div class="task-meta">
                    ${task.dayOfWeek ? `<span class="task-day">${dayNames[task.dayOfWeek] || task.dayOfWeek}</span>` : ''}
                    <span>Создано: ${utils.formatDate(task.createdAt)}</span>
                    ${task.completedAt ? `<span>Завершено: ${utils.formatDate(task.completedAt)}</span>` : ''}
                </div>
            </div>
            <div class="task-actions">
                ${!task.completed ? 
                    `<button class="btn btn-success btn-sm" onclick="completeTask(${task.id})">✔ Выполнено</button>` :
                    `<button class="btn btn-warning btn-sm" onclick="uncompleteTask(${task.id})">↺ Вернуть</button>`
                }
                <button class="btn btn-danger btn-sm" onclick="deleteTask(${task.id})">🗑️ Удалить</button>
            </div>
        </div>
    `;
}

// Добавить задачу
async function addTask() {
    const description = document.getElementById('task-description').value.trim();
    const dayOfWeek = document.getElementById('task-day').value;

    if (!description) {
        utils.showError('Введите описание задачи');
        return;
    }

    try {
        await api.createTask(description, dayOfWeek || null);
        utils.showSuccess('Задача успешно добавлена!');
        
        // Очистить форму
        document.getElementById('task-description').value = '';
        document.getElementById('task-day').value = '';
        
        // Перезагрузить список
        await loadTasks();
    } catch (error) {
        utils.showError('Ошибка добавления задачи: ' + error.message);
    }
}

// Отметить задачу как выполненную
async function completeTask(id) {
    try {
        await api.completeTask(id);
        utils.showSuccess('Задача отмечена как выполненная!');
        await loadTasks();
    } catch (error) {
        utils.showError('Ошибка: ' + error.message);
    }
}

// Отменить выполнение
async function uncompleteTask(id) {
    try {
        await api.uncompleteTask(id);
        utils.showSuccess('Выполнение задачи отменено');
        await loadTasks();
    } catch (error) {
        utils.showError('Ошибка: ' + error.message);
    }
}

// Удалить задачу
async function deleteTask(id) {
    if (!confirm('Вы уверены, что хотите удалить эту задачу?')) {
        return;
    }

    try {
        await api.deleteTask(id);
        utils.showSuccess('Задача успешно удалена');
        await loadTasks();
    } catch (error) {
        utils.showError('Ошибка удаления: ' + error.message);
    }
}

// Удалить все завершенные
async function deleteAllCompleted() {
    const completedCount = allTasks.filter(t => t.completed).length;
    
    if (completedCount === 0) {
        utils.showError('Нет завершенных задач для удаления');
        return;
    }

    if (!confirm(`Удалить все ${completedCount} завершенных задач?`)) {
        return;
    }

    try {
        const result = await api.deleteCompletedTasks();
        utils.showSuccess(`Удалено задач: ${result.deleted}`);
        await loadTasks();
    } catch (error) {
        utils.showError('Ошибка удаления: ' + error.message);
    }
}

// Фильтровать задачи
function filterTasks(filter) {
    currentFilter = filter;
    
    // Обновить кнопки фильтров
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    renderTasks();
}

// Загрузить при открытии страницы
document.addEventListener('DOMContentLoaded', loadTasks);

// Enter для добавления задачи
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('task-description').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addTask();
        }
    });
});
