package com.karry.ohmychat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.ItemRecentChatListBinding
import com.karry.ohmychat.model.RecentChat
import com.karry.ohmychat.utils.convert
import java.text.SimpleDateFormat
import java.util.*

class RecentChatAdapter(private val conversations: ArrayList<RecentChat>) : RecyclerView.Adapter<RecentChatAdapter.RecentChatViewHolder>() {
    private var onItemClicked: OnItemClicked? = null

    inner class RecentChatViewHolder(private val binding: ItemRecentChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recent: RecentChat) {
            with(binding) {
                recentProfileImage.setImageBitmap(recent.conversionImage?.let { convert(it) })
                recentName.text = recent.conversionName
                recentMess.text = recent.lastMessage
                val simpleDateFormat = SimpleDateFormat("hh:mm aa", Locale("vi", "VN"))
                recentTime.text = simpleDateFormat.format(recent.timestamp)
                if (recent.conversionStatus == true) {
                    recentStatus.setBackgroundResource(R.drawable.online_status)
                } else {
                    recentStatus.setBackgroundResource(R.drawable.offline_status)
                }
            }
        }

        init {
            with(binding.root) {
                setOnClickListener { onItemClicked?.onItemClicked(it, adapterPosition) }
                setOnLongClickListener { onItemClicked?.onItemLongClicked(it, adapterPosition) ?: false }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatViewHolder {
        val binding =
            ItemRecentChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentChatViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount() = conversations.size

    fun setOnItemClickListener(listener: OnItemClicked) {
        onItemClicked = listener
    }
}