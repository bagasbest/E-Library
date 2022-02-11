package com.project.elibrary.book

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.project.elibrary.R
import com.project.elibrary.databinding.ActivityBookDetailBinding


class BookDetailActivity : AppCompatActivity() {

    private var binding: ActivityBookDetailBinding? = null
    private var model: BookModel? =null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        model = intent.getParcelableExtra(EXTRA_BOOK)
        checkRole()

        Glide.with(this)
            .load(model?.image)
            .into(binding!!.roundedImageView)

        binding?.title?.text = model?.title
        binding?.writer?.text = "Oleh: ${model?.writer}"
        binding?.genre?.text = "Kategori Buku: ${model?.genre}"
        binding?.description?.text = model?.description


        binding?.readNow?.setOnClickListener {
            val intent = Intent(this, BookReadActivity::class.java)
            intent.putExtra(BookReadActivity.PDF, model?.pdf)
            startActivity(intent)
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, BookEditActivity::class.java)
            intent.putExtra(BookEditActivity.EXTRA_BOOK, model)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            deleteBook()
        }

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun deleteBook() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Buku")
            .setMessage("Apakah anda yakin ingin mengjapus buku ${model?.title} ?")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()

                model?.uid?.let {
                    FirebaseFirestore
                        .getInstance()
                        .collection("book")
                        .document(it)
                        .delete()
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful) {

                                /// delete from cloud firestore
                                val reference =
                                    model?.pdf?.let { it1 ->
                                        FirebaseStorage.getInstance().getReferenceFromUrl(
                                            it1
                                        )
                                    }
                                reference?.delete()
                                    ?.addOnSuccessListener { //deleted from cloud firestore
                                        mProgressDialog.dismiss()
                                        Toast.makeText(this, "Sukses menghapus buku ${model?.title}", Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                    }
                                    ?.addOnFailureListener { _ -> // failed delete
                                        mProgressDialog.dismiss()
                                        Toast.makeText(this, "gagal menghapus buku ${model?.title}, mohon periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                    }
                            } else {
                                mProgressDialog.dismiss()
                                Toast.makeText(this, "gagal menghapus buku ${model?.title}, mohon periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if(it.data?.get("role") == "admin") {
                    binding?.edit?.visibility = View.VISIBLE
                    binding?.delete?.visibility = View.VISIBLE
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val EXTRA_BOOK = "book"
    }
}