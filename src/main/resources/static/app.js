
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

function createDeleteButton(id, type) {
    const button = document.createElement('button');
    button.textContent = `🗑️ Delete`;
    button.onclick = () => confirmDelete(id, type);
    button.style.background = '#dc3545';
    button.style.marginLeft = '10px';
    return button;
}

async function confirmDelete(id, type) {
    const msg = type === 'user' 
        ? `Are you sure you want to delete user #${id}? This cannot be undone.`
        : `Are you sure you want to delete book #${id}? This cannot be undone.`;
    
    if (!confirm(msg)) {
        return;
    }

    const endpoint = type === 'user' ? `/users/${id}` : `/admin/books/${id}`;
    const result = await apiCall('DELETE', endpoint);
    
    if (result.success) {
        log(`✅ ${type === 'user' ? 'User' : 'Book'} deleted successfully`);
        if (type === 'user') {
            loadAllUsers();
            document.getElementById('user-id-query').value = '';
            document.getElementById('user-details').innerHTML = '';
        } else {
            loadBooks();
        }
    }
}

// ============= Books =============
async function loadBooks() {
    const result = await apiCall('GET', '/books');
    if (result.success && Array.isArray(result.data)) {
        const booksDiv = document.getElementById('books-list');
        booksDiv.innerHTML = '';
        const table = document.createElement('table');
        table.style.cssText = 'width: 100%; border-collapse: collapse; margin-top: 10px;';
        
        result.data.forEach(book => {
            const row = document.createElement('tr');
            row.style.cssText = 'border-bottom: 1px solid #ddd; padding: 10px 0;';
            const idCell = document.createElement('td');
            idCell.style.cssText = 'padding: 10px; text-align: left;';
            idCell.textContent = `#${book.id}`;
            const titleCell = document.createElement('td');
            titleCell.style.cssText = 'padding: 10px; text-align: left;';
            titleCell.textContent = book.title;
            const actionCell = document.createElement('td');
            actionCell.style.cssText = 'padding: 10px; text-align: right;';
            
            const deleteBtn = createDeleteButton(book.id, 'book');
            actionCell.appendChild(deleteBtn);
            
            row.appendChild(idCell);
            row.appendChild(titleCell);
            row.appendChild(actionCell);
            table.appendChild(row);
        });
        booksDiv.appendChild(table);
    } else {
        displayResults('books-list', result);
    }
}

