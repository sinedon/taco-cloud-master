package edu.iu.p566.taco_cloud.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import edu.iu.p566.taco_cloud.data.OrderRepository;
import edu.iu.p566.taco_cloud.model.TacoOrder;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
@SessionAttributes("tacoOrder")
public class OrderController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DesignTacoController.class);
    private OrderRepository orderRepo;

    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @GetMapping("/current")
    public String orderForm() {
        return "orderForm";
    }

    @PostMapping
    public String processOrder(@Valid TacoOrder order, Errors errors,
            SessionStatus sessionStatus) {
        log.info("Received order: {}", order);
        log.info("Has errors: {}", errors.hasErrors());
        log.info("Error count: {}", errors.getErrorCount());

        if (errors.hasErrors()) {
            return "orderForm";
        }

        log.info("******************************************************");

        orderRepo.save(order);
        log.info("Order submitted: {}", order);

        sessionStatus.setComplete();

        return "redirect:/";
    }
}
