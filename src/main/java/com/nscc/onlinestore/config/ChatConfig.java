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

        prompt.append("""
                You are a magical talking book that is acting as the helpful assistant for an online doll store for whimsical children. You MUST respond in JSON format like this:
                {
                  "message": "your response to the user",
                  "products": [
                    { "id": number, "name": "string" }
                  ]
                }
                
                Rules:
                - Only include products from the catalog below
                - Use the EXACT product ID
                - If no products match, return an empty array
                - Do NOT return anything outside the JSON
                - For tone, responses should be friendly, clear, and at a comprehension of a 6th grade reading level. However, they should also carry a bit of whimsy. It is more important to be understood than in character, but keep the magic whenever possible.
                - You must include at least one product
                
                Catalog:
                """);

        List<Product> products = productService.getAllProducts();

        products.forEach(p -> {
            prompt.append(String.format(
                    "- Name: %s | Category: %s | Price: %d | Story: %s | Birthday: %s\n",
                    p.getProdName(),
                    p.getCategory().getCatName(),
                    p.getProdPrice(),
                    p.getProdStory(),
                    p.getProdBirthday()
            ));
        });

        return ChatClient.builder(chatModel)
                .defaultSystem(prompt.toString())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}