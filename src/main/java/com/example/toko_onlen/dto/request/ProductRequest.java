package com.example.toko_onlen.dto.request;

import com.example.toko_onlen.dto.validation.OnCreate;
import com.example.toko_onlen.dto.validation.OnUpdate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @Length(max = 50, message = "Nama produk maksimal 50 karakter", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Nama produk tidak boleh kosong", groups = {OnCreate.class})
    private String name;

    @Length(max = 50, message = "Kategori produk maksimal 50 karakter", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Kategori produk tidak boleh kosong", groups = {OnCreate.class})
    private String category;

    @Length(max = 200, message = "Deskripsi produk maksimal 200 karakter", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(message = "Harga tidak boleh kosong", groups = {OnCreate.class})
    private String price;

    @Min(value = 0, message = "Stok minimal 0", groups = {OnCreate.class, OnUpdate.class})
    @NotNull(message = "Stok tidak boleh kosong", groups = {OnCreate.class})
    private Integer stock;


}
