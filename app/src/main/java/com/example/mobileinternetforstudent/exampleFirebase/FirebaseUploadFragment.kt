package com.example.mobileinternetforstudent.exampleFirebase


import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mobileinternetforstudent.R
import com.example.mobileinternetforstudent.toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_firebase_upload.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
@TargetApi(Build.VERSION_CODES.O)
class FirebaseUploadFragment : Fragment() {

    // 이미지 파일에 대한 경로 정의
    private var image_filePath: Uri? = null

    // firebaseStorage 에 대해 정의 및 스토리지 리퍼런스
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    // 파일을 업로드할 시 파일명을 현재 날짜로 만들기 위함.
    private val current_time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // lottie 셋팅
        img_firebase_pic.setAnimation("ani_image_loading.json")
        img_firebase_pic.loop(true)
        img_firebase_pic.playAnimation()

        // 이미지 선택하기 이벤트
        btn_choose.setOnClickListener {
            val intent_image = Intent()
            intent_image.setType("image/*")     // image 타입
            intent_image.setAction(Intent.ACTION_GET_CONTENT)       // intent로 갤러리로 들어가게 한다.
            // 이미지를 클릭하면 onActivityResult 를 이용한다.
            startActivityForResult(Intent.createChooser(intent_image, "이미지를 선택하세요."), 0)
        }

        // 이미지 업로드 이벤트
        btn_upload.setOnClickListener {
            uploadFile()
        }
    }

    private fun uploadFile() {
        // 스토리지를 사용하기 위해 인스턴스를 가져온다.
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // 이미지 경로가 null 이 아닌경우
        if (image_filePath != null) {
            showProgress()
            // storage에 넣을 이미지 이름은 현재 시각으로. 또한 uploads 라는 경로 안에 넣기 위함.
            val ref = storageReference?.child("uploads/$current_time")
            val uploadTask = ref?.putFile(image_filePath!!)
            // Task 활용
            uploadTask?.continueWithTask(
                    Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                        if (!it.isSuccessful) {
                            hideProgress()
                            it.exception?.let { throw it }
                        }
                        return@Continuation ref.downloadUrl
                    }
            )?.addOnCompleteListener {  // 이미지 업로드에 대한 완료 리스너
                if (it.isSuccessful) {      // 성공일 때
                    val downloadUri = it.result
                    hideProgress()
                    addUploadRecord(downloadUri.toString(), current_time.toString())
                } else {    // 실패할 때
                    hideProgress()
                    toast("업로드를 실패했습니다.")
                }
            }?.addOnFailureListener { }
        } else {
            toast("파일을 먼저 선택하세요.")
        }
    }

    // 업로드할 때, storage에는 이미지를 저장하고 그 주소를 firestore에 저장한다.
    private fun addUploadRecord(uri: String, time: String) {
        val db = FirebaseFirestore.getInstance()
        // firestore 에 대해 HashMap을 만든다.
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri               // firestorage에 있는 imageUrl 을 가져오기 위한 배열
        data["time_stamp"] = time       // time_stamp 를 활용하여 언제 이미지를 올렸는지 확인 가능.
        db.collection("posts")      // posts 라는 컬렉션을 만든다.
                .add(data)                                              // 그 안에 위에 데이터 배열로 만든 imageUrl 과 time_stamp를 추가한다.
                .addOnSuccessListener {                     // 성공적으로 업로드 되었을 때
                    toast("업로드가 완료되었습니다.")
                }.addOnFailureListener {}
    }

    // onViewCreated 안에 있는 startActivityForResult을 위한 onActivityResult 를 만든다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // requestCode 가 0일
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            image_filePath = data?.data
            try {
                // glide 를 활용하여 R.id.img_firebase_pic 에 선택한 이미지를 미리 보여준다.
                Glide.with(this)
                        .asBitmap()
                        .load(image_filePath)
                        .centerCrop()
                        .into(img_firebase_pic)
            } catch (e: Exception) {
                e.message
            }
        }
    }

    private fun showProgress() {
        // 프로그래스바 보이기
        progressBar_firebase.visibility = View.VISIBLE
        // 프로그래스바 실행 중일때 화면 터치 무효화
        activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideProgress() {
        // 프로그래스바 숨기기
        progressBar_firebase.visibility = View.GONE
        // 프로그래스바 끝나면 화면 터치 유효화
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }


}
