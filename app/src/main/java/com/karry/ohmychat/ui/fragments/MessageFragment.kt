package com.karry.ohmychat.ui.fragments

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.karry.ohmychat.R
import com.karry.ohmychat.adapter.MessageAdapter
import com.karry.ohmychat.databinding.FragmentMessageBinding
import com.karry.ohmychat.model.Message
import com.karry.ohmychat.model.User
import com.karry.ohmychat.ui.fragments.ViewPaperFragment.Companion.toChats
import com.karry.ohmychat.utils.*
import com.karry.ohmychat.utils.Constants.KEY_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_IS_SEEN
import com.karry.ohmychat.utils.Constants.KEY_LAST_MESSAGE
import com.karry.ohmychat.utils.Constants.KEY_MESSAGE
import com.karry.ohmychat.utils.Constants.KEY_NAME
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_ID
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_NAME
import com.karry.ohmychat.utils.Constants.KEY_RECEIVER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_SENDER_ID
import com.karry.ohmychat.utils.Constants.KEY_SENDER_IMAGE
import com.karry.ohmychat.utils.Constants.KEY_SENDER_NAME
import com.karry.ohmychat.utils.Constants.KEY_SENDER_STATUS
import com.karry.ohmychat.utils.Constants.KEY_STATUS
import com.karry.ohmychat.utils.Constants.KEY_TIMESTAMP
import com.karry.ohmychat.utils.Constants.KEY_TYPE
import com.karry.ohmychat.utils.Constants.KEY_USER_ID
import com.karry.ohmychat.viewmodel.DatabaseViewModel

