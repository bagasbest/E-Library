package com.project.elibrary.book

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.elibrary.HomepageActivity
import com.project.elibrary.MainActivity
import com.project.elibrary.R
import com.project.elibrary.databinding.ActivityBookEditBinding
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class BookEditActivity : AppCompatActivity() {

    private var binding: ActivityBookEditBinding? = null
    private var dp: String? = null
    private val REQUEST_FROM_DEVICE_PDF = 1002
    private val REQUEST_FROM_GALLERY = 1001
    private var pdfUri: Uri? = null
    private var pdfName: String? = null
    private var model: BookModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_BOOK)

        Glide.with(this)
            .load(model?.image)
            .into(binding!!.image)

        binding?.title?.setText(model?.title)
        binding?.writer?.setText(model?.writer)
        binding?.description?.setText(model?.description)

        binding?.titlePdf?.text = model?.title



        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }


        binding?.saveBook?.setOnClickListener {
            formValidation()
        }

        // KLIK TAMBAH GAMBAR
        binding?.imageHint?.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .start(REQUEST_FROM_GALLERY)
        }


        binding?.uploadPdf?.setOnClickListener {
            pdfPickFromGallery()
        }


    }

    private fun formValidation() {
        val title = binding?.title?.text.toString().trim()
        val writer = binding?.writer?.text.toString().trim()
        val description = binding?.description?.text.toString().trim()

        when {
            title.isEmpty() -> {
                Toast.makeText(this, "Judul buku tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            writer.isEmpty() -> {
                Toast.makeText(this, "Pengarang buku tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            description.isEmpty() -> {
                Toast.makeText(this, "Deskripsi buku tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
            else -> {

                // simpan buku pdf kedalam database
                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()

                if(pdfUri != null) {

                    /// file path and name in firebase storage
                    val filePathAndName = "pdf/file_" + System.currentTimeMillis() + ".pdf"

                    /// storage reference
                    val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)


                    /// upload pdf, you can upload any type
                    storageReference.putFile(pdfUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            /// pdf uploaded, get url of uploaded pdf
                            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                            while (!uriTask.isSuccessful);
                            val downloadUri: Uri = uriTask.result
                            if (uriTask.isSuccessful) {
                                // url of uploaded pdf is received
                                // now we can add pdf details to our firebase firestore
                                val pdfAdd: MutableMap<String, Any> = HashMap()
                                pdfAdd["title"] = title
                                pdfAdd["titleTemp"] = title.lowercase(Locale.getDefault())
                                pdfAdd["pdf"] = "" + downloadUri
                                pdfAdd["writer"] = writer
                                pdfAdd["description"] = description
                                if(dp != null) {
                                    pdfAdd["image"] = dp!!
                                }
                                pdfAdd["genre"] = model?.genre.toString()


                                /// simpan kedalam database firestore
                                model?.uid?.let {
                                    FirebaseFirestore
                                        .getInstance()
                                        .collection("book")
                                        .document(it)
                                        .update(pdfAdd)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                mProgressDialog.dismiss()
                                                showSuccessDialog()
                                            } else {
                                                mProgressDialog.dismiss()
                                                showFailureDialog()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            mProgressDialog.dismiss()
                                            e.message?.let { Log.e("Exception pdf", it) }
                                        }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            mProgressDialog.dismiss()
                            e.message?.let { Log.e("Exception pdf", it) }
                        }
                } else {
                    val pdfAdd: MutableMap<String, Any> = HashMap()
                    pdfAdd["title"] = title
                    pdfAdd["titleTemp"] = title.lowercase(Locale.getDefault())
                    pdfAdd["writer"] = writer
                    pdfAdd["description"] = description
                    if(dp != null) {
                        pdfAdd["image"] = dp!!
                    }
                    pdfAdd["genre"] = model?.genre.toString()


                    /// simpan kedalam database firestore
                    model?.uid?.let {
                        FirebaseFirestore
                            .getInstance()
                            .collection("book")
                            .document(it)
                            .update(pdfAdd)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    mProgressDialog.dismiss()
                                    showSuccessDialog()
                                } else {
                                    mProgressDialog.dismiss()
                                    showFailureDialog()
                                }
                            }
                            .addOnFailureListener { e ->
                                mProgressDialog.dismiss()
                                e.message?.let { Log.e("Exception pdf", it) }
                            }
                    }
                }
            }
        }
    }

    private fun pdfPickFromGallery() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select PDF Document"),
            REQUEST_FROM_DEVICE_PDF
        )
    }


    @SuppressLint("SetTextI18n", "Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FROM_DEVICE_PDF) {
                pdfUri = data?.data
                binding!!.uploadPdf.text = "Dokumen PDF berhasil diunggah"
                if (pdfUri.toString().startsWith("content://")) {
                    val cursor: Cursor?
                    try {
                        cursor = contentResolver.query(pdfUri!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (pdfUri.toString().startsWith("file://")) {
                    pdfName = File(pdfUri.toString()).name
                }
                binding!!.titlePdf.text = pdfName
            } else if (requestCode == REQUEST_FROM_GALLERY) {
                uploadArticleDp(data?.data)
            }
        }
    }


    /// fungsi untuk mengupload foto kedalam cloud storage
    private fun uploadArticleDp(data: Uri?) {
        val mStorageRef = FirebaseStorage.getInstance().reference
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
        val imageFileName = "book/image_" + System.currentTimeMillis() + ".png"
        mStorageRef.child(imageFileName).putFile(data!!)
            .addOnSuccessListener {
                mStorageRef.child(imageFileName).downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        mProgressDialog.dismiss()
                        dp = uri.toString()
                        Glide
                            .with(this)
                            .load(dp)
                            .into(binding!!.image)
                    }
                    .addOnFailureListener { e: Exception ->
                        mProgressDialog.dismiss()
                        Toast.makeText(
                            this,
                            "Gagal mengunggah gambar",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("imageDp: ", e.toString())
                    }
            }
            .addOnFailureListener { e: Exception ->
                mProgressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Gagal mengunggah gambar",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("imageDp: ", e.toString())
            }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Berhasil Memperbarui Buku")
            .setMessage("Operasi berhasil")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("OKE") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val intent = Intent(this, HomepageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                dialogInterface.dismiss()
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun showFailureDialog() {
        AlertDialog.Builder(this)
            .setTitle("Gagal Memperbarui Buku")
            .setMessage("Terdapat kesalahan ketika mengunggah buku baru, silahkan periksa koneksi internet anda, dan coba lagi nanti")
            .setIcon(R.drawable.ic_baseline_clear_24)
            .setPositiveButton("OKE") { dialogInterface, _-> dialogInterface.dismiss() }
            .show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_BOOK = "book"
    }
}