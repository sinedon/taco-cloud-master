package edu.iu.p566.taco_cloud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Ingredient {

    @Id
    private final String id;
    private final String name;
    private final Type type;

    public Ingredient(String id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    private Ingredient() {
        this.id = "";
        this.name = "";
        this.type = null;
    }

    public enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Ingredient [id=" + id + ", name=" + name + ", type=" + type + "]";
    }

   

    

    

}
