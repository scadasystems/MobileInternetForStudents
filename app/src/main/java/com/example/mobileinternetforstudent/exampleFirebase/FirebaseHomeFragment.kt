package com.example.mobileinternetforstudent.exampleFirebase


import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mobileinternetforstudent.R
import kotlinx.android.synthetic.main.fragment_firebase_home.*

/**
 * A simple [Fragment] subclass.
 */
class FirebaseHomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lottie_firebase_home.setAnimation("ani_upload.json")
        lottie_firebase_home.loop(true)
        lottie_firebase_home.playAnimation()
        lottie_firebase_home.addAnimatorUpdateListener {
            object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }

            }


        }
    }

}
