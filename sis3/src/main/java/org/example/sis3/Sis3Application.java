package org.example.sis3;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAsync // Активируем асинхронную обработку
public class Sis3Application {

	public static void main(String[] args) {
		SpringApplication.run(Sis3Application.class, args);
	}

	// 1. СОБЫТИЕ (Сделано static)
	public static class CustomSpringEvent extends ApplicationEvent {
		private final String message;

		public CustomSpringEvent(Object source, String message) {
			super(source);
			this.message = message;
		}
		public String getMessage() {
			return message;
		}
	}

	// 2. ИЗДАТЕЛЬ (Сделан static)
	@Component
	public static class CustomSpringEventPublisher {
		private final ApplicationEventPublisher applicationEventPublisher;

		public CustomSpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
			this.applicationEventPublisher = applicationEventPublisher;
		}

		public void publishCustomEvent(final String message) {
			System.out.println("Publishing custom event. ");
			CustomSpringEvent customSpringEvent = new CustomSpringEvent(this, message);
			applicationEventPublisher.publishEvent(customSpringEvent);
		}
	}

	// 3. СЛУШАТЕЛЬ 1 (Синхронный, сделан static)
	@Component
	public static class SyncEventListener {

		@EventListener
		public void handleSyncEvent(CustomSpringEvent event) {
			System.out.println("Received sync event - " + event.getMessage());
		}
	}

	// 4. СЛУШАТЕЛЬ 2 (Асинхронный, сделан static)
	@Component
	public static class AsyncEventListener {

		// Используем @Async, чтобы обработка события происходила в отдельном потоке
		@EventListener
		@Async
		public void handleAsyncEvent(CustomSpringEvent event) {
			// Имитация задержки для демонстрации асинхронности
			try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
			System.out.println("Handle async event: " + event.getMessage() + " (Thread: " + Thread.currentThread().getName() + ")");
		}
	}

	// 5. ЗАПУСК (Сделан static)
	@Component
	public static class TestRunner implements CommandLineRunner {
		private final CustomSpringEventPublisher publisher;

		// Инъекция через конструктор
		public TestRunner(CustomSpringEventPublisher publisher) {
			this.publisher = publisher;
		}

		@Override
		public void run(String... args) {
			publisher.publishCustomEvent("Начало работы системы!");
		}
	}

	// 6. КОНФИГУРАЦИЯ АСИНХРОННОГО РЕЖИМА (Сделана static)
	// Это настраивает ApplicationEventMulticaster для работы в асинхронном режиме.
	// Если вам не нужна кастомная конфигурация, Spring Boot сделает это сам при @EnableAsync.
	@Configuration
	public static class AsynchronousSpringEventsConfig {

		// Для переопределения стандартного мультипередатчика необходимо,
		// чтобы бин назывался "applicationEventMulticaster".
		@Bean(name = "applicationEventMulticaster")
		public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
			SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
			// Используем пул потоков для асинхронной работы
			eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor("event-task-"));
			return eventMulticaster;
		}
	}
}