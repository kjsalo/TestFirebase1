package salot.kari.testfirebase1.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog
import com.anurag.multiselectionspinner.MultiSpinner
import com.google.android.material.snackbar.Snackbar
import salot.kari.testfirebase1.R
import salot.kari.testfirebase1.databinding.FragmentNewMeetingBinding
import salot.kari.testfirebase1.model.Room
import salot.kari.testfirebase1.model.User
import salot.kari.testfirebase1.util.BackHome
import salot.kari.testfirebase1.viewmodel.NewMeetingViewModel
import java.time.LocalDateTime
import java.util.*

//Fragment, which enables creating a meeting by selecting participants, meeting length in hours,
//meeting room, and defining meeting purpose.
//
//multiselection dropdown menu is implemented by using Anuraganu Punalur's Github project
//https://github.com/AnuraganuPunalur/Multi-Selection-Spinner-Android
//
//fragment is communicating NewMeetingViewModel by calling relevant methods in ViewModel
//depending on user interaction and using Observer pattern to get data changes
class NewMeeting : Fragment(), MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener, AdapterView.OnItemSelectedListener {

    private lateinit var homeInterface: BackHome
    private lateinit var binding: FragmentNewMeetingBinding
    private var meetingLength = ""
    private var meetingRoom = ""
    private var meetingRoomId = ""
    private var meetingPurpose = ""
    private var paForce: Boolean = false
    private var leForce: Boolean = false
    private var mrForce: Boolean = false
    private lateinit var vModel: NewMeetingViewModel
    private lateinit var sMulti: MultiSpinner
    private lateinit var hSpinner: Spinner
    private lateinit var rSpinner: Spinner
    private lateinit var sMeet: Button
    private lateinit var bDone: Button
    private var idList: MutableList<String> = mutableListOf()
    private var userList: MutableList<User> = mutableListOf()
    private var selectedList: MutableList<String> = mutableListOf()
    private var nameList: MutableList<String> = mutableListOf()
    private var roomList: MutableList<Room> = mutableListOf()
    private var roomNames: MutableList<String> = mutableListOf("Select meeting room")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewMeetingBinding.inflate(inflater, container, false)
        val view = binding.root
        vModel = ViewModelProvider(this).get(NewMeetingViewModel::class.java)

        sMulti = binding.spinnerMultiSpinner
        hSpinner = binding.hourSpinner
        rSpinner = binding.roomSpinner

        //multiselection dropdown menu is implemented by using Anuraganu Punalur's Github project
        //this enables selection of participants
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meeting_length,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            hSpinner.adapter = adapter
            hSpinner.setOnItemSelectedListener(this)
        }

        //Spinner using StringArray defined in resources and enabling meeting room selection
        val roomAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,roomNames)
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        rSpinner.adapter = roomAdapter
        rSpinner.setOnItemSelectedListener(this)

        obs()

        //edit text captures the purpose of meeting
        binding.et1.doAfterTextChanged{text ->
            meetingPurpose=text.toString()
            }

        sMeet = binding.sMeeting
        sMeet.setOnClickListener { newM() }
        activity?.let {
            instantiatehomeInterface(it)
        }
        bDone = binding.allDone
        bDone.setOnClickListener { goHome() }
        return view
    }

    private fun instantiatehomeInterface(context: FragmentActivity) {
        homeInterface = context as BackHome
    }

    // method for listening changes in users and rooms using Observer pattern
    private fun obs(){

         vModel.myUsers.observe(viewLifecycleOwner, Observer { it ->
             for (user in it){
                 userList.add(user)
                 nameList.add(user.name)
             }
             sMulti.setAdapterWithOutImage(context,nameList,this)
             sMulti.initMultiSpinner(context,sMulti)

         })

        vModel.myRooms.observe(viewLifecycleOwner, Observer { it ->
            for (room in it){
                roomList.add(room)
                roomNames.add(room.name)
            }
        })

    }

    //this method is called when user makes participants selection in Spinner
    override fun OnMultiSpinnerItemSelected(chosenItems: MutableList<String>?) {
        for (i in chosenItems!!.indices){
            selectedList.add(chosenItems[i])
            paForce = true
        }
    }

    //this method is called when user makes meeting length selection in Spinner
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if(parent.id == R.id.hourSpinner) {
            meetingLength = parent.getItemAtPosition(pos).toString()
            hSpinner.prompt = meetingLength
            leForce = true
        } else {
            meetingRoom = parent.getItemAtPosition(pos).toString()
            rSpinner.prompt = meetingRoom
            mrForce = true
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    //This method calls ViewModel's addMeeting method in order to enable a new meeting creation
    //In addition, method listens if the meeting was created by listening observer and
    //calling device's calendar app using new meeting's start and end time and purpose
    private fun newM(){
        if (paForce && leForce && mrForce) {

            for (usr in userList) {
                for (usrName in selectedList) {
                    if (usrName == usr.name) {
                        idList.add(usr.id)
                    }
                }
            }

            for (r in roomList) {
                if (meetingRoom == r.name) meetingRoomId = r.id
            }

            vModel.addMeeting(idList, meetingLength.toInt(), meetingRoomId, meetingPurpose)
        } else {
            Snackbar.make(binding.root,"Some mandatory field values missing", Snackbar.LENGTH_SHORT)
                .show()

        }
        vModel.myEvent.observe(viewLifecycleOwner, Observer {
            val sTime = LocalDateTime.parse(it.startTime)
            val eTime = LocalDateTime.parse(it.endTime)
            val title = it.meetingTopic
            val startTime = Calendar.getInstance()
            //for some reason monthValue is giving a wrong month at least in my Galaxy S10, thus I need to
            //subtract 1 from the value ????
            startTime.set(sTime.year,sTime.monthValue-1, sTime.dayOfMonth, sTime.hour,0,0)

            val endTime = Calendar.getInstance()
            endTime.set(eTime.year,eTime.monthValue-1, eTime.dayOfMonth, eTime.hour,0,0)

            val i = Intent(Intent.ACTION_EDIT)

            i.type = "vnd.android.cursor.item/event"
            i.putExtra("beginTime", startTime.timeInMillis)
            i.putExtra("endTime", endTime.timeInMillis)
            i.putExtra("title", title)
            startActivity(i)
        })
    }

    private fun goHome(){
        homeInterface.swapToHomeFrag()
    }


}