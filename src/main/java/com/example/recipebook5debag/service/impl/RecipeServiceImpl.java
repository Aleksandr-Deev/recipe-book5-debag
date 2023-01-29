package com.example.recipebook5debag.service.impl;

import com.example.recipebook5debag.model.Ingredient;
import com.example.recipebook5debag.model.Recipe;
import com.example.recipebook5debag.service.RecipeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final Map<Long, Recipe> recipes = new HashMap<>();
    private static long lastId = 0;

    private final Path pathToTxtTemplate;
    private final Path path;
    private final ObjectMapper objectMapper;

    public RecipeServiceImpl(@Value("${applicatoin.file.recipe}") String path) {
        try {
            this.path = Paths.get(path);
            this.pathToTxtTemplate = Paths.get(
                    RecipeServiceImpl.class.getResource("recipesTemplate.txt").toURI());
            this.objectMapper = new ObjectMapper();
        } catch (InvalidPathException e) {
            e.printStackTrace();
            throw e;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void init() {
        readDataFromFile();
    }

    private void readDataFromFile() {
        try {
            byte[] file = Files.readAllBytes(path);
            Map<Long, Recipe> mapFromFile = objectMapper.readValue(file, new TypeReference<>() {
            });
            recipes.putAll(mapFromFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataToFile(Map<Long, Recipe> recipes) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(recipes);
            Files.write(path, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Recipe addRecipe(Recipe recipe) {
        if (recipes.containsKey(recipe.getId())) {
            throw new RuntimeException("Такой рецепт уже добавлен");
        } else {
            recipes.put(lastId++, recipe);
            writeDataToFile(recipes);
        }
        return recipe;
    }


    public Recipe getRecipeId(long id) {
        if (recipes.containsKey(id)) {
            return recipes.get(id);
        } else {
            throw new RuntimeException("Рецепт не найден");
        }
    }

    public Collection<Recipe> getAllRecipes() {
        return recipes.values();
    }

    public Recipe updateRecipe(long id, Recipe recipe) {
        if (recipes.containsKey(id)) {
            recipes.put(id, recipe);
            writeDataToFile(recipes);
            return recipe;
        } else {
            throw new RuntimeException("Рецепт не найден");
        }
    }

    @Override
    public Recipe remoweRecipe(long id) {
        if (recipes.containsKey(id)) {
            Recipe recipe = recipes.remove(id);
            writeDataToFile(recipes);
            return recipe;
        }else {
            throw new RuntimeException("Рецепт не найден");
        }
    }

    @Override
    public byte[] getAllInBytes() {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void importRecipes(MultipartFile recipes) {
        try {
            Map<Long, Recipe> mapFromRequest = objectMapper.readValue(recipes.getBytes(),
                    new TypeReference<>() {});
            writeDataToFile(mapFromRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] exportTxt() {
        try {
            String template = Files.readString(pathToTxtTemplate, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            for (Recipe recipe : recipes.values()) {
                StringBuilder ingredients = new StringBuilder();
                StringBuilder steps = new StringBuilder();
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ingredients.append(" - ").append(ingredient).append("/n");
                }
                int stepsCounter = 1;
                for (String step : recipe.getCookingSteps()) {
                    steps.append(stepsCounter++).append(". ").append(step).append("\n");
                }
                String recipeData = template.replace("%nameRecipe%", recipe.getNameRecipe())
                        .replace("%cookingTimeMinutes%", String.valueOf(recipe.getCookingTimeMinutes()))
                        .replace("%ingredients%", ingredients.toString())
                        .replace("%cookingSteps%", steps.toString());
                stringBuilder.append(recipeData).append("\n\n\n");
            }
            return stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
