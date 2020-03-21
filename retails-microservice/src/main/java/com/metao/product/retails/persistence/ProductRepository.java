package com.metao.product.retails.persistence;

import com.metao.product.retails.domain.ProductEntity;
import com.metao.product.retails.model.ProductDTO;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends CassandraRepository<ProductEntity, String> {

    Optional<ProductEntity> findProductEntityById(String productId);

    @Query("SELECT * FROM product limit ?0 offset ?1")
    Optional<List<ProductEntity>> findAllProductsWithOffset(int limit, int offset);

    @Query("SELECT * FROM product where category =?0 limit ?1 offset ?2")
    List<ProductDTO> findAllProductsWithCategoryAndOffset(String category, int limit, int offset);
}
