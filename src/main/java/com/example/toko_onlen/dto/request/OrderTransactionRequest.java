package com.example.toko_onlen.dto.request;

import com.example.toko_onlen.dto.validation.OnCreate;
import com.example.toko_onlen.model.record.ProductsOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderTransactionRequest {

    @NotBlank(message = "Nama produk tidak boleh kosong", groups = {OnCreate.class})
    private String productId;

    @NotNull(message = "jumlah produk beli tidak boleh kosong", groups = {OnCreate.class})
    @Min(value = 1, message = "minimal order adalah 1",groups = {OnCreate.class})
    private int quantity;

}
