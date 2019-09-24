package com.example.mobileinternetforstudent.exampleFirebase


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileinternetforstudent.R
import com.example.mobileinternetforstudent.databinding.ItemFirebasePostBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_firebase_gallery.*

/**
 * A simple [Fragment] subclass.
 */
class FirebaseGalleryFragment : Fragment() {
    // RecyclerView 어댑터 생성
    private lateinit var adapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firestore 에 대한 쿼리를 만듭니다. Firestore은 NoSQL 입니다.
        val query = FirebaseFirestore.getInstance()
                .collection("posts")    // 이미지를 업로드할때 posts 라는 collection을 만들었는데 이곳을 참조한다.
                .orderBy("time_stamp", Query.Direction.ASCENDING)   // 이미지를 보여줄때 시간순으로 보여준다.

        // FirestoreRecyclerOptions 을 빌드함
        val options = FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post::class.java)  // 앞서 만든 query을 사용하여 셋팅.
                .build()    // 빌드

        adapter = PostAdapter(options)      // adapter 에 PostAdapter 연결

        recycler_view_firebase.adapter = adapter     // 최종적으로 adapter를 recyclerView에 연결
    }

    // 갤러리 프래그먼트 시작할때 어댑터를 시작한다.
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    // 종료될 때 어댑터를 정지한다.
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    class PostAdapter(
            options: FirestoreRecyclerOptions<Post>     // FirebaseUI 를 사용한 firestoreRecyclerOptions
    ) : FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_firebase_post, parent, false)
            return PostHolder(ItemFirebasePostBinding.bind(view))
        }

        override fun onBindViewHolder(p0: PostHolder, p1: Int, p2: Post) {
            p0.binding.postFirebase = p2
        }

        // ItemFirebasePostBinding -> 만들었던 item_firebase_post.xml 을 이용하여 스튜디오가 자동으로 Binding 파일을 만듬.
        class PostHolder(val binding: ItemFirebasePostBinding) : RecyclerView.ViewHolder(binding.root)
    }


}
