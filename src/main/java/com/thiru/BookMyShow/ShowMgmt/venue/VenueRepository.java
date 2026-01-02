package com.thiru.BookMyShow.ShowMgmt.venue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface VenueRepository extends
        JpaRepository<VenueEntity, Long>,
        JpaSpecificationExecutor<VenueEntity> {
    Optional<VenueEntity> findById(Long venueId);

    List<VenueEntity> findAll(Specification<VenueEntity> spec);
}
