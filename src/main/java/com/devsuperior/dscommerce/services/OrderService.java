package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.dto.OrderItemDTO;
import com.devsuperior.dscommerce.entities.*;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFindException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public OrderDTO findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFindException("Resource not found!"));
        //Valida se o usuário logado é ADMIN ou é dono do pedido
        authService.validateSelforAdmin(order.getClient().getId());
        return new OrderDTO(order);
    }

    @Transactional
    public OrderDTO insert(OrderDTO dto) {

        Order order = new Order();

        order.setMoment(Instant.now());
        order.setStatus(OrderStatus.WAITING_PAYMENT);

        //recupera o usuário logado
        User user = userService.authenticate();
        order.setClient(user);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDTO.getProductId());
            OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
            order.getItems().add(item);
        }

        order = orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return new OrderDTO(order);

    }

    @Transactional
    public OrderDTO update(Long id, OrderDTO dto) {
        try {
            Order entity = orderRepository.getReferenceById(id);
            CopyDtoToEntity(dto, entity);
            entity = orderRepository.save(entity);
            return new OrderDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFindException("Resource not find!");

        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFindException("Resource not find!");
        }
        try {
            orderRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential Integrity failure!");

        }
        ;
    }

    private void CopyDtoToEntity(OrderDTO dto, Order entity) {
        entity.setMoment(dto.getMoment());
        entity.setStatus(dto.getStatus());
        //entity.setPayment(new Payment(dto.getPayment()));
        //Limpa as categorias anteriores e Insere as categorias atreladas ao produto
/*
        entity.getCategories().clear();
        for(CategoryDTO categoryDTO : dto.getCategories()){
            Category cat = new Category();
            cat.setId(categoryDTO.getId());
            entity.getCategories().add(cat);
        }
*/
    }

}
