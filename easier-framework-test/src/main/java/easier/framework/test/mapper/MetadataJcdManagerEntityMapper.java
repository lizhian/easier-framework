package easier.framework.test.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import easier.framework.test.eo.MetadataJcdManagerEntity;
import org.apache.ibatis.annotations.Mapper;


//@Repository
@Mapper
@DS("dataServer")
public interface MetadataJcdManagerEntityMapper extends BaseMapper<MetadataJcdManagerEntity> {

}
