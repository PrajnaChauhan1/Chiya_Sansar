# 🍵 Chiya Sansar

Chiya Sansar is a web-based platform for buying and selling authentic Ilam tea products. Built with Java and Spring Boot on the backend and a responsive frontend, this platform connects tea lovers with genuine producers directly from the heart of Nepal.

---

## 🧩 Features

- 🔍 **Search & Buy Tea:** Users can search for tea based on flavor, category, or price. Just type the location for delivery and select your products.
- 🛒 **Cart & Checkout:** Add desired products, confirm your order, and receive an e-receipt via email.
- 📦 **Admin Product Management:** Admins can add, edit, and update tea inventory with fields like product name, quantity, and price.
- 🔐 **Secure Login & Signup:** Standard user authentication ensures a safe shopping experience.
- 📧 **Email Notifications:** Electronic receipts are automatically generated and sent to the buyer's email.
- 📉 **Stock Management:** Product stock gets updated instantly after purchase.

---

## 👨‍🔬 Testing Scenarios

### ✅ Unit Testing Cases

| Test ID | Scenario | Test Data | Expected Outcome | Result |
|--------|----------|-----------|------------------|--------|
| UT001 | Signup with mismatched passwords | Password: `abcd1234` / Retype: `abcd4321` | Error: Passwords do not match | ✅ Pass |
| UT002 | Signup with invalid email | Email: `usergmail.com` | Error: Invalid email format | ✅ Pass |
| UT003 | Add product with empty fields | Name: `""`, Quantity: `50`, Price: `150` | Error: Product name required | ✅ Pass |
| UT004 | Add valid product | Name: `Lemon Tea`, Quantity: `50`, Price: `150` | Product added successfully | ✅ Pass |
| UT005 | Search by delivery location | Location: `Kathmandu` | Product list for Kathmandu shown | ✅ Pass |
| UT006 | Purchase product with limited stock | Order: `60 units`, Available: `50 units` | Error: Not enough stock | ✅ Pass |
| UT007 | Purchase product with valid quantity | Order: `2 units`, Available: `10 units` | Order success, stock reduced | ✅ Pass |

### 🌐 System Testing Cases

| Test ID | Scenario | Description | Expected Outcome | Result |
|--------|----------|-------------|------------------|--------|
| ST001 | User signup and login | Register & log in with valid details | User logged in | ✅ Pass |
| ST002 | Product search | Search for “Green Tea” for “Lalitpur” | Display matching products | ✅ Pass |
| ST003 | Checkout & email confirmation | Buy 2 units of Black Tea | Email receipt sent | ✅ Pass |
| ST004 | Admin adds new tea product | Name: `Tulsi Tea`, Quantity: 100, Price: 250 | Product added to inventory | ✅ Pass |
| ST005 | Admin edits tea product | Update price of `Tulsi Tea` to 300 | Product updated in DB | ✅ Pass |

---

## 🛠 Tech Stack

- **Backend:** Java + Spring Boot ☕
- **Frontend:** HTML/CSS/JS with responsive design 🖥️
- **Database:** MySQL 🐬
- **Email Service:** JavaMail API 📬

---

## 📸 Screenshots

> Coming soon...

---

## 👥 Credits

- 🧠 **Backend Developer:** Utsav Kattel  
- 🎨 **Frontend Developer:** Prajna Chauhan

---

## 🧪 Test Locally

```bash
# Backend (Spring Boot)
./mvnw spring-boot:run

# Frontend
Open index.html or host on local server