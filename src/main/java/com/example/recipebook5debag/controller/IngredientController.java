package com.example.recipebook5debag.controller;

import com.example.recipebook5debag.model.Ingredient;
import com.example.recipebook5debag.service.IngredientsService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {

    private final IngredientsService ingredientsService;

    public IngredientController(IngredientsService ingredientsService) {
        this.ingredientsService = ingredientsService;
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Показать ингриедиент по идентификатору",
            description = "Выдаёт ингриедиент по указанному идентификатору"
    )
    public Ingredient getIngredient(@PathVariable long id) {
        return this.ingredientsService.getIngredientId(id);
    }

    @PostMapping
    @Operation(
            summary = "Запись ингриедиента",
            description = "Создает ингриедиент по заданным параметрам"
    )
    public Ingredient createIngredient(@RequestBody Ingredient ingredient) {
        return this.ingredientsService.addIngredient(ingredient);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Редактирование ингриедиента по указанному индетефикатору",
            description = "Редактирует уже созданный ингриедиент"
    )
    public Ingredient updateIngredient(@PathVariable ("id") long id, @RequestBody Ingredient ingredient) {
        return ingredientsService.updateIngredient(id, ingredient);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление ингриедиента",
            description = "Удаляет ингриедиент по указанному индетефикатору"
    )
    public Ingredient remoweIngredient(@PathVariable ("id") long id) {
        return ingredientsService.remoweIngredient(id);
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadRecipes() {
        InputStreamResource inputStreamResource = ingredientsService.getAllInBytes();
        if (inputStreamResource == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"ingredients.json\" ")
                .body(inputStreamResource);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importRecipes(MultipartFile ingredients) {
        ingredientsService.importIngredients(ingredients);
    }
}