class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var rootView: View
    private lateinit var user: User
    private lateinit var messageList: ArrayList<Message>
    private lateinit var databaseViewModel: DatabaseViewModel
    private lateinit var adapter: MessageAdapter
    private val args: MessageFragmentArgs by navArgs()
    private lateinit var conversionId: String
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)

        init()
        listener()
        listenerMessage()



        return binding.root
    }

    private fun checkForConversion() {
        if (messageList.size != 0) {
            checkForConversionRemotely(preferenceManager.getString(KEY_USER_ID)!!, user.id)
            checkForConversionRemotely(user.id, preferenceManager.getString(KEY_USER_ID)!!)
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        databaseViewModel.checkForConversionRemotely(senderId, receiverId)
        databaseViewModel.documentSnapshot.observe(viewLifecycleOwner) { conversionId = it.id }
    }

    private fun init() {
        user = args.user
        toChats = true
        rootView = binding.messageLayout.rootView
        binding.messageName.text = user.name
        binding.messageProfileImage.setImageBitmap(convert(user.imageBase64))
        binding.chatMessageEditText.requestFocus()
        if (user.status) {
            binding.messageUserStatus.setBackgroundResource(R.drawable.online_status)
        } else {
            binding.messageUserStatus.setBackgroundResource(R.drawable.offline_status)
        }
        when (args.position) {
            2 -> onFromTabSearch()
            1 -> onFromTabChats()
        }

        databaseViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)).get(DatabaseViewModel::class.java)
        preferenceManager = PreferenceManager(requireContext())
        messageList = ArrayList()
        adapter = MessageAdapter(messageList, preferenceManager.getString(KEY_USER_ID)!!, args.position)
        binding.messagesRecord.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun listenerMessage() {
        databaseViewModel.eventListenerMessageSend(preferenceManager.getString(KEY_USER_ID)!!, user.id)
        databaseViewModel.messageList.observe(viewLifecycleOwner) { messAdd ->
            val count = messageList.size
            messageList.addAll(messAdd)
            messageList.sortWith(compareBy { it.timestamp })
            if (count == 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(messageList.size, messageList.size)
                binding.messagesRecord.smoothScrollToPosition(messageList.size - 1)
            }
            binding.progressBarLoadMessages.visibility = View.GONE
            binding.messagesRecord.visibility = View.VISIBLE
            if (!this::conversionId.isInitialized) {
                checkForConversion()
            }
        }

        databaseViewModel.eventListenerMessageSend(user.id, preferenceManager.getString(KEY_USER_ID)!!)
        databaseViewModel.messageList.observe(viewLifecycleOwner) { messAdd ->
            val count = messageList.size
            messageList.addAll(messAdd)
            messageList.sortWith(compareBy { it.timestamp })
            if (count == 0) {
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyItemRangeInserted(messageList.size, messageList.size)
                binding.messagesRecord.smoothScrollToPosition(messageList.size - 1)
            }
            binding.progressBarLoadMessages.visibility = View.GONE
            binding.messagesRecord.visibility = View.VISIBLE
            if (!this::conversionId.isInitialized) {
                checkForConversion()
            }
        }
    }

    private fun addConversion(conversion: HashMap<String, Any>) {
        databaseViewModel.addConversion(conversion)
        databaseViewModel.documentReference.observe(viewLifecycleOwner) { conversionId = it.id }
    }

    private fun updateConversion(message: String) {
        databaseViewModel.updateConversion(conversionId, message)
    }

    private fun listener() {
        with(binding) {
            val clickAnimation = AlphaAnimation(1f, 0.6f)
            buttonBackTo.setOnClickListener {
                it.startAnimation(clickAnimation)
                dismissKeyboard(requireActivity())
                requireActivity().onBackPressed()
            }

            databaseViewModel.listenerReceiver(user.id)
            databaseViewModel.user.observe(viewLifecycleOwner) {
                if (it.status) {
                    messageUserStatus.setBackgroundResource(R.drawable.online_status)
                } else {
                    messageUserStatus.setBackgroundResource(R.drawable.offline_status)
                }
                messageName.text = it.name
                messageProfileImage.setImageBitmap(convert(it.imageBase64))
            }

            sendMessage.setOnClickListener {
                it.startAnimation(clickAnimation)
                if (chatMessageEditText.text.isNotEmpty()) {
                    val timestamp = System.currentTimeMillis()
                    val senderId = preferenceManager.getString(KEY_USER_ID)!!
                    val receiverId = user.id
                    val message = chatMessageEditText.text.toString()
                    val chat = HashMap<String, Any>()

                    chat[KEY_SENDER_ID] = senderId
                    chat[KEY_RECEIVER_ID] = receiverId
                    chat[KEY_MESSAGE] = message
                    chat[KEY_IS_SEEN] = false
                    chat[KEY_TYPE] = "text"
                    chat[KEY_TIMESTAMP] = timestamp

                    databaseViewModel.sendMessage(chat)
                    chatMessageEditText.setText("")
                    databaseViewModel.isUpdated.observe(viewLifecycleOwner) { result ->
                        if (result) {
                            if (this@MessageFragment::conversionId.isInitialized) {
                                updateConversion(message)
                            } else {
                                val conversion = HashMap<String, Any>()
                                conversion[KEY_SENDER_ID] = preferenceManager.getString(KEY_USER_ID)!!
                                conversion[KEY_RECEIVER_ID] = user.id
                                conversion[KEY_SENDER_NAME] = preferenceManager.getString(KEY_NAME)!!
                                conversion[KEY_RECEIVER_NAME] = user.name
                                conversion[KEY_SENDER_IMAGE] = preferenceManager.getString(KEY_IMAGE)!!
                                conversion[KEY_RECEIVER_IMAGE] = user.imageBase64
                                conversion[KEY_SENDER_STATUS] = preferenceManager.getBoolean(KEY_STATUS)
                                conversion[KEY_RECEIVER_STATUS] = user.status
                                conversion[KEY_LAST_MESSAGE] = message
                                conversion[KEY_TIMESTAMP] = timestamp
                                addConversion(conversion)
                            }
                        } else {
                            showToast(requireContext(), "Can't send message")
                        }
                    }
                }
            }
        }
    }

    private fun onFromTabChats() {
        with(binding) {
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.chats_100))
            setStatusBarColor(requireActivity(), getColorResource(requireActivity(), R.color.chats_500))
            toolbar.setBackgroundColor(getColorResource(requireActivity(), R.color.chats_500))
            setIconColor(requireActivity(), R.drawable.ic_back, buttonBackTo, R.color.chats_100)
            setIconColor(requireActivity(), R.drawable.ic_info, buttonShowProfile, R.color.chats_100)
            setIconColor(requireActivity(), R.drawable.ic_send, sendMessage, R.color.chats_900)
            setIconColor(requireActivity(), R.drawable.ic_attach, attachFile, R.color.chats_900)
            setBackgroundColor(sendLayout.background!!, getColorResource(requireActivity(), R.color.chats_200))
            setBackgroundColor(chatMessageEditText.background!!, getColorResource(requireActivity(), R.color.chats_200))
            progressBarLoadMessages.indeterminateDrawable.setColorFilter(getColorResource(requireContext(), R.color.chats_700), PorterDuff.Mode.SRC_IN)
            messageName.setTextColor(getColorResource(requireActivity(), R.color.search_100))
            chatMessageEditText.setTextColor(getColorResource(requireActivity(), R.color.search_900))
            chatMessageEditText.setHintTextColor(getColorResource(requireActivity(), R.color.search_300))
        }
    }

    private fun onFromTabSearch() {
        with(binding) {
            rootView.setBackgroundColor(getColorResource(requireActivity(), R.color.search_100))
            setStatusBarColor(requireActivity(), getColorResource(requireActivity(), R.color.search_500))
            toolbar.setBackgroundColor(getColorResource(requireActivity(), R.color.search_500))
            setIconColor(requireActivity(), R.drawable.ic_back, buttonBackTo, R.color.search_100)
            setIconColor(requireActivity(), R.drawable.ic_info, buttonShowProfile, R.color.search_100)
            setIconColor(requireActivity(), R.drawable.ic_send, sendMessage, R.color.search_900)
            setIconColor(requireActivity(), R.drawable.ic_attach, attachFile, R.color.search_900)
            setBackgroundColor(sendLayout.background!!, getColorResource(requireActivity(), R.color.search_200))
            setBackgroundColor(chatMessageEditText.background!!, getColorResource(requireActivity(), R.color.search_200))
            progressBarLoadMessages.indeterminateDrawable.setColorFilter(getColorResource(requireContext(), R.color.search_700), PorterDuff.Mode.SRC_IN)
            messageName.setTextColor(getColorResource(requireActivity(), R.color.search_100))
            chatMessageEditText.setTextColor(getColorResource(requireActivity(), R.color.search_900))
            chatMessageEditText.setHintTextColor(getColorResource(requireActivity(), R.color.search_300))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}