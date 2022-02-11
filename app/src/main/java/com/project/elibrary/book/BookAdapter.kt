package com.project.elibrary.book

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.elibrary.databinding.ItemBook2Binding
class BookAdapter : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    private val bookList = ArrayList<BookModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: ArrayList<BookModel>) {
        bookList.clear()
        bookList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemBook2Binding)  : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(model: BookModel) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(model.image)
                    .into(image)

                title.text = model.title
                writer.text = "Oleh: ${model.writer}"
                description.text = model.description
                cv.setOnClickListener {
                    val intent = Intent(itemView.context, BookDetailActivity::class.java)
                    intent.putExtra(BookDetailActivity.EXTRA_BOOK, model)
                    itemView.context.startActivity(intent)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBook2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookList[position])
    }

    override fun getItemCount(): Int = bookList.size
}