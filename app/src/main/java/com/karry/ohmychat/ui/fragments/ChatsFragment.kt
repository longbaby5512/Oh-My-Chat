package com.karry.ohmychat.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.DocumentChange
import com.karry.ohmychat.adapter.OnItemClicked
import com.karry.ohmychat.adapter.RecentChatAdapter
import com.karry.ohmychat.databinding.FragmentChatsBinding
import com.karry.ohmychat.model.RecentChat
import com.karry.ohmychat.model.User
import com.karry.ohmychat.utils.Constants.KEY_LAST_MESSAGE
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_ID
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_NAME
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_SENDER_ID
import com.karry.ohmychat.utils.Constants.KEY_SENDER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_SENDER_NAME
import com.karry.ohmychat.utils.Constants.KEY_SENDER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_TIMESTAMP
import com.karry.ohmychat.utils.Constants.KEY_USER_ID
import com.karry.ohmychat.utils.PreferenceManager
import com.karry.ohmychat.viewmodel.DatabaseViewModel

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var conversations: ArrayList<RecentChat>
    private lateinit var adapter: RecentChatAdapter
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        init()
        listenerConversations()
        listener()

        return binding.root
    }

    private fun init() {
        databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(requireContext())
        conversations = ArrayList()
        adapter = RecentChatAdapter(conversations)
        binding.chatList.adapter = adapter
    }

    private fun listenerConversations() {
        listenerConversationWithUserId(KEY_SENDER_ID)
        listenerConversationWithUserId(KEY_RECEIVER_ID)
    }

    private fun listener() {
        val animation = AlphaAnimation(1f, 0.7f)
        adapter.setOnItemClickListener(object : OnItemClicked {
            override fun onItemClicked(v: View, position: Int) {
                super.onItemClicked(v, position)
                v.startAnimation(animation)
                var user: User
                databaseViewModel.fetchUser(conversations[position].conversionId!!)
                databaseViewModel.user.observe(viewLifecycleOwner) {
                    user = it
                    val action = ViewPaperFragmentDirections.actionViewPaperFragmentToMessageFragment(user, 1)
                    findNavController().navigate(action)
                }
            }

            override fun onItemLongClicked(v: View, position: Int): Boolean {
                v.startAnimation(animation)
                return super.onItemLongClicked(v, position)
            }
        })
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun listenerConversationWithUserId(id: String) {
        databaseViewModel.fetchConversions(id, preferenceManager.getString(KEY_USER_ID)!!)
        databaseViewModel.documentChangeList.observe(viewLifecycleOwner) {
            for (documentChange in it) {
                when (documentChange.type) {
                    DocumentChange.Type.ADDED -> {
                        val senderId = documentChange.document.getString(KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(KEY_RECEIVER_ID)
                        val chatMessage = RecentChat()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        chatMessage.timestamp = documentChange.document.getLong(KEY_TIMESTAMP)
                        if (preferenceManager.getString(KEY_USER_ID)!! == senderId) {
                            chatMessage.conversionImage = documentChange.document.getString(KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(KEY_RECEIVER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(KEY_RECEIVER_ID)
                            chatMessage.conversionStatus = documentChange.document.getBoolean(KEY_RECEIVER_STATUS)
                        } else {
                            chatMessage.conversionImage = documentChange.document.getString(KEY_SENDER_IMAGE)
                            chatMessage.conversionName = documentChange.document.getString(KEY_SENDER_NAME)
                            chatMessage.conversionId = documentChange.document.getString(KEY_SENDER_ID)
                            chatMessage.conversionStatus = documentChange.document.getBoolean(KEY_SENDER_STATUS)
                        }
                        chatMessage.lastMessage = documentChange.document.getString(KEY_LAST_MESSAGE)
                        conversations.add(chatMessage)
                        Log.d("listenerConversationWithUserId", chatMessage.lastMessage.toString())
                    }
                    DocumentChange.Type.MODIFIED -> {
                        val senderId = documentChange.document.getString(KEY_SENDER_ID)
                        val receiverId = documentChange.document.getString(KEY_RECEIVER_ID)
                        for (i in 0 until conversations.size) {
                            if (conversations[i].senderId == senderId && conversations[i].receiverId == receiverId) {
                                conversations[i].lastMessage = documentChange.document.getString(KEY_LAST_MESSAGE)
                                conversations[i].timestamp = documentChange.document.getLong(KEY_TIMESTAMP)
                                if (preferenceManager.getString(KEY_USER_ID)!! == senderId) {
                                    conversations[i].conversionImage = documentChange.document.getString(KEY_RECEIVER_IMAGE)
                                    conversations[i].conversionStatus = documentChange.document.getBoolean(KEY_RECEIVER_STATUS)

                                } else {
                                    conversations[i].conversionImage = documentChange.document.getString(KEY_SENDER_IMAGE)
                                    conversations[i].conversionStatus = documentChange.document.getBoolean(KEY_SENDER_STATUS)

                                }
                                break
                            }
                        }
                    }
                    else -> return@observe
                }
            }
            conversations.sortWith(compareBy { conversation -> conversation.timestamp })
            Log.d("listenerConversationWithUserId", conversations.toString())
            adapter.notifyDataSetChanged()
            binding.chatList.smoothScrollToPosition(0)
            binding.chatList.visibility = View.VISIBLE
            binding.progressBarLoadConversations.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}