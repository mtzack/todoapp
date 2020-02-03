package com.mtzack.todoapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
    @PrimaryKey val id :Int,
    @ColumnInfo(name = "checked") var checked: Boolean,
    @ColumnInfo(name = "text") var text: String,
    @ColumnInfo(name = "deleted_flag") val isDeleted:Boolean
)