async function loadBooksWithAvailability() {
    const result = await apiCall('GET', '/books/availability');
    const booksDiv = document.getElementById('books-list');

    if (!result.success || !Array.isArray(result.data)) {
        displayResults('books-list', result);
        return;
    }

    booksDiv.innerHTML = '';

    if (result.data.length === 0) {
        booksDiv.innerHTML = '<p class="info">No books found</p>';
        return;
    }

    const table = document.createElement('table');
    table.style.cssText = 'width: 100%; border-collapse: collapse; margin-top: 10px;';

    // Header row
    const headerRow = document.createElement('tr');
    headerRow.style.cssText = 'background: #f0f0f0; font-weight: bold;';
    ['ID', 'Title', 'Status', 'Checked Out To'].forEach(text => {
        const th = document.createElement('th');
        th.style.cssText = 'padding: 10px; text-align: left; border-bottom: 2px solid #ddd;';
        th.textContent = text;
        headerRow.appendChild(th);
    });
    table.appendChild(headerRow);

    result.data.forEach(book => {
        const row = document.createElement('tr');
        row.style.cssText = 'border-bottom: 1px solid #ddd;';

        const idCell = document.createElement('td');
        idCell.style.cssText = 'padding: 10px;';
        idCell.textContent = `#${book.bookId}`;

        const titleCell = document.createElement('td');
        titleCell.style.cssText = 'padding: 10px;';
        titleCell.textContent = book.title;

        const statusCell = document.createElement('td');
        statusCell.style.cssText = 'padding: 10px;';
        const badge = document.createElement('span');
        badge.className = book.status === 'Available' ? 'availability-badge available' : 'availability-badge checked-out';
        badge.textContent = book.status === 'Available' ? '✅ Available' : '📖 Checked out';
        statusCell.appendChild(badge);

        const checkedOutCell = document.createElement('td');
        checkedOutCell.style.cssText = 'padding: 10px;';
        if (book.checkedOutTo) {
            checkedOutCell.textContent = `#${book.checkedOutTo.userId} — ${book.checkedOutTo.username}`;
        } else {
            checkedOutCell.innerHTML = '<span style="color: #aaa;">—</span>';
        }

        row.appendChild(idCell);
        row.appendChild(titleCell);
        row.appendChild(statusCell);
        row.appendChild(checkedOutCell);
        table.appendChild(row);
    });

    booksDiv.appendChild(table);
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
    const surname = document.getElementById('user-surname').value;
    const username = document.getElementById('user-username').value;

    if (!name.trim() || !surname.trim() || !username.trim()) {
        log('⚠️ Please fill in all fields');
        return;
    }

    const result = await apiCall('POST', '/users', { name, surname, username });
    if (result.success) {
        log('✅ User registered successfully');
        document.getElementById('user-name').value = '';
        document.getElementById('user-surname').value = '';
        document.getElementById('user-username').value = '';
        
        showUserRegistrationResult('User registered: ${name} (${username})', null);
    }else if (result.data && typeof result.data === 'string' && result.data.includes('already taken')) {
        const resultDiv = document.getElementById('user-registration-result');
        resultDiv.innerHTML = `<p class="error">❌ ${result.data}</p>`;
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
    if (result.success) {
        const detailsDiv = document.getElementById('user-details');
        detailsDiv.innerHTML = `<pre>${JSON.stringify(result.data, null, 2)}</pre>`;
        
        const deleteBtn = createDeleteButton(userId, 'user');
        detailsDiv.appendChild(deleteBtn);
    } else {
        displayResults('user-details', result);
    }
}

async function loadAllUsers() {
    const result = await apiCall('GET', '/users');
    const allUsersDiv = document.getElementById('all-users-list');
    
    if (!result.success || !Array.isArray(result.data)) {
        allUsersDiv.innerHTML = '<p class="error">Could not load users</p>';
        return;
    }

    if (result.data.length === 0) {
        allUsersDiv.innerHTML = '<p class="info">No users found</p>';
        return;
    }

    allUsersDiv.innerHTML = '';
    const table = document.createElement('table');
    table.style.cssText = 'width: 100%; border-collapse: collapse; margin-top: 10px;';
    
    result.data.forEach(user => {
        const row = document.createElement('tr');
        row.style.cssText = 'border-bottom: 1px solid #ddd; padding: 10px 0;';
        
        const idCell = document.createElement('td');
        idCell.style.cssText = 'padding: 10px; text-align: left; width: 50px;';
        idCell.textContent = `#${user.userId}`;
        
        const nameCell = document.createElement('td');
        nameCell.style.cssText = 'padding: 10px; text-align: left;';
        nameCell.textContent = `${user.name} ${user.surname}`;

        const usernameCell = document.createElement('td');
        usernameCell.style.cssText = 'padding: 10px; text-align: left; color: #666;';
        usernameCell.textContent = `@${user.username}`;
        
        const actionCell = document.createElement('td');
        actionCell.style.cssText = 'padding: 10px; text-align: right;';
        
        const deleteBtn = createDeleteButton(user.userId, 'user');
        actionCell.appendChild(deleteBtn);
        
        row.appendChild(idCell);
        row.appendChild(nameCell);
        row.appendChild(usernameCell)
        row.appendChild(actionCell);
        table.appendChild(row);
    });
    
    allUsersDiv.appendChild(table);
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
    setInterval(checkBackendStatus, 10000); // Check every 10 seconds
});
