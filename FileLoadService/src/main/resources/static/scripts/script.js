const BASE_URL = 'http://localhost:3000/api';
let currentUser = null;
let currentToken = null;

async function register() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const statusDiv = document.getElementById('authStatus');

    if (!username || !password) {
        showStatus(statusDiv, 'Заполните все поля', 'error');
        return;
    }

    try {
        showStatus(statusDiv, 'Регистрация...', '');

        const response = await fetch(`${BASE_URL}/auth`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, action: 'register' })
        });

        const result = await response.json();

        if (result.success) {
            showStatus(statusDiv, '✅ Регистрация успешна! Теперь войдите.', 'success');
            document.getElementById('username').value = '';
            document.getElementById('password').value = '';
        } else {
            showStatus(statusDiv, '❌ ' + result.error, 'error');
        }
    } catch (error) {
        showStatus(statusDiv, '❌ Ошибка регистрации', 'error');
    }
}

async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const statusDiv = document.getElementById('authStatus');

    if (!username || !password) {
        showStatus(statusDiv, 'Заполните все поля', 'error');
        return;
    }

    try {
        showStatus(statusDiv, 'Вход...', '');

        const response = await fetch(`${BASE_URL}/auth`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, action: 'login' })
        });

        const result = await response.json();

        if (result.success) {
            currentUser = result.username;
            currentToken = result.token;
            updateUI();
            showStatus(statusDiv, '✅ Вход выполнен!', 'success');
            document.getElementById('username').value = '';
            document.getElementById('password').value = '';
            loadStats();
        } else {
            showStatus(statusDiv, '❌ ' + result.error, 'error');
        }
    } catch (error) {
        showStatus(statusDiv, '❌ Ошибка входа', 'error');
    }
}

async function logout() {
    if (currentToken) {
        try {
            await fetch(`${BASE_URL}/auth`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ action: 'logout', token: currentToken })
            });
        } catch (error) {
            console.log('Ошибка выхода:', error);
        }
    }

    currentUser = null;
    currentToken = null;
    updateUI();
}

function updateUI() {
    const authSection = document.getElementById('authSection');
    const userSection = document.getElementById('userSection');
    const uploadSection = document.getElementById('uploadSection');
    const currentUserSpan = document.getElementById('currentUser');

    if (currentUser) {
        authSection.style.display = 'none';
        userSection.style.display = 'block';
        uploadSection.style.display = 'block';
        currentUserSpan.textContent = currentUser;
    } else {
        authSection.style.display = 'block';
        userSection.style.display = 'none';
        uploadSection.style.display = 'none';
        document.getElementById('resultSection').style.display = 'none';
    }
}

async function uploadFile() {
    if (!currentToken) {
            showStatus(document.getElementById('uploadStatus'), '❌ Требуется авторизация', 'error');
            return;
    }
    const fileInput = document.getElementById('fileInput');
    const statusDiv = document.getElementById('uploadStatus');

    if (!fileInput.files.length) {
        showStatus(statusDiv, 'Выберите файл', 'error');
        return;
    }

    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append('file', file);

    try {
        showStatus(statusDiv, 'Загрузка...', '');

        const response = await fetch(`${BASE_URL}/upload`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${currentToken}`
            },
            body: formData
        });

        const result = await response.json();

        if (result.success) {
            showDownloadLink(result.fileInfo.downloadUrl);
            showStatus(statusDiv, '✅ Файл успешно загружен!', 'success');
            fileInput.value = '';
            loadStats();
        } else {
            showStatus(statusDiv, '❌ Ошибка: ' + result.error, 'error');
        }
    } catch (error) {
        showStatus(statusDiv, '❌ Ошибка загрузки: ' + error.message, 'error');
    }
}

function showDownloadLink(downloadPath) {
    const resultSection = document.getElementById('resultSection');
    const downloadLink = document.getElementById('downloadLink');

    downloadLink.value = `http://localhost:3000${downloadPath}`;
    resultSection.style.display = 'block';

    resultSection.scrollIntoView({ behavior: 'smooth' });
}

function copyLink() {
    const linkInput = document.getElementById('downloadLink');
    linkInput.select();
    document.execCommand('copy');

    const button = event.target;
    const originalText = button.textContent;
    button.textContent = '✅ Скопировано!';
    setTimeout(() => {
        button.textContent = originalText;
    }, 2000);
}

async function loadStats() {
    const statsDiv = document.getElementById('stats');
    if (!currentToken) {
            statsDiv.innerHTML = '<p>Для просмотра статистики требуется авторизация</p>';
            return;
        }
    try {
        statsDiv.innerHTML = '<div class="loading">Загрузка статистики...</div>';
        const response = await fetch(`${BASE_URL}/stats`, {
                    headers: {
                        'Authorization': `Bearer ${currentToken}`
                    }
                });

        if (response.status === 401) {
             statsDiv.innerHTML = '<div class="error">❌ Требуется авторизация</div>';
             return;
        }
        const stats = await response.json();

        displayStats(stats);
    } catch (error) {
        statsDiv.innerHTML = '<div class="error">❌ Ошибка загрузки статистики</div>';
    }
}

function displayStats(stats) {
    const statsDiv = document.getElementById('stats');

    let html = `
        <div class="stats-grid">
            <div class="stat-card">
                <span class="stat-number">${stats.totalFiles}</span>
                <span class="stat-label">Всего файлов</span>
            </div>
            <div class="stat-card">
                <span class="stat-number">${stats.totalDownloads}</span>
                <span class="stat-label">Всего скачиваний</span>
            </div>
        </div>
    `;

    if (stats.files && stats.files.length > 0) {
        html += '<h3>Загруженные файлы:</h3>';
        stats.files.forEach(file => {
            const uploadDate = new Date(file.uploadDate).toLocaleDateString('ru-RU');
            const lastDownload = file.lastDownload !== 'никогда'
                ? new Date(file.lastDownload).toLocaleDateString('ru-RU')
                : 'никогда';
            const downloadUrl = file.downloadUrl || `/download/${file.fileName}`;
            const linkInput = document.getElementById('downloadLink');

            html += `
                <div class="file-item">
                    <div class="file-name">${file.originalName}</div>
                    <div class="file-meta">
                        📅 Загружен: ${uploadDate} |
                        ⬇️ Скачиваний: ${file.downloadCount} |
                        📆 Последнее: ${lastDownload}
                    </div>
                </div>
            `;
        });
    } else {
        html += '<p>Файлы еще не загружены</p>';
    }

    statsDiv.innerHTML = html;
}

function showStatus(element, message, type) {
    element.textContent = message;
    element.className = `status ${type}`;
}
loadStats();