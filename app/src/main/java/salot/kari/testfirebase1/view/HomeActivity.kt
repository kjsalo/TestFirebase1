package salot.kari.testfirebase1.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import salot.kari.testfirebase1.databinding.ActivityHomeBinding
import salot.kari.testfirebase1.util.BackHome

//Simple activity which manages fragments and implements BackHome interface
class HomeActivity : AppCompatActivity(), BackHome {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var fragHome: Introduction
    private lateinit var fragNM: NewMeeting
    private lateinit var fragLM: ListMeetings
    private lateinit var fTransaction: FragmentTransaction
    private lateinit var fManager: FragmentManager
    private lateinit var fragContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fragHome= Introduction()
        fragContainer = binding.fcontainer
        fManager= supportFragmentManager
        fTransaction= fManager.beginTransaction()

        fTransaction.add(fragContainer.id, fragHome)
        fTransaction.commit()
        binding.bAppointment.setOnClickListener { openNewMeeting() }
        binding.vMeeting.setOnClickListener { listMeetings() }
    }

    private fun openNewMeeting(){
        fragNM = NewMeeting()
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(fragContainer.id, fragNM)
        fTransaction.commit()
    }

    private fun listMeetings(){
        fragLM = ListMeetings()
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(fragContainer.id, fragLM)
        fTransaction.commit()
    }

    //This method is called by fragments in order to show Introduction Fragment
    override fun swapToHomeFrag() {
        fTransaction = fManager.beginTransaction()
        fTransaction.replace(fragContainer.id, fragHome)
        fTransaction.commit()
    }
}