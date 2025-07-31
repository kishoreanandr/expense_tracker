package com.expensetracker.expensetracker;

import com.expensetracker.expensetracker.Entity.Category;
import com.expensetracker.expensetracker.Respository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpensetrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExpensetrackerApplication.class, args);
	}

	@Bean
	public CommandLineRunner initCategories(CategoryRepository categoryRepository) {
		return args -> {
			if (categoryRepository.count() == 0) {
				categoryRepository.save(new Category("Food", "Food & Groceries", "#FF6384", "utensils", true));
				categoryRepository.save(new Category("Travel", "Travel & Transport", "#36A2EB", "car", true));
				categoryRepository.save(new Category("Rent", "House Rent", "#FFCE56", "home", true));
				categoryRepository.save(new Category("Utilities", "Bills & Utilities", "#4BC0C0", "bolt", true));
				categoryRepository.save(new Category("Entertainment", "Movies, Games, etc.", "#9966FF", "film", true));
				categoryRepository.save(new Category("Health", "Health & Fitness", "#FF9F40", "heartbeat", true));
				categoryRepository.save(new Category("Other", "Other Expenses", "#CCCCCC", "ellipsis-h", true));
			}
		};
	}
}
