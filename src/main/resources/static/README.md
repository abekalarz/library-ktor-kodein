# Frontend Testing UI

Simple HTML/JavaScript frontend for testing the Ktor library backend - **served directly from Ktor!**

## Quick Start

Just run the Ktor backend:
```bash
./gradlew run
```

Then open your browser:
```
http://localhost:8080
```

That's it! Frontend is served from the same server as the API.

## Features

### 📖 Books
- List all books (with optional title filter)
- Add new books

### 👤 Users
- Register new users
- Get user details by ID

### 🛒 Checkouts & Returns
- Checkout books (requires user ID and book ID)
- Return books

### 🔧 Custom API Tester
- Make custom requests (GET, POST, PUT, DELETE)
- Send JSON bodies

## Real-time Logging
- All API requests/responses logged
- Backend status monitoring (checks every 5 seconds)
- Clear logs anytime

## Notes

- Frontend files in `src/main/resources/static/`
- CORS enabled for localhost:8080
- Matches actual backend endpoints (no non-existent endpoints)
- Pure HTML/CSS/JS - no build tools needed
