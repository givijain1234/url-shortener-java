package com.url.shortener;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

// --- DATA MODEL ---
class URLData {
    String originalUrl;
    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    AtomicLong clickCount;

    public URLData(String originalUrl, int ttlDays) {
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(ttlDays);
        this.clickCount = new AtomicLong(0);
    }
}

// --- SERVICE LAYER ---
class ShortenerService {
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String DOMAIN = "http://go.ly/";

    private final Map<String, URLData> urlStore = new ConcurrentHashMap<>();
    private final AtomicLong globalCounter = new AtomicLong(2000000000L); // Start with large ID

    // Base62 Encoding
    private String generateKey(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62.charAt((int) (id % 62)));
            id /= 62;
        }
        return sb.reverse().toString();
    }

    public String shorten(String longUrl, String customAlias, int ttl) {
        // Simple URL Validation
        if (!longUrl.startsWith("http")) return "❌ Error: Invalid URL protocol";

        String key;
        if (customAlias != null && !customAlias.isEmpty()) {
            if (urlStore.containsKey(customAlias)) return "❌ Error: Alias already exists";
            key = customAlias;
        } else {
            key = generateKey(globalCounter.incrementAndGet());
        }

        urlStore.put(key, new URLData(longUrl, ttl));
        return DOMAIN + key;
    }

    public String resolve(String shortUrl) {
        String key = shortUrl.replace(DOMAIN, "");
        URLData data = urlStore.get(key);

        if (data == null) return "❌ Error: 404 Not Found";
        if (LocalDateTime.now().isAfter(data.expiresAt)) {
            urlStore.remove(key);
            return "❌ Error: Link Expired";
        }

        data.clickCount.incrementAndGet(); // Record analytics
        return data.originalUrl;
    }

    public void displayStats() {
        if (urlStore.isEmpty()) {
            System.out.println("No URLs in the system.");
            return;
        }
        System.out.println("\n--- 📈 ACTIVE URL ANALYTICS ---");
        urlStore.forEach((key, data) -> {
            System.out.printf("Short: %-15s | Clicks: %-3d | Target: %s%n",
                    DOMAIN + key, data.clickCount.get(), data.originalUrl);
        });
    }
}

// --- INTERACTIVE CLI ---
public class Main {
    public static void main(String[] args) {
        ShortenerService service = new ShortenerService();
        Scanner sc = new Scanner(System.in);

        System.out.println("🌐 CLOUD-SCALE URL SHORTENER INITIALIZED");

        while (true) {
            System.out.println("\n1: Shorten URL | 2: Redirect (Click) | 3: Dashboard | 4: Exit");
            System.out.print("Action > ");
            String input = sc.nextLine();

            if (input.equals("4")) break;

            switch (input) {
                case "1" -> {
                    System.out.print("Enter Long URL: ");
                    String longUrl = sc.nextLine();
                    System.out.print("Custom Alias (Leave blank for auto): ");
                    String alias = sc.nextLine();
                    System.out.print("Days until expiry (default 7): ");
                    String daysStr = sc.nextLine();
                    int days = daysStr.isEmpty() ? 7 : Integer.parseInt(daysStr);

                    String result = service.shorten(longUrl, alias, days);
                    System.out.println("✨ Result: " + result);
                }
                case "2" -> {
                    System.out.print("Enter Short URL to Resolve: ");
                    String res = service.resolve(sc.nextLine());
                    System.out.println("🔗 Redirecting to: " + res);
                }
                case "3" -> service.displayStats();
                default -> System.out.println("⚠️ Unknown command.");
            }
        }
        System.out.println("System gracefully shut down.");
        System.exit(0);
    }
}