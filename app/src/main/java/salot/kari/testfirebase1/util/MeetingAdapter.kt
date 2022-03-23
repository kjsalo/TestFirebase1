package salot.kari.testfirebase1.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import salot.kari.testfirebase1.databinding.ListLayoutBinding
import salot.kari.testfirebase1.model.Meeting

// Meeting adapter is used to populate RecyclerView from the list of meeting objects
class MeetingAdapter(val meetingList: List<Meeting>): RecyclerView.Adapter<MeetingAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingAdapter.ViewHolder {
        val binding = ListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MeetingAdapter.ViewHolder, position: Int) {
        holder.bindItems(meetingList[position])
    }

    //this method is giving the size of the list containing meeting objects
    override fun getItemCount() = meetingList.size


    //the class ViewHolder is holding the list view
    class ViewHolder(val binding: ListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItems(meeting: Meeting) {
            binding.purposeTxt.text = meeting.purpose
            binding.participantTxt.text = meeting.userIds.toString()
            binding.startTxt.text = meeting.startTime
            binding.endTxt.text = meeting.endTime
            binding.roomTxt.text = meeting.roomId
        }
    }
}