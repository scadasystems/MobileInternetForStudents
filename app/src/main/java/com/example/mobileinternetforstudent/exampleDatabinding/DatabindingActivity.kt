package com.example.mobileinternetforstudent.exampleDatabinding

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileinternetforstudent.BR
import com.example.mobileinternetforstudent.MainActivity
import com.example.mobileinternetforstudent.R
import com.example.mobileinternetforstudent.databinding.ActivityDatabindingBinding
import com.example.mobileinternetforstudent.databinding.ItemDatabindingPostBinding
import com.example.mobileinternetforstudent.toast
import kotlinx.android.synthetic.main.activity_databinding.*

class DatabindingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatabindingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_databinding)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_databinding)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)  // toolbar에 뒤로가기 버튼 생성
        getSupportActionBar()!!.setTitle("Databinding 예제")

        // Data.kt에 데이터를 설정
        val origin_text = Data("데이터바인딩 기초")
        binding.dataVar = origin_text

        // 실시간 텍스트 변경
        edt_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {       // 입력한 문자열이 비어있으면
                    binding.setVariable(BR.dataVar, origin_text)    // Data.kt 안의 text 를 데이터바인딩 기초로 나타냄.
                } else {    // 입력한 문자열이 있다면
                    binding.setVariable(BR.dataVar, Data(s.toString()))     // 그 문자열을 받아 데이터로 넣는다.
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        val text_list = arrayListOf<Data>()

        // 버튼이벤트
        btn_send.setOnClickListener {
            val textData = edt_text.text.toString()     // EditText의 문자열을 변수에 넣는다.
            if (textData.isNotEmpty()) {        // 문자열의 값이 있으면
                text_list.add(Data(textData))   // arrayList에 넣는다.
//                rv_databinding.adapter?.notifyDataSetChanged()        // notifyDataSetChanged 은 새로고침이 있다. 없으려면 아래처럼.
                rv_databinding.adapter?.notifyItemInserted(text_list.size)      // 넣은 아이템을 리스트 마지막에 넣으면 보여준다.
            } else {        // 문자열의 값이 없으면
                toast("텍스트를 입력하세요.")
            }
        }

        // 리사이클러뷰 연결
        rv_databinding.apply {
            layoutManager = LinearLayoutManager(this@DatabindingActivity)
            adapter = MyAdapter(text_list) { text_list ->
                toast("$text_list")
            }
        }
    }

    /*  toolbar 뒤로가기 버튼 누를 시 메인으로 이동 */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent_home = Intent(this, MainActivity::class.java)
                startActivity(intent_home)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

// 리사이클러뷰 어댑터
class MyAdapter(val items: List<Data>, private val clickListener: (text: Data) -> Unit) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val binding: ItemDatabindingPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_databinding_post, parent, false)

        val viewHolder = MyViewHolder(ItemDatabindingPostBinding.bind(view))

        view.setOnClickListener {
            clickListener.invoke(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.textData = items[position]
    }
}
