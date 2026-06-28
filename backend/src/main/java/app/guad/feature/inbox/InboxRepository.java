package app.guad.feature.inbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface InboxRepository extends
        JpaRepository<Capture, Long>,
        PagingAndSortingRepository<Capture, Long>,
        JpaSpecificationExecutor<Capture> {

    /// find captures by user id and capture status
    ///
    /// @param  userId id of the capture creator
    /// @param status capture status
    ///
    /// @return a page of captures
    @Query("SELECT c FROM Capture c WHERE c.userId = :userId AND c.status = :status")
    List<Capture> findByStatus(
            @Param("userId") UUID userId,
            @Param("status") CaptureStatus status
    );

    /// find capture by id
    ///
    /// @param id capture id
    /// @param userId user id the capture belongs to
    ///
    /// @return the capture with the given id or {@literal Optional#empty()} if none found.
    @Deprecated
    @Query("SELECT c FROM Capture c WHERE c.userId = :userId AND c.status = :status")
    Optional<Capture> findById(
            @Param("id") Long id,
            @Param("userId") UUID userId
    );

    /// count user captures
    ///
    /// @param userId user id the capture belongs to
    /// @param status capture status
    ///
    /// @return count of the capture
    @Query("SELECT COUNT(c.id) FROM Capture c WHERE c.userId = :userId AND c.status = :status")
    long countByStatus(
            @Param("userId") UUID userId,
            @Param("status") CaptureStatus status
    );
}
