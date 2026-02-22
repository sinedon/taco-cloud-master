package edu.iu.p566.taco_cloud.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.iu.p566.taco_cloud.model.TacoOrder;

@Repository
public interface OrderRepository extends CrudRepository<TacoOrder, Long> {
    
}
