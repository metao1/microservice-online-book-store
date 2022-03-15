package com.metao.book.shared.infra.eventlog;

import org.springframework.lang.NonNull;

import java.util.Optional;

import com.metao.book.shared.domain.base.DomainEvent;

/**
 * Interface for a remote event translator that translates a remote
 * {@link StoredDomainEvent} into a local
 * {@link DomainEvent}, taking into account that the original domain event class
 * used on the remote side may not
 * exist in the class path on the local side.
 */
public interface RemoteEventTranslator {

    /**
     * Returns whether this translator supports the given remote event (i.e. knows
     * how to translate it).
     */
    boolean supports(@NonNull StoredDomainEvent remoteEvent);

    /**
     * Translates the {@link StoredDomainEvent#toJsonString() JSON} of the given
     * remote event into a local
     * {@link DomainEvent}.
     */
    @NonNull
    Optional<DomainEvent> translate(@NonNull StoredDomainEvent remoteEvent);
}
