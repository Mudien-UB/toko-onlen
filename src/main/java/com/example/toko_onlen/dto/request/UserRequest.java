package com.example.toko_onlen.dto.request;

import com.example.toko_onlen.dto.validation.OnCreate;
import com.example.toko_onlen.dto.validation.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class UserRequest {

    @Length(min = 5, max = 50, message = "Usernama harus memiliki 5 karakter dan maksimal 50 karakter ", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;


}
