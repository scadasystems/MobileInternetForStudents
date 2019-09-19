package com.example.mobileinternetforstudent.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mobileinternetforstudent.R
import com.example.mobileinternetforstudent.exampleFirebase.FirebaseGalleryFragment
import com.example.mobileinternetforstudent.exampleFirebase.FirebaseHomeFragment
import com.example.mobileinternetforstudent.exampleFirebase.FirebaseUploadFragment
import kotlinx.android.synthetic.main.fragment_firebase.*

/**
 * A simple [Fragment] subclass.
 */
class FirebaseFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.firebaseMainFragment, FirebaseHomeFragment())
        transaction.commitNow()

        bottom_nav.setOnNavigationItemSelectedListener {
            it.isChecked = false
            when(it.itemId) {
                R.id.bottomNavFragmentHome -> {
                    setFragment(FirebaseHomeFragment())
                    true
                }
                R.id.bottomNavFragmentGallery -> {
                    setFragment(FirebaseGalleryFragment())
                    true
                }
                R.id.bottomNavFragmentUpload -> {
                    setFragment(FirebaseUploadFragment())
                    true
                }
            }
            false
        }
    }

    private fun setFragment(child: Fragment) {
        val transaction = childFragmentManager.beginTransaction();
        if (!child.isAdded) {
            transaction.replace(R.id.firebaseMainFragment, child)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


}
