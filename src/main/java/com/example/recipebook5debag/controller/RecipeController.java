package com.example.recipebook5debag.controller;

import com.example.recipebook5debag.model.Recipe;
import com.example.recipebook5debag.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @GetMapping
    @Operation(
            summary = "Показать все рецепты",
            description = "Выдаёт рецепты без ввода параметров"
    )
    public Collection<Recipe> getAllRecipe() {
        return this.recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Показать рецепт по идентификатору",
            description = "Выдаёт рецепт по указанному идентификатору"
    )
    public Recipe getRecipe(@PathVariable long id) {
        return this.recipeService.getRecipeId(id);
    }

    @PostMapping
    @Operation(
            summary = "Запись рецепта",
            description = "Создает рецепт по заданным параметрам"
    )
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return this.recipeService.addRecipe(recipe);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Редактирование рецепта",
            description = "Редактирует уже созданный рецепт по указанному индетефикатору"
    )
    public Recipe updateRecipe(@PathVariable("id") long id, @RequestBody Recipe recipe) {
        return recipeService.updateRecipe(id, recipe);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление рецепта",
            description = "Удаляет рецепт по указанному индетефикатору"
    )
    public Recipe remoweRecipe(@PathVariable("id") long id) {
        return recipeService.remoweRecipe(id);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadRecipes() {
        byte[] bytes = recipeService.getAllInBytes();
        if (bytes == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recipes.json\"")
                .body(bytes);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importRecipes(MultipartFile recipes) {
        recipeService.importRecipes(recipes);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTxt() {
        byte[] bytes = recipeService.exportTxt();
        if (bytes == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"info.txt\"")
                .body(bytes);
    }
}


