package com.karry.ohmychat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.karry.ohmychat.R
import com.karry.ohmychat.databinding.ItemUserListBinding
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.convert

class UserAdapter(private val users: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var context: Context
    private var onItemClicked: OnItemClicked? = null

    inner class UserViewHolder(private val binding: ItemUserListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            with(binding) {
                userName.text = user.name
                userEmail.text = user.email
                userProfileImage.setImageBitmap(convert(user.imageBase64))
                if (user.status) {
                    userStatus.setBackgroundResource(R.drawable.online_status)
                } else {
                    userStatus.setBackgroundResource(R.drawable.offline_status)
                }
            }
        }

        init {
            binding.root.apply {
                setOnClickListener { onItemClicked?.onItemClicked(it, adapterPosition) }
                setOnLongClickListener { onItemClicked?.onItemLongClicked(it, adapterPosition) ?: false }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        context = parent.context
        val binding = ItemUserListBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size


    fun setOnItemClickListener(listener: OnItemClicked) {
        onItemClicked = listener
    }
}