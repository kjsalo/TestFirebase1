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
import salot.kari.testfirebase1.model.Meeting
import salot.kari.testfirebase1.model.Room
import salot.kari.testfirebase1.model.User
import salot.kari.testfirebase1.repository.FirebaseRepo

class ListMeetingsViewModel: ViewModel() {
    private val repo: FirebaseRepo = FirebaseRepo()

    //get users from FirebaseRepository and pass them as a List of User objects to ListMeeting fragment
    val myUsers = liveData(Dispatchers.IO) {
        val users = repo.getUsers()
        emit(users)
    }

    //pass meeting info as list of Meeting objects to ListMeeting fragment
    val myMeetings: MutableLiveData<List<Meeting>> by lazy {
        MutableLiveData<List<Meeting>>()
    }

    //public method to be called by ListMeeting fragment when user wants to see meetings of the selected user
    fun getMeetings(usrID: MutableList<String>){
        getM(usrID)
    }

    //method for getting meetings, users, rooms from Firebase repository
    private fun getM(uID: MutableList<String>){
        val modList: List<String> = uID
        viewModelScope.launch(Dispatchers.Main) {
            val mList: Deferred<List<Meeting>> = async(Dispatchers.IO){repo.getMeetings(modList)}
            val roomList: Deferred<List<Room>> = async(Dispatchers.IO){repo.getRooms()}
            val userList: Deferred<List<User>> = async(Dispatchers.IO){repo.getUsers()}
            modMeetings(mList.await(),roomList.await(), userList.await())
        }

    }

    //Modify meeting objects by replacing ids with names (both rooms and users)
    //pass meetings info as a list of modified meeting objects to ListMeeting fragment
    private fun modMeetings(meet: List<Meeting>,room: List<Room>, user: List<User>){
        for (meeting in meet){
            for (r in room){
                if (meeting.roomId == r.id) {
                    meeting.roomId = r.name
                }
            }
            val tempList: MutableList<String> = mutableListOf()
            for (id in meeting.userIds){
                    for (u in user){
                        if (id == u.id) {
                            tempList.add(u.name)
                        }
                }
            }
            meeting.userIds = tempList
        }

        for (m in meet){
            Log.v("meetings ****",m.roomId + " " + m.userIds.toString())
        }
        myMeetings.value = meet

    }

}