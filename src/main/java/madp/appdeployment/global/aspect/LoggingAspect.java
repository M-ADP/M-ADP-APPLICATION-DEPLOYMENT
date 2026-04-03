package madp.appdeployment.global.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("@annotation(madp.appdeployment.global.annotation.Trace) || @within(madp.appdeployment.global.annotation.Trace)")
    public void loggingTraceAnnotationPointcut() {}

    @Before("loggingTraceAnnotationPointcut()")
    public void logBefore(final JoinPoint joinPoint) {
        log.info("[TRACE][{}][START] arguments={}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "loggingTraceAnnotationPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("[TRACE][{}][END] result={}", joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(pointcut = "loggingTraceAnnotationPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.warn("[TRACE][{}][ERROR] exClass={}, exMessage={}",
                joinPoint.getSignature().getName(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                e);
    }
}
