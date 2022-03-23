package salot.kari.testfirebase1.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import salot.kari.testfirebase1.model.Meeting
import salot.kari.testfirebase1.model.Room
import salot.kari.testfirebase1.model.User

class FirebaseRepo {
    private var fireStoreDatabase = FirebaseFirestore.getInstance()

    //gets the latest meeting id (from counter)
    //a bit clumsy way to add automatic id to collections instead of using Firestore's own
    suspend fun getCounter(): String{
        val counterRef = fireStoreDatabase.collection("counter").document("count")
        var numb: String = ""
        try {
            numb = counterRef.get().await().get("val") as String
        } catch (exception: Exception) {
            Log.w("*****************", "Error getting documents $exception")
        }
        return numb
    }

    //increment by one of the latest meeting id
    suspend fun addCounter(newVal :String){
        val counterVal = HashMap<String, Any>()
        counterVal["val"] = newVal
        fireStoreDatabase.collection("counter").document("count")
            .set(counterVal)
            .addOnSuccessListener { Log.d("iiiiiiiiiiiiiiiii", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("iiiiiiiiiiiiiiii", "Error writing document", e) }
    }

    //returns a list of User objects from the Firestore
    suspend fun getUsers(): List<User> {

        var userList:List<User> = mutableListOf()
        val userRef = fireStoreDatabase.collection("users")
        try {
            userList = userRef.get().await().documents.mapNotNull { snapShot ->
                snapShot.toObject(User::class.java)

            }
        } catch (exception: Exception) {
            Log.w("*****************", "Error getting documents $exception")
        }
        return userList

    }

    //update users collection's particular (defined by userIDs list) documents' meetingIds array
    suspend fun updateUsers(usrIDs: List<String>, mId: String): Boolean{
        for (userID in usrIDs){
            val userRef = fireStoreDatabase.collection("users").document(userID)
            userRef.update("meetingIds", FieldValue.arrayUnion(mId))
        }
        return true
    }

    //returns a list of Room objects from the Firestore
    suspend fun getRooms(): List<Room> {
        var roomList: List<Room> = mutableListOf()
        val roomRef = fireStoreDatabase.collection("rooms")
        try {
            roomList = roomRef.get().await().documents.mapNotNull { snapShot ->
                snapShot.toObject(Room::class.java)
            }
        } catch (exception: Exception) {
            Log.w("*****************", "Error getting documents $exception")
        }
        return roomList
    }

    //returns a list of Meeting objects from the Firestore based on userIds
    suspend fun getMeetings(sList: List<String>): List<Meeting>{
        var meetingList:List<Meeting> = mutableListOf()
        val meetRef = fireStoreDatabase.collection("meetings")
        try {
            meetingList = meetRef.whereArrayContainsAny("userIds",
                sList).get().await().documents.mapNotNull { snapShot ->
                snapShot.toObject(Meeting::class.java)
            }
        } catch (exception: Exception) {
            Log.w("*****************", "Error getting documents $exception")
        }
        return meetingList
    }

    //returns a list of Meeting objects from the Firestore based on roomId
    suspend fun getRoomMeetings(rId: String): List<Meeting>{

        var meetingList:List<Meeting> = mutableListOf()
        val meetRef = fireStoreDatabase.collection("meetings")
        try {
            meetingList = meetRef.whereEqualTo("roomId",
                rId).get().await().documents.mapNotNull { snapShot ->
                snapShot.toObject(Meeting::class.java)
            }
        } catch (exception: Exception) {
            Log.w("*****************", "Error getting documents $exception")
        }
        return meetingList

    }

    //upload a meeting data into Firestore
    suspend fun setMeetings(meet: Meeting, id: String ){
        val nMeet = HashMap<String, Any>()
        nMeet["startTime"] = meet.startTime
        nMeet["endTime"] = meet.endTime
        nMeet["userIds"] = meet.userIds
        nMeet["roomId"] = meet.roomId
        nMeet["purpose"] = meet.purpose


        fireStoreDatabase.collection("meetings").document(id)
            .set(meet)
            .addOnSuccessListener { Log.d("iiiiiiiiiiiiiiiii", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("iiiiiiiiiiiiiiii", "Error writing document", e) }

    }
}