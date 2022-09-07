package POJO;

import java.util.List;

public class Order {
    private List<String> ingredients;

    // конструктор со всеми параметрами
    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // конструктор без параметров
    public Order() {
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public static Order getIngredientsForOrder() {
        List<String> list = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa72", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa79");
        return new Order(list);
    }

    public static Order getInvalidIngredientsForOrder() {
        List<String> list = List.of("invalidIngredientHash", "233253532nkjo34434o");
        return new Order(list);
    }
}
