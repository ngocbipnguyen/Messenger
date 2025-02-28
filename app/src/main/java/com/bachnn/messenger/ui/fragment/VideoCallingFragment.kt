package com.bachnn.messenger.ui.fragment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.bachnn.messenger.R
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.data.model.User
import com.bachnn.messenger.databinding.VideoCallingFragmentBinding
import com.bachnn.messenger.ui.service.IVideoCalling
import com.bachnn.messenger.ui.service.VideoCallingService
import com.bachnn.messenger.ui.viewModel.VideoCallingViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas

@AndroidEntryPoint
class VideoCallingFragment: BaseFragment<VideoCallingViewModel, VideoCallingFragmentBinding>() {

    private var userTo: User? = null

    private lateinit var mService: VideoCallingService
    private var mBound: Boolean = false

    private var windowManager: WindowManager? = null
    private var screenWidth: Int = -1
    private var screenHeight: Int = -1

    private var windowRoot: View? = null

    private var mRemoteView: FrameLayout? = null

    private val videoCalling = object : IVideoCalling {

        override fun setLocalView(rtcEngine: RtcEngine) {
            // run on window manager.
            mRemoteView.let {
                val surfaceView = SurfaceView(requireContext())
                mRemoteView?.addView(surfaceView)
                rtcEngine.setupLocalVideo(VideoCanvas(surfaceView,VideoCanvas.RENDER_MODE_FIT, 0))
            }
        }

        override fun setRemoteView(rtcEngine: RtcEngine) {
            val surfaceView = SurfaceView(requireContext()).apply {
                setZOrderMediaOverlay(true)
            }
            binding.videoCallingFrame.addView(surfaceView)
            rtcEngine.setupLocalVideo(VideoCanvas(surfaceView,VideoCanvas.RENDER_MODE_FIT, 0))
        }

    }


    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as VideoCallingService.LocalBinder
            mService = binder.getService()
            mService.setIVideoCalling(videoCalling)
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }



    override fun createViewModel(): VideoCallingViewModel {
        return ViewModelProvider(this)[VideoCallingViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VideoCallingFragmentBinding {
        return VideoCallingFragmentBinding.inflate(inflater,container,false)
    }

    override fun initView() {
        userTo = VideoCallingFragmentArgs.fromBundle(requireArguments()).userArg!!

        userTo.let {
            binding.videoCallingFrame.setBackgroundColor(generateColorFromUsername(it!!.name))
            binding.usernameUserTo.text = it.name
            Glide.with(this).load(it.photoUrl).into(binding.userToImage)
        }

        // Bind to LocalService.
        Intent(requireContext(), VideoCallingService::class.java).also { intent ->
            requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

    }

    private fun generateColorFromUsername(username: String): Int {
        val hash = username.hashCode()
        val r = (hash shr 16) and 0xFF
        val g = (hash shr 8) and 0xFF
        val b = hash and 0xFF
        return Color.rgb(r, g, b)
    }


    fun showWinDown() {
        windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        windowRoot = LayoutInflater.from(requireContext()).inflate(R.layout.window_video, null)

        val layoutParam = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        windowManager?.addView(windowRoot, layoutParam)
    }

}