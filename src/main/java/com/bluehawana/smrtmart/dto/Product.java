package com.bluehawana.smrtmart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


    public class Product {
        private Long id;
        private String name;
        private BigDecimal price;
        private String description;

        public String getDescription() {
            return name + price + description;
        }
    }