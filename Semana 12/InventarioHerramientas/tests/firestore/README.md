# Firestore security tests

Requires Node.js and Java 21+. From this directory run:

    npm ci
    Copy-Item ../../firestore.rules firestore.rules
    npx firebase emulators:exec --project demo-toolrent --only firestore "node solicitudes.test.cjs"

Tests use demo data only. They verify ownership, approval, atomic payment and rejection of duplicates and invalid totals. No production data is modified.
