package com.snappay.taxforecaster.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snappay.taxforecaster.controller.model.AbstractModel;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxUser extends AbstractModel<String> {

    private String username;
    @JsonIgnore
    private String password;

    public TaxUser(String username) {
        this.username = username;
    }
}
