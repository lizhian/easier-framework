package easier.framework.starter.mybatis.lambda;

import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Getter
@RequiredArgsConstructor
public class ColumnSFunction<T, R> implements SFunction<T, R> {
    private final String column;
    private final String columnSelect;

    @SneakyThrows
    @Override
    public R apply(T t) {
        return null;
    }

    public ColumnCache toColumnCache() {
        return new ColumnCache(this.column, this.columnSelect);
    }
}
