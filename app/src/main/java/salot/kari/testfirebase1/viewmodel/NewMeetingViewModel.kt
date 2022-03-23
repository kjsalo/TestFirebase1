package salot.kari.testfirebase1.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Deferred

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import salot.kari.testfirebase1.model.CalendarEvent
import salot.kari.testfirebase1.model.Meeting
import salot.kari.testfirebase1.repository.FirebaseRepo
import java.time.LocalDate
import java.time.LocalDateTime

//ViewModel which communicates with NewMeeting fragment using LiveData and FirebaseRepository using coroutines
//This includes the main functionality in implementing a new meeting
//
//There is a limited algorithm based on today and four consecutive weekdays, in each the timeframe is
//from 8:00 to 17:00, except current day, which will start from next full hour until 17:00
class NewMeetingViewModel: ViewModel() {

    private val repo: FirebaseRepo = FirebaseRepo()
    private lateinit var timeArr: Array<BooleanArray>
    private lateinit var dayArray: ArrayList<LocalDate>

    //get users from FirebaseRepository and pass them as a List of User objects to NewMeeting fragment
    val myUsers = liveData(Dispatchers.IO) {
        val users = repo.getUsers()
        emit(users)
    }
    //get rooms from FirebaseRepository and pass them as a List of Room objects to NewMeeting fragment
    val myRooms = liveData(Dispatchers.IO) {
        val rooms = repo.getRooms()
        Log.w("***************** VM", rooms.toString())
        emit(rooms)
    }

    //pass new meeting info as CalendarEvent object to NewMeeting fragment
    val myEvent: MutableLiveData<CalendarEvent> by lazy {
        MutableLiveData<CalendarEvent>()
    }


    //this method creates a timeArray which will contain info about free and reserved timeslots
    fun addMeeting(sel:MutableList<String>, meetL: Int, meetRoomId: String, meetPurpose: String ) {
        initArray()
        val modList: List<String> = sel
        viewModelScope.launch(Dispatchers.Main) {
            val mList: Deferred<List<Meeting>> = async(Dispatchers.IO){repo.getMeetings(modList)}
            val mrList: Deferred<List<Meeting>> = async(Dispatchers.IO){repo.getRoomMeetings(meetRoomId)}
            val lastId: Deferred<String> = async(Dispatchers.IO){repo.getCounter()}
            addExistingMeetings(mList.await(), mrList.await(),lastId.await(),modList, meetL, meetRoomId, meetPurpose)
        }
    }

    // timeArray contains 9 slots for five consecutive weekdays
    // each slot will contain info if that time is available for a meeting or not
    // first we set all slots true
    private fun initArray(){
        val t:Boolean = true

        timeArr = arrayOf(
            booleanArrayOf(t,t,t,t,t,t,t,t,t),
            booleanArrayOf(t,t,t,t,t,t,t,t,t),
            booleanArrayOf(t,t,t,t,t,t,t,t,t),
            booleanArrayOf(t,t,t,t,t,t,t,t,t),
            booleanArrayOf(t,t,t,t,t,t,t,t,t)
        )
        curTime()
    }

    // if current day is week day (Mon - Fri) then we will check the current time
    // and define already gone time slots to false in timeArray
    private fun curTime(){
        val tString: CharSequence  = "2022-03-19T13:28:20.866"
        val tDateTime = LocalDateTime.parse(tString)
        val tDate: LocalDate = tDateTime.toLocalDate()
        val tDay = tDate.dayOfWeek

        if (tDay.value < 6) {
            val indexT: Int = tDateTime.hour - 8
            for (k in 0..indexT){
                timeArr[0][k]=false
            }
        }

        checkWeekDays()
    }

    // dayArray is an arraylist containing dates for the five rows in timeArray
    // we will define those dates so that weekends will be stripped off
    private fun checkWeekDays(){
        //val tString: CharSequence  = "2022-03-19T13:28:20.866"
        //val tDateTime = LocalDateTime.parse(tString)
        val tDateTime = LocalDateTime.now()
        val tDate: LocalDate = tDateTime.toLocalDate()
        val tDay = tDate.dayOfWeek
        dayArray = ArrayList<LocalDate>()
        if (tDay.value < 6) {
            dayArray.add(0, tDate)
        } else {
            val addD:Long = (8 - tDay.value.toLong())
            dayArray.add(0,tDate.plusDays(addD))
        }
        when(dayArray[0].dayOfWeek.value){
            1 -> {
                dayArray.add(1,dayArray[0].plusDays(1))
                dayArray.add(2,dayArray[0].plusDays(2))
                dayArray.add(3,dayArray[0].plusDays(3))
                dayArray.add(4,dayArray[0].plusDays(4))
            }
            2 -> {
                dayArray.add(1,dayArray[0].plusDays(1))
                dayArray.add(2,dayArray[0].plusDays(2))
                dayArray.add(3,dayArray[0].plusDays(3))
                dayArray.add(4,dayArray[0].plusDays(6))
            }
            3 -> {
                dayArray.add(1,dayArray[0].plusDays(1))
                dayArray.add(2,dayArray[0].plusDays(2))
                dayArray.add(3,dayArray[0].plusDays(5))
                dayArray.add(4,dayArray[0].plusDays(6))
            }
            4 -> {
                dayArray.add(1,dayArray[0].plusDays(1))
                dayArray.add(2,dayArray[0].plusDays(4))
                dayArray.add(3,dayArray[0].plusDays(5))
                dayArray.add(4,dayArray[0].plusDays(6))
            }
            5 -> {
                dayArray.add(1,dayArray[0].plusDays(3))
                dayArray.add(2,dayArray[0].plusDays(4))
                dayArray.add(3,dayArray[0].plusDays(5))
                dayArray.add(4,dayArray[0].plusDays(6))
            }
        }

    }

