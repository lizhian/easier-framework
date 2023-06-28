package easier.framework.starter.mybatis.types;

import easier.framework.core.plugin.enums.EnumCodec;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 枚举处理器
 */
@Slf4j
@Getter
public class EnumCodecTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    private final Class<E> type;
    private final EnumCodec<E> enumCodec;

    public EnumCodecTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enumCodec = (EnumCodec<E>) EnumCodec.of(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (this.enumCodec.isIntValue()) {
            ps.setInt(i, this.enumCodec.getEnumValueAsInt(parameter));
        } else {
            ps.setString(i, this.enumCodec.getEnumValueAsStr(parameter));
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : this.enumCodec.getEnumInstance(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : this.enumCodec.getEnumInstance(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : this.enumCodec.getEnumInstance(value);
    }
}
