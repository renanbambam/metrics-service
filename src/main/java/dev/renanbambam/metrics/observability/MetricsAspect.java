package dev.renanbambam.metrics.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Aspect
@Component
public class MetricsAspect {

    private final TransactionMetrics metrics;

    public MetricsAspect(TransactionMetrics metrics) {
        this.metrics = metrics;
    }

    @Around("execution(* dev.renanbambam.metrics.domain.service.TransactionService.*(..))")
    public Object measureTransactionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        try {
            return joinPoint.proceed();
        } catch (Throwable ex) {
            metrics.recordFailure(ex.getClass().getSimpleName());
            throw ex;
        } finally {
            metrics.recordProcessingTime(Duration.between(start, Instant.now()));
        }
    }
}
