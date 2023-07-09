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
 * 列表int类型处理程序
 *
 * @author lizhian
 * @date 2023年07月05日
 */
public class ListIntTypeHandler extends BaseTypeHandler<List<Integer>> {
    public static final ListIntTypeHandler instance = new ListIntTypeHandler();

    private ListIntTypeHandler() {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Integer> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, this.listToString(parameter));
    }


    @Override
    public List<Integer> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        return this.stringToList(value);
    }

    @Override
    public List<Integer> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        return this.stringToList(value);

    }

    @Override
    public List<Integer> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        return this.stringToList(value);

    }

    private String listToString(List<Integer> parameter) {
        if (parameter == null) {
            return null;
        }
        return parameter.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(StrPool.COMMA));
    }

    private List<Integer> stringToList(String value) {
        if (value == null) {
            return null;
        }
        return StrUtil.smartSplit(value)
                .stream()
                .filter(StrUtil::isNotBlank)
                .map(NumberUtil::parseInt)
                .collect(Collectors.toList());
    }
}
