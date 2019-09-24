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
        // json 애니메이션을 셋팅한다.
        lottie_firebase_home.setAnimation("ani_upload.json")
        // 반복하기
        lottie_firebase_home.loop(true)
        // 애니메이션 재생하기
        lottie_firebase_home.playAnimation()
        // 애니메이션에 대한 업데이트 리스너
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
