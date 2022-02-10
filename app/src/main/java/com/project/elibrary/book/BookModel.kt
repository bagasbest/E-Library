package com.project.elibrary.book

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookModel(

    var uid : String? = null,
    var title: String? = null,
    var writer: String? = null,
    var genre: String? = null,
    var description: String? = null,
    var image: String? = null,
    var pdf: String? = null,

) : Parcelable