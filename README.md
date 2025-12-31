# Enterprise-Grade URL Shortener & Analytics System

### 🚩 Problem Statement
Standard URLs can be long, cumbersome, and difficult to track. Businesses need a way to create branded, short aliases while monitoring engagement. This project implements a system that:
- Maps long URLs to unique, manageable short links.
- Supports **Custom Aliases** for marketing and branding.
- Provides **Real-Time Analytics** on link usage.
- Enforces **Expiration Policies (TTL)** to manage link lifecycle.

### 🛠️ Tech Stack
- **Language:** Java 17+
- **Concurrency:** `ConcurrentHashMap` for thread-safe storage and `AtomicLong` for lock-free click tracking.
- **Algorithms:** Base62 Encoding for unique key generation.
- **Time Management:** `java.time` for Time-To-Live (TTL) expiration logic.

### 💡 Solution & Architecture
- **Base62 Encoding:** Instead of using random strings (which can collide), we encode an incremental `AtomicLong` into Base62 (`a-z`, `A-Z`, `0-9`). This guarantees billions of unique, URL-safe combinations without collisions.
- **Bi-Directional Persistence:** The system uses a metadata model to store the original URL, creation time, and click counts, allowing for $O(1)$ resolution speed.
- **Expiration Logic:** Every link has a "Use By" date. The system automatically validates the timestamp before redirecting, simulating a real-world cache eviction policy.



### ✨ Key Features
- **Dynamic Interaction:** Full CLI menu to shorten, resolve, and check stats.
- **Custom Branding:** Users can choose their own short keys (e.g., `go.ly/my-link`).
- **Input Validation:** Prevents the shortening of malformed or invalid protocols.
- **Traffic Dashboard:** View real-time click counts for all active links.
- **Auto-Cleanup:** Expired links are detected and removed from the system during access.

### 🧪 Use Case (Simulation)
1. **Shorten:** Input `https://github.com/givijain1234` with custom alias `portfolio`.
   - *Result:* `http://go.ly/portfolio` created.
2. **Redirect:** User clicks `http://go.ly/portfolio`.
   - *Result:* Increments click counter and returns original URL.
3. **Analytics:** View Dashboard.
   - *Result:* System shows `portfolio` has 1 click and expires in 7 days.

### 🛠️ How to Run
1. Clone the repo: `git clone https://github.com/givijain1234/url-shortener-java.git`
2. Open in your Java IDE and run `Main.java`.
3. Follow the CLI prompts to start shortening and tracking.
