package easier.framework.starter.mybatis.types;

import cn.hutool.core.date.DateTime;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * DateTime处理器
 */
public class DateTimeTypeHandler extends BaseTypeHandler<DateTime> {
    public static final DateTimeTypeHandler instance = new DateTimeTypeHandler();

    private DateTimeTypeHandler() {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setDate(i, parameter.toSqlDate());
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        Date value = rs.getDate(columnName);
        if (value == null) {
            return null;
        }
        return new DateTime(value);
    }

    @Override
    public DateTime getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        Date value = rs.getDate(columnIndex);
        if (value == null) {
            return null;
        }
        return new DateTime(value);
    }

    @Override
    public DateTime getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        Date value = cs.getDate(columnIndex);
        if (value == null) {
            return null;
        }
        return new DateTime(value);
    }
}
