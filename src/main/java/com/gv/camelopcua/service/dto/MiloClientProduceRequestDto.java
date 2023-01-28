package com.gv.camelopcua.service.dto;

import com.gv.camelopcua.core.types.builtin.MiloClientValue;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class MiloClientProduceRequestDto {
    String produceUri;
    List<MiloClientValue> payload;
}
