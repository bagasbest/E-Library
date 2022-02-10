package com.project.elibrary.book

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
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


        binding?.backButton?.setOnClickListener {
            onBackPressed()
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