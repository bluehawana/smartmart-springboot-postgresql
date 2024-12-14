package com.bluehawana.smrtmart.service;

import com.bluehawana.smrtmart.dto.CartDTO;
import com.bluehawana.smrtmart.model.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDTO(Cart cart);
}
