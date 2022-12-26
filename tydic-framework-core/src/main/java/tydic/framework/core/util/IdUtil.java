package tydic.framework.core.util;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;

/**
 * 主键工具类
 */
@Slf4j
public class IdUtil {
    private volatile static Snowflake snowflake = Singleton.get(Snowflake.class);

    @Nonnull
    public synchronized static String nextIdStr() {
        return snowflake.nextIdStr();
    }

    public synchronized static long nextId() {
        return snowflake.nextId();
    }

    public synchronized static void reset(long workerId, long dataCenterId) {
        snowflake = new Snowflake(workerId, dataCenterId);
    }
}
