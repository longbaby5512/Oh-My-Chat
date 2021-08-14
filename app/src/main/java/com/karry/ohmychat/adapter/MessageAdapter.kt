package com.karry.ohmychat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.ItemReceivedMessageBinding
import com.karry.ohmychat.databinding.ItemSentMessageBinding
import com.karry.ohmychat.model.Message
import com.karry.ohmychat.utils.getColorResource
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val messages: ArrayList<Message>, private val senderId: String, private val fromTo: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onItemClicked: OnItemClicked? = null

    inner class SentMessageViewHolder(private val binding: ItemSentMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding) {
                messageSent.text = message.message
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale("vi", "VN"))
                timeSent.text = simpleDateFormat.format(message.timestamp)
                statusSent.text = if (message.isSeen) "Seen" else "Received"
            }
        }

        init {
            with(binding) {
                messageSent.apply {
                    setOnClickListener {
                        if (timeSent.visibility == View.GONE) {
                            timeSent.visibility = View.VISIBLE
                            statusSent.visibility = View.VISIBLE
                        } else {
                            timeSent.visibility = View.GONE
                            statusSent.visibility = View.GONE
                        }
                    }

                    setOnLongClickListener { onItemClicked?.onItemLongClicked(it, adapterPosition) ?: false }
                }

                when (fromTo) {
                    1 -> {
                        messageSent.setTextColor(getColorResource(root.context, R.color.chats_100))
                        var drawable = ContextCompat.getDrawable(
                            root.context.applicationContext,
                            R.drawable.background_sent_message
                        )
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(
                            drawable,
                            getColorResource(root.context, R.color.chats_700)
                        )
                        messageSent.background = drawable
                    }
                    2 -> {
                        messageSent.setTextColor(getColorResource(root.context, R.color.search_100))
                        var drawable = ContextCompat.getDrawable(
                            root.context.applicationContext,
                            R.drawable.background_sent_message
                        )
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(
                            drawable,
                            getColorResource(root.context, R.color.search_700)
                        )
                        messageSent.background = drawable
                    }
                }
            }
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemReceivedMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding) {
                messageSent.text = message.message
                val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale("vi", "VN"))
                timeSent.text = simpleDateFormat.format(message.timestamp)
                statusSent.text = if (message.isSeen) "Seen" else "Received"
            }
        }

        init {
            with(binding) {
                messageSent.apply {
                    setOnClickListener {
                        if (timeSent.visibility == View.GONE) {
                            timeSent.visibility = View.VISIBLE
                            statusSent.visibility = View.VISIBLE
                        } else {
                            timeSent.visibility = View.GONE
                            statusSent.visibility = View.GONE
                        }
                    }

                    setOnLongClickListener { onItemClicked?.onItemLongClicked(it, adapterPosition) ?: false }
                }

                when (fromTo) {
                    1 -> {
                        var drawable = ContextCompat.getDrawable(
                            root.context.applicationContext,
                            R.drawable.background_received_message
                        )
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(
                            drawable,
                            getColorResource(root.context, R.color.chats_1000)
                        )
                        messageSent.background = drawable
                    }
                    2 -> {
                        var drawable = ContextCompat.getDrawable(
                            root.context.applicationContext,
                            R.drawable.background_received_message
                        )
                        drawable = DrawableCompat.wrap(drawable!!)
                        DrawableCompat.setTint(
                            drawable,
                            getColorResource(root.context, R.color.search_1000)
                        )
                        messageSent.background = drawable
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TEXT_SENT) {
            val binding = ItemSentMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SentMessageViewHolder(binding)
        }
        val binding = ItemReceivedMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceivedMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            VIEW_TEXT_SENT -> (holder as SentMessageViewHolder).bind(messages[position])
            VIEW_TEXT_RECEIVED -> (holder as ReceivedMessageViewHolder).bind(messages[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].senderId == senderId) {
            return VIEW_TEXT_SENT
        }
        return VIEW_TEXT_RECEIVED
    }

    override fun getItemCount() = messages.size

    fun setOnItemClickListener(listener: OnItemClicked) {
        onItemClicked = listener
    }

    companion object {
        const val VIEW_TEXT_SENT = 1
        const val VIEW_TEXT_RECEIVED = 2
    }
}