package com.euder.store_buy_end_sell

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.UnsupportedSchemeException
import android.net.Uri
import android.provider.BaseColumns
import com.euder.store_buy_end_sell.NotesDatabaseHelper.Companion.TABLE_NOTE

class NoteProvider: ContentProvider() {
    private lateinit var mUriMatcher: UriMatcher
    private lateinit var dbHelper: NotesDatabaseHelper

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher.addURI(AUTHORITY, "notes", NOTES)
        mUriMatcher.addURI(AUTHORITY, "notes/#", NOTES_BY_ID)
        if (context != null) {dbHelper = NotesDatabaseHelper(context as Context)
        }
        return true


    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val linesAffecct: Int = db.delete( TABLE_NOTE, "${BaseColumns._ID} =?", arrayOf(uri.lastPathSegment) )
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffecct
        } else {
            throw UnsupportedSchemeException("Uri Inválida para exclusão")
        }    }

    override fun getType(uri: Uri): String? = throw UnsupportedSchemeException("Uri não implementado")

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if(mUriMatcher.match(uri)== NOTES) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val id: Long = db.insert(TABLE_NOTE, null, values)
            val insertUri = Uri.withAppendedPath(BASE_URI, id.toString())
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return insertUri
        } else{
            throw UnsupportedSchemeException("Uri Inválida para insierção")
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        return when{
            mUriMatcher.match(uri) == NOTES ->{
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor =
                    db.query(TABLE_NOTE, projection, selection, selectionArgs, null, null, sortOrder)
                cursor.setNotificationUri(context?.contentResolver, uri)
                cursor
            }
            mUriMatcher.match(uri) == NOTES_BY_ID -> {
                val db: SQLiteDatabase = dbHelper.writableDatabase
                val cursor =
                    db.query(TABLE_NOTE,projection,"${BaseColumns._ID} = ?", arrayOf(uri.lastPathSegment), null, null, sortOrder )
                cursor.setNotificationUri((context as Context).contentResolver,uri)
                cursor
            }
            else -> {
                throw UnsupportedSchemeException("Uri não implementada.")
            }
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        if (mUriMatcher.match(uri) == NOTES_BY_ID) {
            val db: SQLiteDatabase = dbHelper.writableDatabase
            val  linesAffect: Int =
                db.update(TABLE_NOTE, values, "${BaseColumns._ID} =?", arrayOf(uri.lastPathSegment))
            db.close()
            context?.contentResolver?.notifyChange(uri, null)
            return linesAffect
        } else {
            throw UnsupportedSchemeException("Uri não implementada.")
        }
    }

    companion object {
        const val AUTHORITY = "com.euder.contentprovider.provider"
        val BASE_URI = Uri.parse("content://$AUTHORITY")
        val URI_NOTES = Uri.withAppendedPath(BASE_URI,"notes")

        //"content://com.euder.contentprovider.provider/notes"

        const val NOTES = 1
        const val NOTES_BY_ID = 2
    }
}
