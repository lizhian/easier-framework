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

    private volatile static long dataCenterId = cn.hutool.core.util.IdUtil.getDataCenterId(32);
    private volatile static long workerId = cn.hutool.core.util.IdUtil.getWorkerId(dataCenterId, 32);

    @Nonnull
    public static String nextIdStr() {
        return Singleton.get(Snowflake.class, workerId, dataCenterId).nextIdStr();
    }

    public static long nextId() {
        return Singleton.get(Snowflake.class, workerId, dataCenterId).nextId();
    }

    public synchronized static void reset(long workerId, long dataCenterId) {
        IdUtil.workerId = workerId;
        IdUtil.dataCenterId = dataCenterId;
    }
}
