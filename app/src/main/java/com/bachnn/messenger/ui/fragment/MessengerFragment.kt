package com.bachnn.messenger.ui.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.constants.Constants
import com.bachnn.messenger.data.model.Message
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.MessengerFragmentBinding
import com.bachnn.messenger.ui.adapter.MessageAdapter
import com.bachnn.messenger.ui.view.Emoticon
import com.bachnn.messenger.ui.view.EmoticonLikeTouchDetector
import com.bachnn.messenger.ui.view.OnEmoticonSelectedListener
import com.bachnn.messenger.ui.view.custom.EmoticonGroupView
import com.bachnn.messenger.ui.view.custom.InitEmoticonConfig
import com.bachnn.messenger.ui.viewModel.MessengerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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

    private lateinit var emoticonLikeTouchDetector: EmoticonLikeTouchDetector


    // Register ActivityResult handler
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val isGranted: Boolean =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                results[Manifest.permission.READ_MEDIA_IMAGES] ?: false ||
                results[Manifest.permission.READ_MEDIA_VIDEO] ?: false ||
                        results[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] ?: false
            } else {
                results[Manifest.permission.READ_MEDIA_IMAGES] ?: false
                results[Manifest.permission.READ_MEDIA_VIDEO] ?: false
            }

            if (isGranted) {
                /*todo: open show list photo and video*/
                val action = MessengerFragmentDirections.actionMessengerFragmentToMediaFragment()
                binding.root.findNavController().navigate(action)

            } else {
                //todo show dialog ask media permission.
                val dialog = AlertDialog.Builder(requireContext())
                    .setMessage(requireContext().getString(R.string.open_setting_permission))
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        val intents = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", requireContext().packageName, null)
                        intents.data = uri
                        startActivity(intents)
                        dialog.cancel()
                    }
                dialog.show()
            }
        }

    private val openCameraIntent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            // save image in cloud and delete file image save in cache.
            lifecycleScope.launch {
                uploadImages(uriImage)
            }
        }
    }

    private val getGalleryIntent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        lifecycleScope.launch {
            it?.let {
                dateCamera = Date()
                uploadImages(it)
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

        emoticonLikeTouchDetector = EmoticonLikeTouchDetector()
        setFragmentResultListener(Constants.REQUEST_MEDIA) { _, bundle ->
            val uri = bundle.getString(Constants.MEDIA_URI)
            if (uri != null) {
                lifecycleScope.launch {
                    dateCamera = Date()
                    uploadImages(uri.toUri())
                }
            }
        }

        binding.messengerToolbar.title = userTo.name

        adapter = MessageAdapter(messages, userTo, emoticonLongClick = {view, position ->
            reactEmoticonIcon(binding.messengerRecycler, binding.messengerRecycler.layoutManager as LinearLayoutManager, position, view)
        })

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

        binding.photoIcon.setOnClickListener {
            //todo : select photo and video in gallery.
            requestPermissionGallery()
        }

//        PushNotification.showNotification(requireContext(), "bachnn","hello world!", User("rMqi1TSgInX1kTEFwnvyaf0h4bs2","","","","",""))

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
//        val folderMedia = "${viewModel.group}/$timestamps"
//        FirebaseStorage.getInstance().reference.child(folderMedia).putFile(uriImage).await()
//        val url = FirebaseStorage.getInstance().reference.child(folderMedia).downloadUrl.await()
//
//        Log.e("uploadImages", "url : $url")
        viewModel.sendMessage(userTo.uid, uriImage.toString(), Constants.TYPE_IMAGE, timestamps, userTo.token)

    }

    /*todo gallery*/
    private fun requestPermissionGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        } else {
            getGalleryIntent.launch("video/*, image/*")
        }
    }


    private fun reactEmoticonIcon(recyclerView: RecyclerView, layoutManager: LinearLayoutManager, position: Int, rootView: View) {
        val popupHeight: Int = 64
        val holder = recyclerView.findViewHolderForAdapterPosition(position)

        if (holder is MessageAdapter.LeftHolder) {
            val leftHolder: MessageAdapter.LeftHolder = holder
            var yPopupWindow: Int = 0

            if ((leftHolder.view.y.toInt() + recyclerView.y.toInt() - popupHeight) < recyclerView.y.toInt()) {
                yPopupWindow = leftHolder.view.y.toInt() + recyclerView.y.toInt() + popupHeight + leftHolder.view.height + 8
            } else {
                yPopupWindow = leftHolder.view.y.toInt() + recyclerView.y.toInt() - popupHeight
            }


            val reactEmoticonView: View = LayoutInflater.from(requireContext()).inflate(R.layout.emoticon_popup, null)

            val emoticonPopup = PopupWindow(
                reactEmoticonView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )


            Log.e("reactEmoticonIcon", "wh out :  ${reactEmoticonView.width}/${reactEmoticonView.height}")

            emoticonPopup.isOutsideTouchable = true
            emoticonPopup.isFocusable = true
            emoticonPopup.showAsDropDown(rootView, 0, yPopupWindow, Gravity.CENTER)


            val emoticonGroupView: EmoticonGroupView = reactEmoticonView.findViewById(R.id.emotion_view)

            val initEmoticonConfig: InitEmoticonConfig = InitEmoticonConfig.with(requireContext())
            initEmoticonConfig.on(leftHolder.view)
                .open(emoticonGroupView)
                .addEmoticon(Emoticon(R.drawable.like, "Like"))
                .addEmoticon(Emoticon(R.drawable.haha, "Haha"))
                .addEmoticon(Emoticon(R.drawable.p, "p"))
                .addEmoticon(Emoticon(R.drawable.sad, "sad"))
                .addEmoticon(Emoticon(R.drawable.kiss, "kiss"))
                .setOnEmojiSelectedListener(object : OnEmoticonSelectedListener{
                    override fun onEmoticonSelected(emoticon: Emoticon) {
                        Log.e("onEmoticonSelected", emoticon.description)
                        emoticonPopup.dismiss()

                    }
                }).setup()

            emoticonLikeTouchDetector.configure(initEmoticonConfig)

            reactEmoticonView.setOnTouchListener(View.OnTouchListener { _, event ->
                Log.e("reactEmoticonIcon", "raw :${event.rawX}/${event.rawY}")
                emoticonLikeTouchDetector.dispatchTouchEvent(event)
                true
            })

//
            reactEmoticonView.viewTreeObserver.addOnGlobalLayoutListener {
                val width = reactEmoticonView.width
                val height = reactEmoticonView.height
                Log.e("reactEmoticonIcon", "wh in :  $width/$height")
            }

        }

    }

}