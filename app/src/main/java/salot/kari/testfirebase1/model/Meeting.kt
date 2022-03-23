package salot.kari.testfirebase1.model

data class Meeting(
    var id: String = "",
    var startTime: String = "",
    var endTime: String = "",
    var userIds: List<String> = emptyList(),
    var roomId: String = "",
    var purpose: String = ""
)


