package easier.framework.starter.web.filter;

import cn.hutool.core.util.StrUtil;
import easier.framework.core.Easier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TraceId加载
 */
@Slf4j
public class TraceIdServletFilter extends OncePerRequestFilter implements Ordered {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String traceId = request.getHeader(Easier.TraceId.x_trace_id);
        if (StrUtil.isBlank(traceId)) {
            traceId = request.getParameter(Easier.TraceId.x_trace_id);
        }
        if (StrUtil.isBlank(traceId)) {
            Easier.TraceId.reset();
        } else {
            Easier.TraceId.set(traceId);
            if (log.isDebugEnabled()) {
                log.debug("接收 traceId={}, path={}", traceId, path);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 100;
    }
}
