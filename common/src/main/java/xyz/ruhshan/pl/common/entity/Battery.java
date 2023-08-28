package xyz.ruhshan.pl.common.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Battery extends BaseEntity{
    private String name;
    private Integer postCode;
    private Double capacity;
}
