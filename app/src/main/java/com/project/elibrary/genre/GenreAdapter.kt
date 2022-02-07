package com.project.elibrary.genre

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.elibrary.book.BookActivity
import com.project.elibrary.databinding.ItemGenreBinding

class GenreAdapter : RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    private val categoryList = ArrayList<GenreModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<GenreModel>) {
        categoryList.clear()
        categoryList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemGenreBinding)  :RecyclerView.ViewHolder(binding.root){
        fun bind(model: GenreModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                genre.text = model.genre
                cv.setOnClickListener {
                    val intent = Intent(itemView.context, BookActivity::class.java)
                    intent.putExtra(BookActivity.EXTRA_GENRE, model.genre)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size
}