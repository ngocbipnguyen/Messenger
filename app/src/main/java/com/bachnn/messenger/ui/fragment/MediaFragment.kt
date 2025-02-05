package com.bachnn.messenger.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bachnn.messenger.base.BaseFragment
import com.bachnn.messenger.constants.Constants
import com.bachnn.messenger.data.model.Media
import com.bachnn.messenger.databinding.MediaFragmentBinding
import com.bachnn.messenger.ui.adapter.MediaAdapter
import com.bachnn.messenger.ui.viewModel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaFragment : BaseFragment<MediaViewModel, MediaFragmentBinding>() {

    lateinit var mediaAdapter: MediaAdapter

    val medias = ArrayList<Media>()

    override fun createViewModel(): MediaViewModel {
        return ViewModelProvider(this)[MediaViewModel::class]
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MediaFragmentBinding {
        return MediaFragmentBinding.inflate(inflater, container, false)
    }


    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val isGranted: Boolean =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    results[Manifest.permission.READ_MEDIA_IMAGES] ?: false ||
                            results[Manifest.permission.READ_MEDIA_VIDEO] ?: false ||
                            results[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] ?: false
                } else {
                    results[Manifest.permission.READ_MEDIA_IMAGES] ?: false ||
                    results[Manifest.permission.READ_MEDIA_VIDEO] ?: false
                }

            if (isGranted) {
                if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ) {
                    binding.allowAccessLayout.visibility = View.GONE
                }
                viewModel.getAllMedia(requireContext().contentResolver)
            }
        }

    override fun initView() {
        if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ) {
            binding.allowAccessLayout.visibility = View.GONE
        }

        mediaAdapter = MediaAdapter(medias, onClickMedia = {
            // todo back message screen
            setFragmentResult(
                Constants.REQUEST_MEDIA,
                bundleOf(
                    Constants.MEDIA_URI to it.uri.toString()
                )
            )
            binding.root.findNavController().popBackStack()

        })

        binding.mediaRecyclerview.adapter = mediaAdapter

        viewModel.medias.observe(this, Observer {
            if (!isDetached) {
                medias.clear()
                medias.addAll(it)
                mediaAdapter.notifyDataSetChanged()
            }
        })

        viewModel.getAllMedia(requireContext().contentResolver)

        binding.allowLimitButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            }
        }

    }
}