package com.club.fund.interceptor;

import com.club.fund.entity.SysLog;
import com.club.fund.service.SysLogService;
import com.club.fund.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class ApiLogInterceptor implements HandlerInterceptor {

    private static final String START_TIME_ATTR = "apiLogStartTime";
    private final SysLogService sysLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME_ATTR, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = request.getAttribute(START_TIME_ATTR) instanceof Long ? (Long) request.getAttribute(START_TIME_ATTR) : System.currentTimeMillis();
        long duration = System.currentTimeMillis() - startTime;

        SysLog log = new SysLog();
        log.setUserId(SecurityUtil.getCurrentUserId());
        log.setUsername(SecurityUtil.getCurrentUsername());

        String operation = request.getRequestURI();
        if (handler instanceof HandlerMethod method) {
            operation = method.getBeanType().getSimpleName() + "#" + method.getMethod().getName();
        }
        log.setOperation(operation);
        log.setMethod(request.getMethod() + " " + request.getRequestURI());
        log.setParams(request.getQueryString());
        log.setIp(request.getRemoteAddr());
        log.setStatus(ex == null && response.getStatus() < 400 ? 1 : 0);
        log.setErrorMsg(ex == null ? null : ex.getMessage());
        log.setDuration(duration);

        try {
            sysLogService.saveLog(log);
        } catch (Exception ignored) {
        }
    }
}
