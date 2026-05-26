package com.agri.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreRegisterRequest {
    @NotBlank(message = "店铺名称不能为空")
    @Size(min = 2, max = 100, message = "店铺名称长度必须在2-100之间")
    private String storeName;

    @Size(max = 500, message = "店铺描述长度不能超过500")
    private String description;

    @Size(max = 20, message = "联系电话长度不能超过20")
    private String phone;

    @Size(max = 200, message = "店铺地址长度不能超过200")
    private String address;

    private String logoUrl;
}
