package com.example.mobileinternetforstudent.exampleRealtime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileinternetforstudent.R
import com.example.mobileinternetforstudent.toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_realtime.*

class RealtimeActivity : AppCompatActivity() {

    private lateinit var mRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtime)

        mRef = FirebaseDatabase.getInstance().reference.child("data").child("texts")
        rv_firebase.layoutManager = LinearLayoutManager(this)

        firebaseData()
    }

    private fun firebaseData() {
        ///////////////////////////////////////////////////////////////////////////
        // 파이어베이스 리사이클러뷰의 경우는 options 즉 쿼리문으로 데이터베이스에서 값을 자동으로 불러와주기 때문에
        // 따로 ArrayList를 사용하지 않는다.
        ///////////////////////////////////////////////////////////////////////////
        val option = FirebaseRecyclerOptions.Builder<Model>()
                // option는 파이어베이스 리사이클러뷰에 DB의 쿼리문 옵션을 넣어 해당 쿼리문에
                // 맞는 데이터들을 자동 세팅해주기 위해서 사용한다.
                .setQuery(mRef, Model::class.java)      // reference와 모델을 쿼리에 셋팅함.
                .setLifecycleOwner(this)                        // firebase 와 databinidng 을 사용할 수 있게 함.
                .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, MyViewHolder>(option) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                // item_databinding_post.xml 과 연결
                val itemView = LayoutInflater.from(this@RealtimeActivity).inflate(R.layout.item_databinding_post, parent, false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Model) {
                val placeid = getRef(position).key.toString()

                mRef.child(placeid).addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        toast("Error : " + error.toException())
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // 프로그래스바가 아이템이 0이면 보여주고 아니면 숨김
                        progress_bar.visibility = if (itemCount == 0) View.VISIBLE else View.GONE
                        holder.txt.setText(model.text)      // MyViewHolder 에 있는 txt 에 데이터를 넣음
                    }
                })
            }
        }

        rv_firebase.adapter = firebaseRecyclerAdapter   // 리사이클러뷰에 현재 만든 어댑터를 연결한다
        firebaseRecyclerAdapter.startListening()
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        internal var txt = itemView!!.findViewById<AppCompatTextView>(R.id.display_text)
    }

}
