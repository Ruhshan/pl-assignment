package xyz.ruhshan.pl.common.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.ruhshan.pl.common.entity.BaseEntity;
import xyz.ruhshan.pl.common.entity.Battery;

public interface BatteryRepository extends JpaRepository<Battery, Long> {
    Optional<Battery> findByPostCodeAndName(Integer postCode, String name);
    List<Battery> findAllByPostCodeBetween(Integer postCodeStart, Integer postCodeEnd);

}
