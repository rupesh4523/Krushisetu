# 🌱 KrushiSetu

### Connecting farmers with the right information, advice, and agricultural resources.

KrushiSetu is an Android application built to make day-to-day farming a little easier by bringing useful agricultural services into one place.

Instead of a farmer having to depend on different sources for crop information, weather updates, expert advice, agricultural products, and suppliers, KrushiSetu brings these things together in a single application.

The idea is simple:

> **A farmer should be able to get the information and resources they need without having to search everywhere for them.**

---

## 🚜 What does KrushiSetu do?

KrushiSetu currently focuses on five main areas:

### 🌱 1. Crop Management

Farmers can add and manage their crops through the application.

They can keep track of their crops and receive relevant information and reminders.

### 📷 2. AI-Assisted Plant Health Check

Farmers can capture a photo of a plant and use the plant scanning feature for an AI-assisted health check.

The goal is to make basic crop-health assistance accessible even to farmers who may not have immediate access to an agricultural expert.

### ☀️ 3. Weather & Agricultural Advisory

The app provides weather information along with agricultural advisories.

For example, the application can provide reminders related to:

- Irrigation
- Rain
- Fertilizer application
- General crop-related activities

The idea is not just to show weather data, but to connect weather conditions with practical farming decisions.

### 🛒 4. Agricultural Marketplace

Farmers can browse products offered by agricultural suppliers.

They can:

- View suppliers
- Browse products
- Add products to their cart
- Change quantities
- Place orders
- View previous orders

Suppliers can add and manage their own products and receive orders from farmers.

### 👨‍🌾 5. Expert Consultation

KrushiSetu also provides an option for farmers to seek agricultural advice and connect with experts.

---

# 💡 Why KrushiSetu?

Agriculture is not only about growing a crop.

A farmer has to make decisions about:

**What to grow → When to irrigate → What the weather will be → What problem the crop has → What product to use → Where to buy it → How to get expert advice**

These activities are often spread across different sources.

KrushiSetu tries to bring these parts together into one platform.

---

# ⭐ What makes our approach different?

KrushiSetu is not designed as just another agricultural information app or just another shopping application.

The main idea is to connect the different parts of a farmer's workflow.

For example:

```text
             FARMER
                │
        ┌───────┼────────┐
        │       │        │
      Crops   Weather   Plant Scan
        │       │        │
        └───────┼────────┘
                │
          Agricultural
            Advisory
                │
        ┌───────┴────────┐
        │                │
     Expert           Marketplace
     Advice               │
                          │
                      Suppliers
                          │
                       Products
                          │
                        Orders
```

This creates a connected ecosystem rather than treating each feature as a separate service.

---

# 📱 Main Features

- 👨‍🌾 Farmer profiles
- 🏪 Supplier profiles
- 🌱 Crop management
- 📷 AI-assisted plant scanning
- 🌦️ Weather information
- 💧 Irrigation reminders
- 📢 Agricultural advisories
- 🛒 Agricultural marketplace
- 📦 Shopping cart
- 🧾 Order placement
- 📋 Order history
- 🌐 Multi-language support
- 🔐 Firebase-based authentication and database
- 🔒 Firebase security rules for controlling data access

---

# 🏗️ Technology Stack

### Android

- **Kotlin**
- **Jetpack Compose**
- **Material 3**

The UI is built using Jetpack Compose, which allows the application screens to be built using reusable composable components.

### Backend

KrushiSetu uses **Firebase** as its backend infrastructure.

Main Firebase services include:

- Firebase Authentication
- Cloud Firestore
- Firebase security rules

Firestore stores application data such as:

- Users
- Crops
- Products
- Cart items
- Orders

Authentication is used to identify users and control access to their data.

---

# 🔐 Security

Security is handled at the Firebase database level using Firestore Security Rules.

For example, a farmer should be able to access their own profile and cart rather than another farmer's private data.

Similarly:

- Farmers can create their own orders.
- Suppliers can manage their own products.
- Suppliers can access orders associated with them.
- Users cannot arbitrarily modify another user's information.
- Orders cannot simply be deleted from the database.

This means that security is not dependent only on what the Android UI allows the user to do.

The database itself also checks whether an operation is permitted.

---

# 🗂️ Basic Data Structure

The main Firestore collections are:

```text
Firestore
│
├── users
│   └── user profiles
│
├── products
│   └── agricultural products
│
├── orders
│   └── farmer orders
│
└── users/{userId}
    ├── crops
    └── cart
```

Users are separated by roles such as:

```text
FARMER
SUPPLIER
```

The role determines what operations the user is allowed to perform.

---

# 🔄 How the application works

A typical farmer journey looks like this:

