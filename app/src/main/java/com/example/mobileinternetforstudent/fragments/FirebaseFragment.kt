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
        // FragmentTransaction 사용
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.firebaseMainFragment, FirebaseHomeFragment())  // 미리 add하여 home을 보여준다.
        transaction.commitNow() // 바로 적용

        // bottomNavigation 에 대한 리스너
        bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.bottomNavFragmentHome -> {
                    setFragment(FirebaseHomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottomNavFragmentGallery -> {
                    setFragment(FirebaseGalleryFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bottomNavFragmentUpload -> {
                    setFragment(FirebaseUploadFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    // 일일이 집어넣는것보다 fragment 이름을 활용하여 함수를 만들자. 편한방법 사용하기~
    private fun setFragment(child: Fragment) {
        this.childFragmentManager.beginTransaction()
                // firebase 메인 프래그레이아웃을 이용하여 해당 프래그먼트를 대체한다.
                .replace(R.id.firebaseMainFragment, child)
/*
                .addToBackStack(null)
                back버튼 누를 때마다 이전 프레그먼트로 순차적으로 이동할 때 사용. 스택이 쌓인다고 생각하면 됨.
*/
                .commit()
    }
}

