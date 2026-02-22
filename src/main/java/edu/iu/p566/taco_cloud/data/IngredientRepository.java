package edu.iu.p566.taco_cloud.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.iu.p566.taco_cloud.model.Ingredient;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, String> {
    
}