```text
Register / Login
       ↓
    Home
       ↓
 ┌─────┼──────────┐
 ↓     ↓          ↓
Crops Weather  Plant Scan
       ↓
   Advisory
       ↓
 Need a product?
       ↓
 Krushi Shop
       ↓
 Select Supplier
       ↓
 View Products
       ↓
 Add to Cart
       ↓
 Checkout
       ↓
 Place Order
       ↓
 Order History
```

This flow is designed around the actual sequence of decisions a farmer may make rather than around individual disconnected features.

---

# 🌍 Multi-language Support

KrushiSetu supports multiple languages so that the application can be more accessible to users who may not be comfortable using English.

User-facing text is handled through Android's string resources rather than hard-coding every piece of text directly into the UI.

This also makes adding additional languages easier in the future.

---

# 🎯 Target Users

### 👨‍🌾 Farmers

Farmers are the primary users of KrushiSetu.

They can use the application to:

- Manage crops
- Check weather
- Get agricultural advisories
- Scan plants
- Seek expert advice
- Purchase agricultural products
- Track orders

### 🏪 Agricultural Suppliers

Suppliers can use the platform to:

- Create their supplier profile
- Add products
- Manage their products
- Receive farmer orders
- Update order/payment status

### 🧑‍🔬 Agricultural Experts

Experts can potentially use the platform to provide advice and help farmers with crop-related problems.

---

# 💰 Business Model

KrushiSetu follows a **B2C + B2B2C marketplace model**.

The basic platform connects:

```text
Suppliers
    ↓
KrushiSetu
    ↓
Farmers
```

Potential revenue streams include:

### 1. Marketplace Commission

KrushiSetu can charge a small commission on successful transactions between farmers and suppliers.

### 2. Supplier Subscription

Suppliers could have optional paid plans for additional visibility, analytics, or product-management features.

### 3. Expert Consultation

Premium agricultural consultations could be offered as a paid service, with revenue shared between the platform and experts.

### 4. Premium Agricultural Services

Additional services such as advanced crop analysis, detailed reports, or personalized recommendations could be offered as premium features.

The important point is that the basic farmer experience should remain useful even without forcing farmers to pay for every feature.

---

# 📈 Scalability

The application is built using Firebase and a cloud-based architecture, which means the backend does not need to be hosted on a single physical server managed by the development team.

As the user base grows, Firebase services can scale according to the application's usage.

However, scalability is not simply about saying "Firebase can handle millions of users."

Actual capacity depends on:

- Number of concurrent users
- Firestore reads and writes
- Query patterns
- AI requests
- Image uploads
- Network traffic
- Authentication traffic
- Database indexes
- Firebase quotas and billing limits

Therefore, proper monitoring and optimization would be required before deploying KrushiSetu to a very large user base.

---

# 🧠 Future Improvements

KrushiSetu is designed so that more agricultural services can be added later.

Possible future improvements include:

- More accurate plant disease detection
- Crop-specific recommendations
- Soil analysis
- Fertilizer recommendations
- Local market price information
- Voice-based interaction
- Regional-language voice assistance
- Direct expert chat/video consultation
- Delivery tracking
- Supplier analytics
- Farmer analytics
- Government agricultural scheme information
- Offline-first functionality for areas with poor connectivity

---

# 🛠️ Getting Started

## Prerequisites

You will need:

- Android Studio
- Android SDK
- Kotlin
- A Firebase project
- An Android device or emulator

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
```

### 2. Open the project

Open the project in Android Studio.

### 3. Connect Firebase

Create/configure the Firebase project and connect the Android application to it.

Make sure the required Firebase configuration file is present in the appropriate location.

### 4. Configure Firebase services

Enable the required services:

- Authentication
- Cloud Firestore

Apply the Firestore security rules included with the project.

### 5. Build and run

Sync the Gradle project and run the application on an Android device or emulator.

---

# 📁 Project Structure

The project follows a separation between UI, data, navigation, and supporting components.

A simplified structure looks like:

```text
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── sashya/
                    └── krushisetu/
                        ├── data/
                        │   └── model/
                        │
                        ├── ui/
                        │   ├── components/
                        │   ├── navigation/
                        │   ├── screens/
                        │   └── theme/
                        │
                        └── ...
```

The exact structure may evolve as new features are added.

---

# 🤝 Team

KrushiSetu was built as a project focused on solving a practical problem in agriculture using mobile technology, cloud services, and AI-assisted features.

The goal is not to replace farmers or agricultural experts.

The goal is to give farmers **better access to information, services, products, and people who can help them make decisions.**

---

# 🌱 Our Vision

We want KrushiSetu to become more than an agricultural app.

The long-term vision is to create a digital bridge between:

**Farmers ↔ Experts ↔ Suppliers ↔ Agricultural Information**

so that farmers can spend less time searching for scattered information and more time focusing on their farms.

---

## Made with 🌱 for agriculture

**KrushiSetu — एक सेतू शेतकऱ्यांसाठी.**