    // Get relevant meetings from the Firebase, pick up meeting times from those dates
    // that are within dayArray dates. Modify timeArray to exclude found times
    private fun addExistingMeetings(ml: List<Meeting>, mrl: List<Meeting>, mlID: String,modList: List<String>, meetL: Int, meetRoomId: String, meetPurpose: String) {
        for (k in 0..4) {
            val compDate = dayArray[k]
            for (m in ml) {
                if (LocalDateTime.parse(m.startTime).toLocalDate().compareTo(compDate)==0){
                    val tStart = LocalDateTime.parse(m.startTime).hour
                    val tEnd = LocalDateTime.parse(m.endTime).hour
                    val startInd = tStart - 8
                    val endInd = startInd + (tEnd - tStart) - 1
                    for (x in startInd..endInd){
                        timeArr[k][x]=false
                    }
                }
            }
            for (r in mrl) {
                if (LocalDateTime.parse(r.startTime).toLocalDate().compareTo(compDate)==0){
                    val tStart = LocalDateTime.parse(r.startTime).hour
                    val tEnd = LocalDateTime.parse(r.endTime).hour
                    val startInd = tStart - 8
                    val endInd = startInd + (tEnd - tStart) - 1
                    for (x in startInd..endInd){
                        timeArr[k][x]=false
                    }
                }
            }
        }

        finallyAddMeeting(mlID, modList, meetL, meetRoomId, meetPurpose)
    }

    //find the first possible timeslot from the updated timeArr and book a meeting
    //check which room is available
    private fun finallyAddMeeting(id: String, participants: List<String>, length: Int, meetRoomId: String, mPurpose: String){
        var row = 0
        var lastind = 0
        //ei toimi!!!
        outerLoop@for (j in 0..4){
            var ok = 0
            innerLoop@for (k in 0..8){
                 if(timeArr[j][k]) {
                    ok += 1
                }
                if (ok == length){
                    row = j
                    lastind = k
                    break@outerLoop
                }
            }
        }
        val tDate = dayArray[row].toString()
        val tEndHour: Int = 9 + lastind
        val tStartHour: Int = tEndHour - length
        var tStartString = ""
        if (tStartHour>9) {
            tStartString = tDate + "T" + tStartHour + ":00:00.000"
        } else {
            tStartString = tDate + "T0" + tStartHour + ":00:00.000"
        }
        var tEndString = ""
        if (tEndHour>9) {
            tEndString = tDate + "T" + tEndHour + ":00:00.000"
        } else {
            tEndString = tDate + "T0" + tEndHour + ":00:00.000"
        }
        val tsDateTime = LocalDateTime.parse(tStartString)
        val startT: String = tsDateTime.toString()
        val teDateTime = LocalDateTime.parse(tEndString)
        val endT: String = teDateTime.toString()

        val userIds: List<String> = participants
        val roomId: String = meetRoomId
        val purpose: String = mPurpose
        val newM = Meeting(id, startT,endT,userIds,roomId, purpose)
        val newCountValue : Int = id.toInt()
        viewModelScope.launch(Dispatchers.Main) {
            async(Dispatchers.IO){repo.setMeetings(newM, id)}
            async(Dispatchers.IO){repo.addCounter((newCountValue+1).toString())}
            val done: Deferred<Boolean> = async(Dispatchers.IO){ repo.updateUsers(userIds, id)}
            addToCalendar(done.await(), tStartString, tEndString, purpose)
        }
    }

    //pass the new meeting info as a CalendarEvent object to NewMeeting fragment
    private fun addToCalendar(trigger: Boolean, sTime: String, eTime: String, topic: String){
        val newCalEvent = CalendarEvent(sTime,eTime,topic)
        myEvent.value = newCalEvent
    }
}