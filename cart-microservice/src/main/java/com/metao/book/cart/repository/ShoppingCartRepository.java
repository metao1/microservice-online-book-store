package com.metao.book.cart.repository;

import com.metao.book.cart.domain.ShoppingCart;
import com.metao.book.cart.domain.ShoppingCartKey;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, ShoppingCartKey> {

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    @Query("SELECT distinct sc FROM #{#entityName} sc WHERE sc.userId=:userId")
    Set<ShoppingCart> findProductsInCartByUserId(@Param("userId") String userId);

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    @Query("SELECT sc FROM #{#entityName} sc WHERE sc.userId=:#{#key.userId} AND sc.asin=:#{#key.asin}")
    Optional<ShoppingCart> findByUserIdAndAsin(@Param("key") ShoppingCartKey shoppingCartKey);
}
