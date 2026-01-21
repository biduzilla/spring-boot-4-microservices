package com.example.user_service.aspect

import com.example.user_service.utils.logger
import lombok.extern.slf4j.Slf4j
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect{
    private val log = logger()

    @Pointcut("execution(* com.example.user_service.service..*(..))")
    fun serviceMethods() {

    }

    @Before("serviceMethods()")
    fun logBefore(joinPoint: JoinPoint) {

        log.info(
            "Called service method: {} with arguments: {}",
            joinPoint.signature.name,
            joinPoint.args
        )
    }

    @AfterReturning(
        pointcut = "serviceMethods()",
        returning = "result"
    )
    fun logAfterReturning(joinPoint: JoinPoint, result: Any?) {
        log.info(
            "Service method: {}, returned: {}",
            joinPoint.signature.name,
            result
        )
    }
}

