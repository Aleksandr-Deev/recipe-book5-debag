package com.example.recipebook5debag.service.impl;

import com.example.recipebook5debag.model.Ingredient;
import com.example.recipebook5debag.service.IngredientsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class IngredientsServiceImpl implements IngredientsService {

    public Map<Long, Ingredient> ingredients = new HashMap<>();
    private static long lastId = 0;

    private final Path path;

    private final ObjectMapper objectMapper;

    public IngredientsServiceImpl(@Value("${applicatoin.file.ingredients}") String path) {
        try {
            this.path = Paths.get(path);
            this.objectMapper = new ObjectMapper();
        } catch (InvalidPathException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @PostConstruct
    private void init() {
        readDataFromFile();
    }

    private void readDataFromFile() {
        try {
            byte[] file = Files.readAllBytes(path);
            Map<Long, Ingredient> mapFromFile = objectMapper.readValue(file, new TypeReference<>() {
            });
            ingredients.putAll(mapFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataToFile(Map<Long, Ingredient> ingredients) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(ingredients);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ingredient addIngredient(Ingredient ingredient) {
        if (ingredients.containsKey(ingredient.getId())) {
            throw new RuntimeException("Такой ингридиент уже добавлен");
        } else {
            Ingredient newIngredient = ingredients.put(lastId++, ingredient);
            writeDataToFile(ingredients);
            return newIngredient;
        }
    }

    @Override
    public Ingredient getIngredientId(long id) {
        if (ingredients.containsKey(id)) {
            return ingredients.get(id);
        } else {
            throw new RuntimeException("ингридиент не найден");
        }
    }

    @Override
    public Ingredient updateIngredient(long id, Ingredient ingredient) {
        if (ingredients.containsKey(id)) {
            Ingredient newIngredient = ingredients.put(id, ingredient);
            writeDataToFile(ingredients);
            return newIngredient;
        }
        return null;
    }

    @Override
    public Ingredient remoweIngredient(long id) {
        Ingredient ingredient =ingredients.remove(id);
        writeDataToFile(ingredients);
        return ingredient;
    }

    @Override
    public InputStreamResource getAllInBytes() {
        try {
            return new InputStreamResource(new FileInputStream(path.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void importIngredients(MultipartFile ingredients) {
        try {
            Map<Long, Ingredient> mapFromRequest = objectMapper.readValue(ingredients.getBytes(),
                    new TypeReference<>() {});
            writeDataToFile(mapFromRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}