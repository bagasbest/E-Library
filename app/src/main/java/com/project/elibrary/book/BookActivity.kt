package com.project.elibrary.book

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.elibrary.R
import com.project.elibrary.databinding.ActivityBookBinding
import com.project.elibrary.genre.GenreEditActivity

class BookActivity : AppCompatActivity() {

    private var binding: ActivityBookBinding? = null
    private var adapter: BookAdapter? = null
    private var genre: String? = null

    override fun onResume() {
        super.onResume()
        genre = intent.getStringExtra(EXTRA_GENRE)
        if (genre != "Semua") {
            binding?.category?.text = genre
        }
        initRecyclerView()
        initViewModel("all")
        checkRole(genre)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.search?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                if (query.isEmpty()) {
                    initRecyclerView()
                    initViewModel("all")
                } else {
                    initRecyclerView()
                    initViewModel(query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding?.backButton?.setOnClickListener {
            onBackPressed()
        }

        binding?.addBook?.setOnClickListener {
            val intent = Intent(this, BookAddActivity::class.java)
            intent.putExtra(BookAddActivity.EXTRA_CATEGORY, genre)
            startActivity(intent)
        }

        binding?.edit?.setOnClickListener {
            val intent = Intent(this, GenreEditActivity::class.java)
            intent.putExtra(GenreEditActivity.EXTRA_CATEGORY, genre)
            startActivity(intent)
        }

        binding?.delete?.setOnClickListener {
            deleteGenre(genre)
        }
    }

    private fun initRecyclerView() {
        binding?.rvBook?.layoutManager = LinearLayoutManager(
            this,
        )
        adapter = BookAdapter()
        binding?.rvBook?.adapter = adapter
    }

    private fun initViewModel(query: String) {
        val viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        binding?.progressBar?.visibility = View.VISIBLE

        if(genre == "Semua") {
            if (query == "all") {
                viewModel.setListBookByAll()
            } else {
                viewModel.setListBookByAllAndQuery(query)
            }
        } else {
            if (query == "all") {
                viewModel.setListBookByGenre(genre!!)
            } else {
                viewModel.setListBookByGenreAndQuery(genre!!, query)
            }
        }
        viewModel.getGenreList().observe(this) { book ->
            if (book.size > 0) {
                adapter!!.setData(book)
                binding?.noData?.visibility = View.GONE
            } else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun deleteGenre(genre: String?) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Menghapus Kategori")
            .setMessage("Apakah anda yakin ingin menghapus kategori $genre ?")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()

                val mProgressDialog = ProgressDialog(this)
                mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...")
                mProgressDialog.setCanceledOnTouchOutside(false)
                mProgressDialog.show()

                FirebaseFirestore
                    .getInstance()
                    .collection("genre")
                    .whereEqualTo("genre", genre)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val uid = document.data["uid"].toString()

                            FirebaseFirestore
                                .getInstance()
                                .collection("category")
                                .document(uid)
                                .delete()
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        mProgressDialog.dismiss()
                                        Toast.makeText(
                                            this,
                                            "Berhasil menghapus kategori ${genre}.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBackPressed()
                                    } else {
                                        mProgressDialog.dismiss()
                                        Toast.makeText(
                                            this,
                                            "Gagal menghapus kategori ${genre}, mohon periksa koneksi internet anda",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    }

            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    private fun checkRole(genre: String?) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.data?.get("role").toString() == "admin" && genre != "Semua") {
                    binding?.addBook?.visibility = View.VISIBLE
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
        const val EXTRA_GENRE = "genre"
    }
}