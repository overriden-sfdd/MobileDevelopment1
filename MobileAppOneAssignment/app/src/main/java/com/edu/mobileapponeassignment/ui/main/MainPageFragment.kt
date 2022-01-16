package com.edu.mobileapponeassignment.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.mobileapponeassignment.databinding.FragmentMainPageBinding
import com.edu.mobileapponeassignment.ui.main.recyclerview.AppPage
import com.edu.mobileapponeassignment.ui.main.recyclerview.AppPageAdapter
import com.edu.mobileapponeassignment.ui.main.viewmodels.MainViewModel
import com.edu.mobileapponeassignment.ui.main.viewmodels.MainViewModelFactory
import java.io.File
import java.io.IOException


class MainPageFragment : Fragment() {
    private var currentPhotoPath: String = ""
    lateinit var backgroundBitmap: BitmapDrawable
    private var _binding: FragmentMainPageBinding? = null
    private lateinit var mainViewModel: MainViewModel


    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)

        // Lookup the recyclerview in activity layout
        val rvAppPages = binding.mainPageRecyclerView

        if (mainViewModel.imageBitmap.value != null) {
            backgroundBitmap = mainViewModel.imageBitmap.value!!
            view.background = backgroundBitmap
        }

        // Initialize activities
        val appPages: List<AppPage> = listOf(
            AppPage("Set background", true),
            AppPage("Locate me", true),
            AppPage("Tell a joke", true),
            AppPage("Add item"), AppPage("Transfer"),
            AppPage("Pick"), AppPage("Ship")
        )

        // Create adapter passing in the sample user data
        val adapter = AppPageAdapter(appPages) { appPage ->
            when (appPage?.pageName) {
                "Set background" -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        )
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            13
                        )
                    }

                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.CAMERA
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        dispatchTakePictureIntent()
                    }
                }
                "Locate me" -> {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val directions = MainPageFragmentDirections.actionMainPageFragmentToGoogleMapFragment()
                        findNavController().navigate(directions)
                    } else {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ), 44
                        )
                    }
                }
                "Tell a joke" -> {
                    Toast.makeText(requireContext(), "Imaging wasting whole weekend on this app, right?", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
        // Attach the adapter to the recyclerview to populate items
        rvAppPages.adapter = adapter
        // Set layout manager to position the items
        rvAppPages.layoutManager = LinearLayoutManager(context)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // small boiler-plate code here
        if (requestCode == 44 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val directions = MainPageFragmentDirections.actionMainPageFragmentToGoogleMapFragment()
            findNavController().navigate(directions)
        } else if (requestCode == 13 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // only one activity starts in this fragment, no check needed
        val uri = FileProvider.getUriForFile(
            requireActivity(),
            "com.edu.mobileapponeassignment.provider",
            File(currentPhotoPath)
        )

        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        backgroundBitmap = BitmapDrawable(requireContext().resources, bitmap)
//        mainViewModel.setImageBitmap(backgroundBitmap)
        view?.background = backgroundBitmap
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (this::backgroundBitmap.isInitialized) {
            outState.putParcelable("BackgroundBitmap", backgroundBitmap.bitmap)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val bitmapParcelled: Bitmap? = savedInstanceState?.getParcelable("BackgroundBitmap")
        backgroundBitmap = BitmapDrawable(requireContext().resources, bitmapParcelled)
        view?.background = backgroundBitmap
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val storageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "regular", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        if (context == null) {
            throw RuntimeException("Context is not presented in MainPagesFragment")
        }

        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager).also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.edu.mobileapponeassignment.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, 123)
                }
            }
        }
    }
}