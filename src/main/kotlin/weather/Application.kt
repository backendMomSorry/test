package weather

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.EnableAsync

@EnableRetry
@EnableAsync
@SpringBootApplication
class Application

@Suppress("detekt.SpreadOperator")
fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
