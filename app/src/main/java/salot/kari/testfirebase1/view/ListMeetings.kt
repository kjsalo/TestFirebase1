package salot.kari.testfirebase1.view

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import salot.kari.testfirebase1.databinding.FragmentListMeetingsBinding
import salot.kari.testfirebase1.model.Meeting
import salot.kari.testfirebase1.model.User
import salot.kari.testfirebase1.util.BackHome
import salot.kari.testfirebase1.util.MeetingAdapter
import salot.kari.testfirebase1.viewmodel.ListMeetingsViewModel

//Fragment, which enables selecting a user and displaying selected user's meetings
//fragment is communicating ListMeetingsViewModel by calling relevant methods in ViewModel
//depending on user interaction and using Observer pattern to get data changes
class ListMeetings : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var homeInterface: BackHome
    private lateinit var binding: FragmentListMeetingsBinding

    private lateinit var vModel: ListMeetingsViewModel
    private lateinit var uSpinner: Spinner
    private lateinit var gMeetings: Button
    private lateinit var lDone: Button
    private lateinit var recView: RecyclerView

    private var selUser: String = ""
    private var userList: MutableList<User> = mutableListOf()
    private var userNames: MutableList<String> = mutableListOf("Select user")
    private var idList: MutableList<String> = mutableListOf()
    private var modMeetingList: List<Meeting> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListMeetingsBinding.inflate(inflater, container, false)
        val view = binding.root
        vModel = ViewModelProvider(this).get(ListMeetingsViewModel::class.java)
        lDone = binding.listDone
        lDone.setOnClickListener { goHome() }

        activity?.let {
            instantiatehomeInterface(it)
        }

        obs()
        uSpinner = binding.userSpinner
        val userAdapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item,userNames)
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        uSpinner.adapter = userAdapter
        uSpinner.setOnItemSelectedListener(this)

        recView = binding.mList

        gMeetings = binding.getUserMeetings
        gMeetings.setOnClickListener { getM() }

        return view
    }

    // method for listening changes in users using Observer pattern
    private fun obs(){
        vModel.myUsers.observe(viewLifecycleOwner, Observer {
            for (user in it){
                userList.add(user)
                userNames.add(user.name)
            }
        })
    }

    // method for listening changes in user meetings using Observer pattern
    private fun getM(){
        for (user in userList){
            if(selUser == user.name){
                idList.add(user.id)
            }
        }
        vModel.getMeetings(idList)
        vModel.myMeetings.observe(viewLifecycleOwner, Observer {
            modMeetingList = it
            recView.adapter = MeetingAdapter(modMeetingList)
        })
    }

    //this method is called when user makes a selection in Spinner
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            selUser = parent.getItemAtPosition(pos).toString()
            uSpinner.prompt = selUser

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    private fun instantiatehomeInterface(context: FragmentActivity) {
        homeInterface = context as BackHome
    }

    private fun goHome(){
        homeInterface.swapToHomeFrag()
    }

}