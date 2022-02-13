package com.project.elibrary

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.elibrary.databinding.ActivityHomepageBinding
import android.content.Intent
import android.net.Uri

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.elibrary.book.BookAdapter
import com.project.elibrary.book.BookViewModel
import com.project.elibrary.genre.GenreAdapter
import com.project.elibrary.genre.GenreAddActivity
import com.project.elibrary.genre.GenreViewModel


class HomepageActivity : AppCompatActivity() {

    private var binding: ActivityHomepageBinding? = null
    private var genreAdapter: GenreAdapter? = null
    private var bookAdapter: BookAdapter? = null

    override fun onResume() {
        super.onResume()
        initRecyclerViewGenre()
        initViewModelGenre()

        initRecyclerViewBook()
        initViewModelBook()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        checkRole()

        binding?.addGenre?.setOnClickListener {
            startActivity(Intent(this, GenreAddActivity::class.java))
        }

        binding?.logout?.setOnClickListener {
            signOut()
        }


        binding?.web?.setOnClickListener {
            val intent  = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mdcalc.com/"))
            startActivity(intent)
        }

    }

    private fun initRecyclerViewGenre() {
        binding?.rvGenre?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        genreAdapter = GenreAdapter()
        binding?.rvGenre?.adapter = genreAdapter
    }

    private fun initViewModelGenre() {
        val viewModel = ViewModelProvider(this)[GenreViewModel::class.java]

        binding?.progressBarGenre?.visibility = View.VISIBLE
        viewModel.setListCategory()
        viewModel.getGenreList().observe(this) { genre ->
            if (genre.size > 0) {
                genreAdapter!!.setData(genre)
            }
            binding?.progressBarGenre?.visibility = View.GONE
        }
    }

    private fun initRecyclerViewBook() {
        binding?.recyclerView?.layoutManager = LinearLayoutManager(
            this,
            )
        bookAdapter = BookAdapter()
        binding?.recyclerView?.adapter = bookAdapter
    }

    private fun initViewModelBook() {
        val viewModel = ViewModelProvider(this)[BookViewModel::class.java]

        binding?.progressBarBook?.visibility = View.VISIBLE
        viewModel.setListBookByLatest()
        viewModel.getGenreList().observe(this) { book ->
            if (book.size > 0) {
                bookAdapter!!.setData(book)
                binding?.noData?.visibility = View.GONE
            }else {
                binding?.noData?.visibility = View.VISIBLE
            }
            binding?.progressBarBook?.visibility = View.GONE
        }
    }


    @SuppressLint("SetTextI18n")
    private fun checkRole() {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("name").toString()
                binding?.name?.text = "Hai,$name"
                if (it.data?.get("role").toString() == "admin") {
                    binding?.addGenre?.visibility = View.VISIBLE
                }
            }
    }


    private fun signOut() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah anda yakin ingin melakukan Logout ?")
            .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
            .setPositiveButton("YA") { dialogInterface, _ ->
                dialogInterface.dismiss()
                // sign out dari firebase autentikasi
                FirebaseAuth.getInstance().signOut()

                // go to login activity
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                dialogInterface.dismiss()
                startActivity(intent)
                finish()
            }
            .setNegativeButton("TIDAK", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}