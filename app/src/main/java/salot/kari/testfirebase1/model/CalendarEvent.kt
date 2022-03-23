package salot.kari.testfirebase1.model

import java.time.LocalDateTime

data class CalendarEvent(
    var startTime: String = "",
    var endTime: String = "",
    var meetingTopic: String = ""
)
