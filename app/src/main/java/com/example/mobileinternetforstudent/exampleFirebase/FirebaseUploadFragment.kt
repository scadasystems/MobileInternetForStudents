package com.example.mobileinternetforstudent.exampleFirebase


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
class FirebaseUploadFragment : Fragment() {

    private var image_filePath: Uri? = null
    /* 추가 사항 */
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? =null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_firebase_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        img_firebase_pic.setAnimation("ani_image_loading.json")
        img_firebase_pic.loop(true)
        img_firebase_pic.playAnimation()

        btn_choose.setOnClickListener {
            val intent_image = Intent()
            intent_image.setType("image/*")
            intent_image.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent_image, "이미지를 선택하세요."), 0)
        }

        btn_upload.setOnClickListener {
            uploadFile()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    private fun uploadFile() {
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        if (image_filePath != null) {
            progressBar_firebase.visibility = View.VISIBLE

            var now = LocalDateTime.now()
            var filename = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
            val ref = storageReference?.child("uploads/" + filename)
            val uploadTask = ref?.putFile(image_filePath!!)

            val uriTask = uploadTask?.continueWithTask(
                    Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                        if (!it.isSuccessful) {
                            progressBar_firebase.visibility = View.GONE
                            toast("업로드를 실패했습니다.")
                            it.exception?.let { throw it }
                        }
                        return@Continuation ref.downloadUrl
                    }
            )?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = it.result
                    progressBar_firebase.visibility = View.GONE
                    toast("업로드가 완료되었습니다.")
                    addUploadRecord(downloadUri.toString())
                } else {
                    progressBar_firebase.visibility = View.GONE
                    toast("업로드를 실패했습니다.")
                }
            }?.addOnFailureListener {  }
        } else {
            toast("파일을 먼저 선택하세요.")
        }
    }

    private fun addUploadRecord(uri: String) {
        val db = FirebaseFirestore.getInstance()

        val data = HashMap<String, Any>()
        data["ImageUrl"] = uri
        db.collection("posts")
                .add(data)
                .addOnSuccessListener {
                    toast("업로드가 완료되었습니다.")
                }.addOnFailureListener{}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            image_filePath = data?.data
            try {
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


}
