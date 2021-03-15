package com.euder.store_buy_end_sell

import android.database.Cursor

interface NoteClickedListener {
    fun noteClikedItem(cursor: Cursor)
    fun noteRemoveItem(cursor: Cursor?)
}