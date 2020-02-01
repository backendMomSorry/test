package weather

import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.util.HashMap
import java.util.concurrent.ScheduledFuture

@Service
class ScheduleTaskService(var scheduler: TaskScheduler) {

    var jobsMap: MutableMap<String, ScheduledFuture<*>?> = HashMap()

    fun addTaskToScheduler(city: String, task: Runnable, delay: Long) {
        val scheduledTask= scheduler.scheduleAtFixedRate(task, delay)
        jobsMap[city] = scheduledTask
    }

    fun removeTaskFromScheduler(city: String) {
        val scheduledTask: ScheduledFuture<*>? = jobsMap[city]
        if (scheduledTask != null) {
            scheduledTask.cancel(true)
            jobsMap[city] = null
        }
    }

    // A context refresh event listener
    @EventListener(*[ContextRefreshedEvent::class])
    fun contextRefreshedEvent() { // Get all tasks from DB and reschedule them in case of context restarted
    }

}
