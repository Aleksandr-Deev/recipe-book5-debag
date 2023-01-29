package com.example.recipebook5debag.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Recipe {

    private final long id;
    private final String nameRecipe;
    private final int cookingTimeMinutes;
    private List<Ingredient> ingredients;
    private List<String> cookingSteps;

}
