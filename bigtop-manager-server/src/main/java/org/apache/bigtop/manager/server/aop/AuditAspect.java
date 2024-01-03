package org.apache.bigtop.manager.server.aop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.apache.bigtop.manager.server.orm.entity.AuditLog;
import org.apache.bigtop.manager.server.orm.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

import static org.apache.bigtop.manager.common.constants.Constants.TRACE_ID_KEY;

@Slf4j
@Aspect
@Configuration
@ConditionalOnProperty(value = "bigtop.manager.server.audit", havingValue = "true")
public class AuditAspect {

    private static final String POINT_CUT = "@annotation(io.swagger.v3.oas.annotations.Operation)";

    @Resource
    private AuditLogRepository auditLogRepository;

    @Before(value = POINT_CUT)
    public void before(JoinPoint joinPoint) {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID_KEY, traceId);

        AuditLog auditLog = new AuditLog();
        auditLog.setTraceId(traceId);
        auditLog.setUserId(SessionUserHolder.getUserId());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            // obtain request uri
            HttpServletRequest request = attributes.getRequest();
            String uri = request.getRequestURI();
            // obtain controller name
            Class<?> controller = joinPoint.getThis().getClass();
            log.info("controller: {}", controller);
            Tag annotation = controller.getAnnotation(Tag.class);
            String apiName = "";
            String apiDesc = "";
            if (annotation != null) {
                apiName = annotation.name();
                apiDesc = annotation.description();
            }
            // obtain method name
            MethodSignature ms = (MethodSignature) joinPoint.getSignature();
            String methodName = ms.getName();
            // obtain method desc
            String operationSummary = "";
            String operationDesc = "";
            Operation methodApiOperation = ms.getMethod().getDeclaredAnnotation(Operation.class);
            if (methodApiOperation != null) {
                operationSummary = methodApiOperation.summary();
                operationDesc = methodApiOperation.description();
            }

            auditLog.setUri(uri);
            auditLog.setTagName(apiName);
            auditLog.setTagDesc(apiDesc);
            auditLog.setOperationSummary(operationSummary);
            auditLog.setOperationDesc(operationDesc);
            auditLog.setArgs(JsonUtils.writeAsString(joinPoint.getArgs()));

            log.debug("auditLog: {}", auditLog);
            log.debug("request method：{}.{}", joinPoint.getSignature().getDeclaringTypeName(), methodName);

            auditLogRepository.save(auditLog);
        }
    }

    @After(value = POINT_CUT)
    public void after(JoinPoint joinPoint) {
        MDC.clear();
    }
}
