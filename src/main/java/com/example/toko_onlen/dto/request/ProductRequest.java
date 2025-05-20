package com.example.toko_onlen.dto.request;

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

    @Length(max = 50, message = "Nama produk maksimal 50 karakter", groups = {onCreate.class, onUpdate.class})
    @NotBlank(message = "Nama produk tidak boleh kosong", groups = {onCreate.class})
    private String name;

    @Length(max = 50, message = "Kategori produk maksimal 50 karakter", groups = {onCreate.class, onUpdate.class})
    @NotBlank(message = "Kategori produk tidak boleh kosong", groups = {onCreate.class})
    private String category;

    @Length(max = 200, message = "Deskripsi produk maksimal 200 karakter", groups = {onCreate.class, onUpdate.class})
    private String description;

    @NotNull(message = "Harga tidak boleh kosong", groups = {onCreate.class})
    private String price;

    @Min(value = 0, message = "Stok minimal 0", groups = {onCreate.class, onUpdate.class})
    @NotNull(message = "Stok tidak boleh kosong", groups = {onCreate.class})
    private Integer stock;

    public interface onCreate {}
    public interface onUpdate {}
}
