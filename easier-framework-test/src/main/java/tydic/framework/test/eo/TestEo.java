package easier.framework.test.eo;

import easier.framework.core.plugin.jackson.annotation.Alias;
import easier.framework.core.plugin.jackson.annotation.AliasId;
import easier.framework.test.enums.EnableStatus;
import lombok.Data;

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
