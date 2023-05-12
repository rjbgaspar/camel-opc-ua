package com.gv.camelopcua.core.types.builtin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiloClientValue {
    MiloClientNodeId nodeId;
    MiloDataType dataType;
    Object value;
}
