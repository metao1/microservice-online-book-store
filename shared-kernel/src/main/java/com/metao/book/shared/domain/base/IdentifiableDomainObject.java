package com.metao.book.shared.domain.base;

import org.springframework.lang.Nullable;
import java.io.Serializable;

/**
 * Interface for domain objects that can be uniquely identified.
 *
 * @param <T> the T type.
 */
public interface IdentifiableDomainObject<T extends Serializable> extends DomainObject {

    /**
     * Returns the ID of this domain object.
     *
     * @return the ID or {@code null} if an ID has not been assigned yet.
     */
    @Nullable
    T id();
}
