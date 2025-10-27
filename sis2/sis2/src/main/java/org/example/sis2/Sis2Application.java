package org.example.sis2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

// Точка входа
@SpringBootApplication
public class Sis2Application {
	public static void main(String[] args) {
		var context = SpringApplication.run(Sis2Application.class, args);

		// Получаем контроллер из контейнера Spring
		UserController controller = context.getBean(UserController.class);
		System.out.println("Контроллер получил: " + controller.getMessage());

		// Ленивая инициализация HeavyService
		System.out.println("Вызываем HeavyService...");
		HeavyService heavy = context.getBean(HeavyService.class);
		heavy.run();

		System.out.println("=== DEMO SCOPES ===");

		// Получаем бины для проверки
		ScopeExamples.SingletonBean s1 = context.getBean(ScopeExamples.SingletonBean.class);
		ScopeExamples.SingletonBean s2 = context.getBean(ScopeExamples.SingletonBean.class);
		System.out.println("SingletonBean одинаковый? " + (s1 == s2));

		ScopeExamples.PrototypeBean p1 = context.getBean(ScopeExamples.PrototypeBean.class);
		ScopeExamples.PrototypeBean p2 = context.getBean(ScopeExamples.PrototypeBean.class);
		System.out.println("PrototypeBean одинаковый? " + (p1 == p2));

		System.out.println("=== DEMO END ===");
	}

	// --- 1. Интерфейс и реализации ---
	public interface MessageService {
		String getMessage();
	}

	@Component("emailMessageService")
	public static class EmailMessageService implements MessageService {
		@Override
		public String getMessage() {
			return "Email сообщение";
		}
	}

	@Component("smsMessageService")
	public static class SmsMessageService implements MessageService {
		@Override
		public String getMessage() {
			return "SMS сообщение";
		}
	}

	// --- 2. Controller с внедрением зависимости ---
	@Component
	public static class UserController {
		private final MessageService service;

		// Constructor-based DI с @Qualifier
		@Autowired
		public UserController(@Qualifier("smsMessageService") MessageService service) {
			this.service = service;
		}

		public String getMessage() {
			return service.getMessage();
		}
	}

	// --- 3. Lazy Bean ---
	@Component
	@Lazy
	public static class HeavyService {
		public HeavyService() {
			System.out.println("HeavyService создан только при первом вызове!");
		}

		public void run() {
			System.out.println("HeavyService запущен!");
		}
	}
}
