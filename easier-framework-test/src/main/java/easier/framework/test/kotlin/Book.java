package easier.framework.test.kotlin;

import com.tangzc.mpe.autotable.annotation.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@EqualsAndHashCode(callSuper = false)
@Data
@FieldNameConstants
public class Book extends Person {

    @Column(Fields.personId)
    private String personId;

    private String bootName;
    private Integer price;

}
