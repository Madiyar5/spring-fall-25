package org.example.sis2;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class ScopeExamples {

    // --- 1. Singleton (default) ---
    @Component
    @Scope("singleton")
    public static class SingletonBean {
        public SingletonBean() {
            System.out.println("Создан SingletonBean");
        }
    }

    // --- 2. Prototype ---
    @Component
    @Scope("prototype")
    public static class PrototypeBean {
        public PrototypeBean() {
            System.out.println("Создан PrototypeBean");
        }
    }

    // --- 3. Request Scope ---
    @Component
    @Scope("request")
    public static class RequestBean {
        public RequestBean() {
            System.out.println("Создан RequestBean (на каждый HTTP-запрос)");
        }
    }

    // --- 4. Session Scope ---
    @Component
    @Scope("session")
    public static class SessionBean {
        public SessionBean() {
            System.out.println("Создан SessionBean (один на всю HTTP-сессию)");
        }
    }

    // --- 5. Application Scope ---
    @Component
    @Scope("application")
    public static class ApplicationBean {
        public ApplicationBean() {
            System.out.println("Создан ApplicationBean (один на весь ServletContext)");
        }
    }

    // --- 6. WebSocket Scope ---
    @Component
    @Scope("websocket")
    public static class WebSocketBean {
        public WebSocketBean() {
            System.out.println("Создан WebSocketBean (один на каждое соединение WebSocket)");
        }
    }
}
