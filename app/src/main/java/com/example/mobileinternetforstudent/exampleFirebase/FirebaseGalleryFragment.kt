package com.example.mobileinternetforstudent.exampleFirebase


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mobileinternetforstudent.R

/**
 * A simple [Fragment] subclass.
 */
class FirebaseGalleryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_gallery, container, false)
    }


}
