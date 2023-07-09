package easier.framework.starter.mybatis.types;

import easier.framework.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * 列表字符串类型处理程序
 *
 * @author lizhian
 * @date 2023年07月05日
 */
public class ListStringTypeHandler extends BaseTypeHandler<List<String>> {
    public static final ListStringTypeHandler instance = new ListStringTypeHandler();

    private ListStringTypeHandler() {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, StrUtil.join(parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        String value = rs.getString(columnName);
        if (value == null) {
            return null;
        }
        return StrUtil.smartSplit(value);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        String value = rs.getString(columnIndex);
        if (value == null) {
            return null;
        }
        return StrUtil.smartSplit(value);
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        String value = cs.getString(columnIndex);
        if (value == null) {
            return null;
        }
        return StrUtil.smartSplit(value);
    }
}
