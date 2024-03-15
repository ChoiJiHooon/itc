package com.example.itc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import api.ImageApi
import api.ImageInstance
import com.bumptech.glide.Glide
import com.example.itc.databinding.ActivityAnsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream

class AnsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "TFLite - ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
        val Imageapi = ImageInstance.imageapi
    }
   private lateinit var imageView: ImageView
    private lateinit var button : ImageButton
    private var imageUri: Uri? = null
    var wastecode : String? = null

    // 갤러리 open
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                activityResult.launch(intent)
                // openGallery()

            }
        }

    private lateinit var binding: ActivityAnsBinding // 뷰 바인딩 객체 선언
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val spinner1 = binding.spinner1
        val spinner2 = binding.spinner2
        imageView = binding.testImage
        val iuri = intent.data
        imageUri = Uri.parse(iuri.toString())




        val sintent = Intent(this, MapsActivity::class.java)
        // 뷰 바인딩을 통해 XML 레이아웃의 뷰 참조
        //val iuri = Uri.parse(intent.getStringExtra("IURI"))
        Log.d(TAG,iuri.toString())
        imageUri = Uri.parse(iuri.toString())
        Log.d(TAG,"${getRealPathFromURI(iuri!!,this)}")
        val file = File("${getRealPathFromURI(iuri!!,this)}")
        val mediaType = "image/*".toMediaType()
        val requestFile = file.asRequestBody(mediaType)
        val body = MultipartBody.Part.createFormData("img", file.name, requestFile)
        val imageapi = ImageInstance.imageapi

        imageapi.ImageSend(body).enqueue(object : Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.isSuccessful) {
                    // 성공적으로 이미지를 전송한 경우의 처리
                    Toast.makeText(this@AnsActivity, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                    // 서버로부터의 응답 데이터 활용 예시
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setCategories(responseBody.toInt())
                        Log.d("tett","This is a index : ${response.body()}")
                        sintent.putExtra("wasteCode", wastecode.toString())
                    }
                } else {
                    // 서버 응답이 실패한 경우의 처리
                    Toast.makeText(this@AnsActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"이미지 전송 실패")
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                // 네트워크 요청 실패 시의 처리
                Log.e("testt", "네트워크 요청 실패: ${t.message}", t)
                Toast.makeText(this@AnsActivity, "네트워크 요청 실패", Toast.LENGTH_SHORT).show()
            }
        })

        imageView.setImageURI(imageUri)
        //val bitmap = getBitmapFromUri(imageUri)
        //Log.e(TAG, "imageUri price: ${imageUri}")
        //Toast.makeText(baseContext, "imageUri price: ${imageUri}", Toast.LENGTH_SHORT).show()
        //setViewAndDetect(bitmap)

        //val sintent = intent
        //imageview1.setImageURI(Uri.parse(sintent.getStringExtra("imageUri")))
        //Toast.makeText(baseContext, sintent.getStringExtra("imageUri"),Toast.LENGTH_SHORT).show()
        title = "Edit Profile"
        /* @Suppress("DEPRECATION") val receivedUri: Uri? = intent.getParcelableExtra("imageUri")
         Toast.makeText(baseContext, intent.getStringExtra("imageUri"),Toast.LENGTH_SHORT).show()
         imageview1.setImageURI(receivedUri)
         runObjectDetection(receivedUri!!)*/




        // 대분류 정보
        val categories = resources.getStringArray(R.array.categories)

        // 대분류 스피너 설정
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = categoryAdapter

        //x

        // 소분류 스피너 설정 메서드
        //
        // 대분류 스피너 선택 이벤트 처리
        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 선택한 대분류에 따라 소분류와 가격 정보를 설정
                setCategories(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 선택이 해제될 때 수행할 작업 (옵션)
            }
        }

        binding.MapButton.setOnClickListener {
            val aintent = intent
            val email = aintent.getStringExtra("Email")
            val type = aintent.getStringExtra("Type")
            Log.d("Intent","Email Price : ${email.toString()}")
            Log.d("Intent","Type Price : ${type.toString()}")
            sintent.putExtra("Email",email)
            sintent.putExtra("Type",type)
            sintent.putExtra("wasteCode",wastecode)
            sintent.setData(imageUri!!)
            startActivity(sintent)
        }

    }

    private fun setCategories(index: Int) {
        // 대분류 스피너를 설정
        binding.spinner1.setSelection(index)
        // 선택한 대분류에 따라 소분류와 가격 정보를 설정
        when (index) {
            0 -> setSubcategories(R.array.dining_table_)
            1 -> setSubcategories(R.array.shelf_)
            2 -> setSubcategories(R.array.sofa_)
            3 -> setSubcategories(R.array.chair_)
            4 -> setSubcategories(R.array.wardrobe_)
            5 -> setSubcategories(R.array.desk_)
            6 -> setSubcategories(R.array.golf_club_)
            7 -> setSubcategories(R.array.jar_)
            8 -> setSubcategories(R.array.pot_)
            9 -> setSubcategories(R.array.bicycle_)
            10 -> setSubcategories(R.array.tricycle_)
            11 -> setSubcategories(R.array.television_)
            12 -> setSubcategories(R.array.regrigerator_ )
        }
    }

    fun setSubcategories(subcategoryArrayResId: Int) {
        val subcategoriesWithPrices = resources.getStringArray(subcategoryArrayResId)
        val subcategoryNames = subcategoriesWithPrices.map { it.split(",")[0] }.toTypedArray()
        val subcategoryAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, subcategoryNames)
        subcategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner2.adapter = subcategoryAdapter

        // 소분류 스피너 선택 이벤트 처리
        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // 선택한 소분류에 따른 가격 정보 표시
                val selectedSubcategoryString = subcategoriesWithPrices[position]
                val parts = selectedSubcategoryString.split(",")
                val subcategoryPrice = parts[1].toInt()
                val subcategoryCode = parts[2] // 코드 정보

                // 가격을 textView3에 표시
                //binding.textView3.text = "가격: $subcategoryPrice 원"
                // 가격을 textView3에 표시
                binding.textView.text = "가격: $subcategoryPrice 원"
                Log.d("Price", "가격: $subcategoryPrice 원")

                // 코드 정보를 변수에 저장
                wastecode = subcategoryCode.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 선택이 해제될 때 수행할 작업 (옵션)
            }
        }
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {

        if(it.resultCode == RESULT_OK && it.data != null){
            val uri = it.data!!.data
            imageUri = uri

            val file = File(absolutelyPath(uri!!))
            val mediaType = "image/jpeg".toMediaType()
            val requestFile = file.asRequestBody(mediaType)
            val body = MultipartBody.Part.createFormData("img", file.name, requestFile)
            Glide.with(this)
                 .load(uri)
                 .into(binding.testImage)


            Imageapi.ImageSend(body).enqueue(object: Callback<Int>{
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if(response.isSuccessful){
                        Toast.makeText(this@AnsActivity, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                        setSubcategories(response.body()!!)
                        Log.d("tett","This is a index : ${response.body()}")
                    }else{
                        Toast.makeText(this@AnsActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("tett",response.body().toString())
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Log.d("testt", t.message.toString())
                }

            })

            //val bitmap = getBitmapFromUri(imageUri!!)
            //Log.e(TAG, "imageUri price: ${imageUri}")
            //Toast.makeText(baseContext, "imageUri price: ${imageUri}", Toast.LENGTH_SHORT).show()
            //setViewAndDetect(bitmap)
        }
    }
/*
    fun absolutelyPath(path: Uri, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)

        return result!!
    }*/
fun absolutelyPath(path: Uri?): String {

    var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    var c: Cursor?= contentResolver.query(path!!, proj, null, null, null)
    var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    c?.moveToFirst()

    var result = c?.getString(index!!)

    return result!!
}

    fun getRealPathFromURI(contentUri: Uri, context: Context): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


    fun sendImage( body: MultipartBody.Part){

    }

    private fun runObjectDetection(bitmap : Bitmap) {

        // 1. 이미지 객체 만들기
        val image = TensorImage.fromBitmap(bitmap)

        // 2. 검사 프로그램 객체 만들기
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(1) // 모델에서 감지해야 하는 최대 객체 수
            .setScoreThreshold(0.5f) // 감지된 객체를 반환하는 객체 감지기의 신뢰도
            // .setDisplayNamesLocale("ko")
            .build()

        val detector = ObjectDetector.createFromFileAndOptions(
            this,
            "best_with_metadata_objd.tflite", // 실제 모델 파일 이름으로 수정
            options
        )

        // 3. 검사 프로그램에 피드 이미지
        val results = detector.detect(image) // 검사 프로그램에 이미지 전달

        // 4. 결과 출력 메소드 호출
        debugPrint(results)
        /*
                // 5. 입력 이미지에 감지 결과 그리기
                val resultToDisplay = results.map {
                    val category = it.categories.first()
                    val text = "${category.label}, ${category.score.times(100).toInt()}%"

                    DetectionResult(it.boundingBox, text)
                }
                val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
                runOnUiThread {
                    imageView.setImageBitmap(imgWithResult)
                }*/
    }

    private fun debugPrint(results: List<Detection>) {
        for ((i, obj) in results.withIndex()) {
            val box = obj.boundingBox

            Log.d(TAG, "Detected object : $i")
            Log.d(TAG, "  boundingBox : (${box.left}, ${box.top}) - (${box.right}, ${box.bottom})")

            // 첫 번째 카테고리 정보 가져오기
            val firstCategory = obj.categories.firstOrNull()
            if (firstCategory != null) {
                // 첫 번째 카테고리 정보를 Toast로 출력
                Toast.makeText(
                    baseContext,
                    "Detected category: ${firstCategory.label}",
                    Toast.LENGTH_LONG
                ).show()
                binding.textView.text = firstCategory.label
            } else {
                // 만약 카테고리 정보가 없는 경우에 대한 처리
                Toast.makeText(baseContext, "No category information available.", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }




    /*
      setViewAndDetect(bitmap: Bitmap)
           Set image to view and call object detection
     */
    private fun setViewAndDetect(bitmap: Bitmap) {
        // Display capture image
        imageView.setImageBitmap(bitmap)

        // Run ODT and display result
        // Note that we run this in the background thread to avoid blocking the app UI because
        // TFLite object detection is a synchronised process.
        lifecycleScope.launch(Dispatchers.Default) { runObjectDetection(bitmap) }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }


    /*
      drawDetectionResult(bitmap: Bitmap, detectionResults: List<DetectionResult>
           Draw a box around each objects and show the object's name.
     */
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<DetectionResult>
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()
        pen.textAlign = Paint.Align.LEFT

        detectionResults.forEach {
            // draw bounding box
            pen.color = Color.RED
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)


            val tagSize = Rect(0, 0, 0, 0)

            // calculate the right font size
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.YELLOW
            pen.strokeWidth = 2F

            pen.textSize = MAX_FONT_SIZE
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()

            // adjust the font size so texts are inside the bounding box
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text, box.left + margin,
                box.top + tagSize.height().times(1F), pen
            )
        }
        return outputBitmap
    }
}

/*
  DetectionResult
       A class to store the visualization info of a detected object.
 */
data class DetectionResult(val boundingBox: RectF, val text: String)


