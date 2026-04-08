package com.nscc.onlinestore.config;

import com.nscc.onlinestore.entity.Product;
import com.nscc.onlinestore.service.ProductService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory, ProductService productService) {
        // Start the conversation 'prompt'. Customize the prompt and replace the "whale" conversation below with the prompt variable.
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a customer service assistant for a store selling whimsical dolls for children.\n");
        prompt.append("Here is our current doll catalog as a list:\n");

        List<Product> products = productService.getAllProducts();
        products.forEach(m -> {
            // Include movies in the catalog. Note: %s is string value, %d is number
            prompt.append(String.format("- Name: %s. Story: %s. Birthday: %s. Price: $ %n.\n",
                    m.getProdName(), m.getProdStory(), m.getProdBirthday(), m.getProdPrice()));
        });

        prompt.append("Answer customer questions based only on this catalog.");

        return ChatClient.builder(chatModel)
                .defaultSystem(prompt.toString())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}