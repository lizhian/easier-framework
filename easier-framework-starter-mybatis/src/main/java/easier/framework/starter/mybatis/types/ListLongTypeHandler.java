package easier.framework.starter.mybatis.types;

import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.NumberUtil;
import easier.framework.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 列出long类型处理程序
 *
 * @author lizhian
 * @date 2023年07月05日
 */
public class ListLongTypeHandler extends BaseTypeHandler<List<Long>> {
    public static final ListLongTypeHandler instance = new ListLongTypeHandler();

    private ListLongTypeHandler() {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, this.listToString(parameter));
    }


    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return this.stringToList(value);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return this.stringToList(value);

    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return this.stringToList(value);

    }

    private String listToString(List<Long> parameter) {
        if (parameter == null) {
            return null;
        }
        return parameter.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(StrPool.COMMA));
    }

    private List<Long> stringToList(String value) {
        if (value == null) {
            return null;
        }
        return StrUtil.smartSplit(value)
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(NumberUtil::parseLong)
                .collect(Collectors.toList());
    }
}
