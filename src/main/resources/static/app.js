const API_URL = 'http://localhost:8080';

// ============= Utility Functions =============
function log(message, data = null) {
    const logsDiv = document.getElementById('logs');
    const timestamp = new Date().toLocaleTimeString();
    const entry = document.createElement('div');
    entry.className = 'log-entry';
    entry.innerHTML = `<span class="timestamp">${timestamp}</span> ${message}`;
    if (data) {
        entry.innerHTML += `<pre>${JSON.stringify(data, null, 2)}</pre>`;
    }
    logsDiv.insertBefore(entry, logsDiv.firstChild);
}

function clearLogs() {
    document.getElementById('logs').innerHTML = '';
}

async function checkBackendStatus() {
    try {
        const res = await fetch(`${API_URL}/books`, { 
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });
        const statusEl = document.getElementById('status');
        if (res.ok) {
            statusEl.textContent = '✅ Online';
            statusEl.className = 'status-online';
            log('✅ Backend is online');
        } else {
            throw new Error(`HTTP ${res.status}`);
        }
    } catch (e) {
        const statusEl = document.getElementById('status');
        statusEl.textContent = '❌ Offline';
        statusEl.className = 'status-offline';
        log('❌ Backend offline: ' + e.message);
    }
}

async function apiCall(method, endpoint, body = null) {
    try {
        const options = {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        };
        if (body) options.body = JSON.stringify(body);

        const res = await fetch(`${API_URL}${endpoint}`, options);
        
        // Try to parse as JSON, fall back to text
        let data;
        const contentType = res.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            data = await res.json().catch(() => null);
        } else {
            data = await res.text();
        }
        
        if (!res.ok) {
            log(`❌ ${method} ${endpoint} - HTTP ${res.status}`, data);
            return { success: false, data };
        }
        
        log(`✅ ${method} ${endpoint}`, data);
        return { success: true, data };
    } catch (e) {
        log(`❌ Error: ${e.message}`);
        return { success: false, data: null };
    }
}

function displayResults(elementId, result) {
    const el = document.getElementById(elementId);
    if (!result.success && !result.data) {
        el.innerHTML = '<p class="error">Error or no response</p>';
        return;
    }
    el.innerHTML = `<pre>${JSON.stringify(result.data, null, 2)}</pre>`;
}

// ============= Books =============
async function loadBooks() {
    const result = await apiCall('GET', '/books');
    displayResults('books-list', result);
}

function showAddBookForm() {
    document.getElementById('books-form').style.display = 'block';
}

function hideAddBookForm() {
    document.getElementById('books-form').style.display = 'none';
    document.getElementById('book-title').value = '';
}

async function addBook() {
    const title = document.getElementById('book-title').value;

    if (!title.trim()) {
        log('⚠️ Please enter a book title');
        return;
    }

    const result = await apiCall('POST', '/admin/books', { title });
    if (result.success) {
        hideAddBookForm();
        await loadBooks();
    }
}

// ============= Users =============
async function registerUser() {
    const name = document.getElementById('user-name').value;

    if (!name.trim()) {
        log('⚠️ Please enter a user name');
        return;
    }

    const result = await apiCall('POST', '/users', { name });
    if (result.success) {
        log('✅ User registered successfully');
        document.getElementById('user-name').value = '';
        
        // Extract user ID from response like "User registered: Adam12345 (ID: 123)"
        const match = result.data.match(/ID:\s*(\d+)/);
        if (match) {
            const userId = match[1];
            document.getElementById('user-id-query').value = userId;
            showUserRegistrationResult(result.data, userId);
        } else {
            showUserRegistrationResult(result.data, null);
        }
    }
}

function showUserRegistrationResult(message, userId) {
    const resultDiv = document.getElementById('user-registration-result');
    let html = `<p class="success">✅ ${message}</p>`;
    if (userId) {
        html += `<p>User ID <strong>${userId}</strong> is now in the "Get User Details" field.</p>`;
        html += `<button onclick="getUser()">Click to fetch user details</button>`;
    }
    resultDiv.innerHTML = html;
}

async function getUser() {
    const userId = document.getElementById('user-id-query').value;

    if (!userId || userId <= 0) {
        log('⚠️ Please enter a valid user ID');
        return;
    }

    const result = await apiCall('GET', `/users/${userId}`);
    displayResults('user-details', result);
}

// ============= Checkouts & Returns =============
async function checkoutBook() {
    const userId = parseInt(document.getElementById('checkout-user-id').value);
    const bookId = parseInt(document.getElementById('checkout-book-id').value);

    if (!userId || !bookId || userId <= 0 || bookId <= 0) {
        log('⚠️ Please enter valid user ID and book ID');
        return;
    }

    const result = await apiCall('POST', '/checkout', { userId, bookId });
    displayResults('checkout-result', result);
    if (result.success) {
        document.getElementById('checkout-user-id').value = '';
        document.getElementById('checkout-book-id').value = '';
    }
}

async function returnBook() {
    const userId = parseInt(document.getElementById('return-user-id').value);
    const bookId = parseInt(document.getElementById('return-book-id').value);

    if (!userId || !bookId || userId <= 0 || bookId <= 0) {
        log('⚠️ Please enter valid user ID and book ID');
        return;
    }

    const result = await apiCall('POST', '/return', { userId, bookId });
    displayResults('checkout-result', result);
    if (result.success) {
        document.getElementById('return-user-id').value = '';
        document.getElementById('return-book-id').value = '';
    }
}

// ============= Custom Request =============
async function sendCustomRequest() {
    const method = document.getElementById('method').value;
    const endpoint = document.getElementById('endpoint').value;
    const bodyText = document.getElementById('body').value;
    
    let body = null;
    if (bodyText) {
        try {
            body = JSON.parse(bodyText);
        } catch {
            log('❌ Invalid JSON in body');
            return;
        }
    }

    const result = await apiCall(method, endpoint, body);
    displayResults('custom-response', result);
}

// ============= Init =============
window.addEventListener('load', () => {
    checkBackendStatus();
    setInterval(checkBackendStatus, 5000); // Check every 5 seconds
});
