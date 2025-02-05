package com.devsuperior.dscommerce.repositories;

import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

//Como é uma tabela de relacionamento é necessário apontar a chave como sendo a classe criada para a finalidade
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPK> {

}
