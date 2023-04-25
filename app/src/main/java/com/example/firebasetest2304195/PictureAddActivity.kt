package com.example.firebasetest2304195

import android.app.Activity
import android.content.Intent
import android.graphics.Picture
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.firebasetest2304195.databinding.ActivityPictureAddBinding
import java.text.SimpleDateFormat
import java.util.*

class PictureAddActivity : AppCompatActivity() {
    lateinit var binding: ActivityPictureAddBinding
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPictureAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //외부저장소 접근 허용했는지 응답받음
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageUri = it.data?.data
                Glide.with(applicationContext).load(imageUri).into(binding.ivAddPicture)
            }
        }
        //이벤트 접근
        binding.ivAddPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        }
        //fireStore 실시간 데이타베이스 저장과 storage 사진저장
        binding.btnPictureSave.setOnClickListener {
            if (binding.ivAddPicture.drawable != null && binding.edtAddContent.text.isNotEmpty()) {
                //실시간 테이블 picture에 push()하면 key값이 등록되는데 이것을 가져옴
                if (!MyApplication.checkAuth()) {
                    Toast.makeText(applicationContext, "인증이 안되었습니다", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                binding.progressBar.visibility = View.VISIBLE
                val pictureDAO = PictureDAO()
                //실시간 데이터베이스 인덱스 키값
                val docID = pictureDAO.databaseReference?.push()?.key
                val email = MyApplication.email
                val content = binding.edtAddContent.text.toString().trim()
                val date = SimpleDateFormat("yy-MM-dd HH.mm.ss").format(Date())
                val pictureData = PictureData(docID, email, content, date)


                //실시간데이터베이스 picture 테이블에 클래스 저장
                pictureDAO.databaseReference?.child(docID!!)?.setValue(pictureData)
                    ?.addOnSuccessListener {
                        Log.e("PictureAddActivity", "pictureData picture 테이블입력성공")
                        //이미지를 스토리지에 저장------------------------------------------------
                        val pictureRef = pictureDAO.storage?.reference?.child("images/${docID}.png")
                        pictureRef?.putFile(imageUri!!)?.addOnSuccessListener {
                            Toast.makeText(applicationContext, "이미지업로드성공", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("PictureAddActivity", "이미지업로드성공")
                            binding.progressBar.visibility = View.INVISIBLE
                            finish()
                        }?.addOnFailureListener {
                            Toast.makeText(applicationContext, "이미지업로드실패", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("PictureAddActivity", "이미지업로드실패")
                            binding.progressBar.visibility = View.INVISIBLE

                        }
                        //이미지를 스토리지에 저장------------------------------------------------
                    }?.addOnFailureListener {
                    Log.e("PictureAddActivity", "pictureData picture 테이블입력실패")
                    binding.progressBar.visibility = View.INVISIBLE

                }
            } else {
                Toast.makeText(applicationContext, "사진과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.INVISIBLE
            }
        }//end of btnPictureSave
    }
}