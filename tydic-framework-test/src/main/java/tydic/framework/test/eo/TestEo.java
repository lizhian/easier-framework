package tydic.framework.test.eo;

import lombok.Data;
import tydic.framework.core.plugin.jackson.annotation.Alias;
import tydic.framework.core.plugin.jackson.annotation.AliasId;
import tydic.framework.test.enums.EnableStatus;

@Data
public class TestEo {

    //@JsonAlias("JsonAliasId")
    //@JsonProperty("JsonPropertyId")
    @AliasId(deserializeFirst = true)
    private String testId;

    private EnableStatus status = EnableStatus.enable;
    @Alias("xxx")
    private boolean isstatus = false;
}
