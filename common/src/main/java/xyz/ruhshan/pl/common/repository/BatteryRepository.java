package xyz.ruhshan.pl.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.ruhshan.pl.common.entity.BaseEntity;

public interface BatteryRepository extends JpaRepository<BaseEntity, Long> {

}
