package org.example.loja.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreManagerDTO {
    @NonNull
    public String email;
    @NonNull
    public String password;
    @NonNull
    public String name;
    @NonNull
    public String cpf;
    @NonNull
    public Long storeId;
}
