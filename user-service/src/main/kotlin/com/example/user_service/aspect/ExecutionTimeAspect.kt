package com.example.user_service.aspect

import com.example.user_service.utils.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Component
class ExecutionTimeAspect {
    private val log = logger()

    @Pointcut("execution(* com.example.user_service.controller.*.*(..))")
    fun controllerMethods() {
    }

    @Around("controllerMethods()")
    fun measureExecutionTime(pjp: ProceedingJoinPoint): Any? {
        val start = System.nanoTime()

        return try {
            pjp.proceed()
        } finally {
            val end = System.nanoTime()
            val elapsedNs = end - start
            val elapsedMs = TimeUnit.NANOSECONDS.toMillis(elapsedNs)
            val signature = pjp.signature.toShortString()

            log.info(
                "Controller method {} executed in {} ms",
                signature,
                elapsedMs
            )
        }
    }
}