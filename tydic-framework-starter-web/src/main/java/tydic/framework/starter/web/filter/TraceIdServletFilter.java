package tydic.framework.starter.web.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;
import tydic.framework.core.util.TraceIdUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class TraceIdServletFilter extends OncePerRequestFilter implements Ordered {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String traceId = request.getHeader(TraceIdUtil.traceId);
        if (StrUtil.isBlank(traceId)) {
            traceId = request.getParameter(TraceIdUtil.traceId);
        }
        if (StrUtil.isBlank(traceId)) {
            TraceIdUtil.create();
        } else {
            TraceIdUtil.set(traceId);
            log.info("接收traceId=[{}],path=[{}]", traceId, path);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 100;
    }
}
