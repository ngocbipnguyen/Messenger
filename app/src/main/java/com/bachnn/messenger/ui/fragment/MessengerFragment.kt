package com.bachnn.messenger.ui.fragment

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.constants.Constants
import com.bachnn.messenger.data.model.Message
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.MessengerFragmentBinding
import com.bachnn.messenger.ui.adapter.MessageAdapter
import com.bachnn.messenger.ui.viewModel.MessengerViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class MessengerFragment : BaseFragment<MessengerViewModel, MessengerFragmentBinding>() {

    lateinit var userTo: User

    lateinit var adapter: MessageAdapter

    var isSendingVisibility: Boolean = false

    var messages = ArrayList<Message>()

    private lateinit var uriImage: Uri
    private lateinit var pathFile: File
    private lateinit var dateCamera: Date

    val openCameraIntent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            // save image in cloud and delete file image save in cache.
            lifecycleScope.launch {
                uploadImages(uriImage)
            }
        }
    }


    override fun createViewModel(): MessengerViewModel {
        return ViewModelProvider(this)[MessengerViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MessengerFragmentBinding {
        return MessengerFragmentBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        userTo = MessengerFragmentArgs.fromBundle(requireArguments()).userArg!!

        binding.messengerToolbar.title = userTo.name

        adapter = MessageAdapter(messages, userTo)

        binding.messengerRecycler.adapter = adapter


        viewModel.messages.observe(this, Observer { it ->
            if (it != null) {
                messages.clear()
                messages.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.setIdMessage(userTo.uid)
        viewModel.setListenerMessage()

        viewModel.isVisibilitySending.observe(this, Observer { it ->
            if (it) {
                setVisibleSending(
                    binding.messengerRecycler,
                    binding.messengerRecycler.layoutManager as LinearLayoutManager,
                    View.VISIBLE
                )
            } else {
                setVisibleSending(
                    binding.messengerRecycler,
                    binding.messengerRecycler.layoutManager as LinearLayoutManager,
                    View.GONE
                )
            }
        })

        binding.sendIcon.setOnClickListener {
            val messageText = binding.messageEdit.text.toString().trim()
            if (messageText != "") {
                val datetime = Date()
                val timestamps: String = datetime.time.toString()
                binding.messageEdit.text.clear()
                messages.add(0,
                    Message(
                        viewModel.auth.currentUser?.uid!!,
                        userTo.uid,
                        timestamps,
                        messageText,
                        Constants.TYPE_TEXT
                    )
                )
                adapter.notifyItemInserted(0)
                viewModel.setVisibilitySending(true)
                viewModel.sendMessage(
                    userTo.uid,
                    messageText,
                    Constants.TYPE_TEXT,
                    timestamps,
                    userTo.token
                )
            }
        }

        binding.cameraIcon.setOnClickListener {
            //todo open camera.
            openCamera()
        }

    }

    private fun setVisibleSending(
        recyclerView: RecyclerView,
        linearLayoutManager: LinearLayoutManager,
        visibility: Int
    ) {
        val firstVisibilityItem = linearLayoutManager.findFirstVisibleItemPosition()
        val holder = recyclerView.findViewHolderForAdapterPosition(firstVisibilityItem)

        if (holder is MessageAdapter.RightHolder) {
            val rightHolder: MessageAdapter.RightHolder = holder
            rightHolder.sendingText.visibility = visibility
            isSendingVisibility = visibility == View.VISIBLE
        }
    }

    private fun openCamera() {
        dateCamera = Date()
        val formatDate = SimpleDateFormat("yyyy-MM-dd:HH:mm:ss")
        val strDate = formatDate.format(dateCamera)

        val pathFile = File(
            requireContext().cacheDir,
            "${requireContext().getString(R.string.app_name)}_$strDate.jpg"
        )
        this.pathFile = pathFile
        uriImage = FileProvider.getUriForFile(requireContext(), "com.bachnn.messenger", pathFile)
        openCameraIntent.launch(uriImage)
    }

    private suspend fun uploadImages(uriImage: Uri) {
        val timestamps = dateCamera.time.toString()
        val folderMedia = "${viewModel.group}/$timestamps"
        FirebaseStorage.getInstance().reference.child(folderMedia).putFile(uriImage).await()
        val url = FirebaseStorage.getInstance().reference.child(folderMedia).downloadUrl.await()

        Log.e("uploadImages", "url : $url")
        viewModel.sendMessage(userTo.uid, uriImage.toString(), Constants.TYPE_IMAGE, timestamps, userTo.token)

    }

}