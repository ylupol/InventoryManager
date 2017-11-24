package com.ylupol.im.location;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findById(int id);

    Optional<Location> findByName(String name);
}
