package com.github.drunlin.webappbox.model

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.github.drunlin.webappbox.R
import com.github.drunlin.webappbox.common.getRawText
import com.github.drunlin.webappbox.common.toBitmap
import com.github.drunlin.webappbox.common.toByteArray
import com.github.drunlin.webappbox.data.*
import dagger.Lazy
import java.util.*
import javax.inject.Inject

class DatabaseManager(private val context: Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "app.db"
        val DB_VERSION = 1

        val TABLE_APP = "app"
        val TABLE_RULE = "rule"
        val TABLE_PATTERN = "pattern"
        val TABLE_USER_AGENT = "user_agent"

        val ID = BaseColumns._ID
        val UUID = "uuid"
        val APP_ID = "app_id"
        val ORDER = "_order"
        val URL = "url"
        val NAME = "name"
        val ICON = "icon"
        val PATTERN = "pattern"
        val REGEX = "regex"
        val VALUE = "value"
        val COLOR = "color"
        val JS_ENABLE = "js_enable"
        val USER_AGENT = "user_agent"
        val ORIENTATION = "orientation"
        val FULL_SCREEN = "full_screen"
        val LAUNCH_MODE = "launch_mode"
        val LOCATION = "location"
    }

    @Inject lateinit var userAgentManager: Lazy<UserAgentManager>

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
        begin transaction;
        create table $TABLE_APP (
            $ID integer primary key,
            $UUID text not null,
            $URL text not null,
            $NAME text not null,
            $ICON blob not null,
            $LOCATION text not null
        );
        create table $TABLE_PATTERN (
            $ID integer primary key,
            $APP_ID integer not null,
            $PATTERN text not null,
            $REGEX integer not null
        );
        create table $TABLE_USER_AGENT (
            $ID integer primary key,
            $NAME text not null,
            $VALUE text not null
        );
        create table $TABLE_RULE (
            $ID integer primary key autoincrement,
            $APP_ID integer not null,
            $PATTERN text not null,
            $REGEX integer not null,
            $COLOR integer not null,
            $LAUNCH_MODE text not null,
            $ORIENTATION text not null,
            $FULL_SCREEN integer not null,
            $USER_AGENT integer not null,
            $JS_ENABLE integer not null,
            $ORDER integer default (last_insert_rowid())
        );
        ${context.getRawText(R.raw.setup)}
        commit;
        """
        sql.split(";\n").filter(String::isNotBlank).map { "$it;" }.forEach { db.execSQL(it) }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) = Unit

    private fun query(table: String, columns: Array<String>, selection: String?,
              selectionArgs: Array<String>?): Cursor {
        return readableDatabase.query(table, columns, selection, selectionArgs, null, null, null)
    }

    private fun query(table: String, columns: Array<String>, selection: String?): Cursor {
        return query(table, columns, selection, null)
    }

    private fun query(table: String, columns: Array<String>): Cursor {
        return query(table, columns, null)
    }

    private fun query(table: String, columns: Array<String>, selection: String?,
                      orderBy: String): Cursor {
        return readableDatabase.query(table, columns, selection, null, null, null, orderBy)
    }

    private fun delete(table: String, whereClause: String): Int {
        return writableDatabase.delete(table, whereClause, null)
    }

    private fun delete(table: String, ids: Set<Long>) {
        delete(table, ids.map { "$ID=$it" }.joinToString(" or "))
    }

    private fun insert(table: String, values: ContentValues): Long {
        return writableDatabase.insert(table, null, values)
    }

    private fun update(table: String, values: ContentValues, whereClause: String): Int {
        return writableDatabase.update(table, values, whereClause, null)
    }

    private fun Cursor.getBoolean(columnIndex: Int) = getInt(columnIndex) != 0

    private fun Webapp.toContentValues(): ContentValues {
        val values = ContentValues()
        values.put(URL, url)
        values.put(ICON, icon.toByteArray())
        values.put(NAME, name)
        values.put(LOCATION, locationPolicy.name)
        return values
    }

    fun insert(uuid: String, webapp: Webapp): Long {
        val values = webapp.toContentValues()
        values.put(UUID, uuid)
        return insert(TABLE_APP, values)
    }

    fun update(webapp: Webapp) {
        update(TABLE_APP, webapp.toContentValues(), "$ID=${webapp.id}")
    }

    fun updateLocationPolicy(id: Long, policy: Policy) {
        val values = ContentValues()
        values.put(LOCATION, policy.name)
        update(TABLE_APP, values, "$ID=$id")
    }

    fun deleteWebapp(id: Long) {
        delete(TABLE_APP, "$ID=$id")
        delete(TABLE_PATTERN, "$APP_ID=$id")
        delete(TABLE_RULE, "$APP_ID=$id")
    }

    fun getWebapp(id: Long): Webapp {
        val cursor = query(DatabaseManager.TABLE_APP, arrayOf(URL, ICON, NAME, LOCATION), "$ID=$id")
        cursor.moveToFirst()
        val webapp = Webapp(id, cursor.getString(0), cursor.getBlob(1).toBitmap(),
                cursor.getString(2), Policy.valueOf(cursor.getString(3)))
        cursor.close()
        return webapp
    }

    fun getPatterns(id: Long): MutableList<URLPattern> {
        val list = LinkedList<URLPattern>()
        val cursor = query(TABLE_PATTERN, arrayOf(ID, PATTERN, REGEX), "$APP_ID=$id")
        if (cursor.moveToFirst())
            do
                list.add(URLPattern(cursor.getLong(0), cursor.getString(1), cursor.getBoolean(2)))
            while (cursor.moveToNext())
        cursor.close()
        return list
    }

    private fun URLPattern.toContentValues(): ContentValues {
        val values = ContentValues()
        values.put(PATTERN, pattern)
        values.put(REGEX, regex)
        return values
    }

    fun insert(id: Long, pattern: URLPattern): Long {
        val values = pattern.toContentValues()
        values.put(APP_ID, id)
        return insert(TABLE_PATTERN, values)
    }

    fun update(pattern: URLPattern) {
        update(TABLE_PATTERN, pattern.toContentValues(), "$ID=${pattern.id}")
    }

    fun deletePatterns(ids: Set<Long>) {
        delete(TABLE_PATTERN, ids)
    }

    private fun Rule.toContentValues(): ContentValues {
        val values = ContentValues()
        values.put(PATTERN, pattern.pattern)
        values.put(REGEX, pattern.regex)
        values.put(COLOR, color)
        values.put(LAUNCH_MODE, launchMode.name)
        values.put(ORIENTATION, orientation.name)
        values.put(FULL_SCREEN, fullScreen)
        values.put(USER_AGENT, userAgent.id)
        values.put(JS_ENABLE, jsEnabled)
        return values
    }

    fun insert(id: Long, rule: Rule): Long {
        val values = rule.toContentValues()
        values.put(APP_ID, id)
        return insert(TABLE_RULE, values)
    }

    fun update(rule: Rule) {
        update(TABLE_RULE, rule.toContentValues(), "$ID=${rule.id}")
    }

    fun deleteRules(ids: Set<Long>) {
        delete(TABLE_RULE, ids)
    }

    fun getRules(id: Long): MutableList<Rule> {
        val list = LinkedList<Rule>()
        val columns = arrayOf(ID, PATTERN, REGEX, COLOR, LAUNCH_MODE, ORIENTATION, FULL_SCREEN,
                USER_AGENT, JS_ENABLE)
        val cursor = query(TABLE_RULE, columns, "$APP_ID=$id", ORDER)
        if (cursor.moveToFirst())
            do
                list.add(Rule(cursor.getLong(0), URLPattern(cursor.getString(1), cursor.getBoolean(2)),
                        cursor.getInt(3), LaunchMode.valueOf(cursor.getString(4)),
                        Orientation.valueOf(cursor.getString(5)), cursor.getBoolean(6),
                        userAgentManager.get().getUserAgent(cursor.getLong(7)), cursor.getBoolean(8)))
            while (cursor.moveToNext())
        cursor.close()
        return list
    }

    fun swapRules(from: Long, to: Long) {
        val sql = """
        update $TABLE_RULE
            set $ORDER = (select sum($ORDER) from $TABLE_RULE where $ID in ($from, $to)) - $ORDER
        where $ID in ($from, $to);
        """
        writableDatabase.execSQL(sql)
    }

    private fun UserAgent.toContentValues(): ContentValues {
        val values = ContentValues()
        values.put(NAME, name)
        values.put(VALUE, value)
        return values
    }

    fun insert(userAgent: UserAgent): Long {
        return insert(TABLE_USER_AGENT, userAgent.toContentValues())
    }

    fun update(userAgent: UserAgent) {
        update(TABLE_USER_AGENT, userAgent.toContentValues(), "$ID=${userAgent.id}")
    }

    fun deleteUserAgents(ids: Set<Long>) {
        delete(TABLE_USER_AGENT, ids)
    }

    fun getUserAgents(): MutableList<UserAgent> {
        val list = LinkedList<UserAgent>()
        val cursor = query(TABLE_USER_AGENT, arrayOf(ID, NAME, VALUE))
        if (cursor.moveToFirst())
            do
                list.add(UserAgent(cursor.getLong(0), cursor.getString(1), cursor.getString(2)))
            while (cursor.moveToNext())
        cursor.close()
        return list
    }

    fun getShortcuts(): MutableList<Shortcut> {
        val list = LinkedList<Shortcut>()
        val cursor = query(TABLE_APP, arrayOf(ID, UUID, ICON, NAME), null, NAME)
        if (cursor.moveToFirst())
            do
                list.add(Shortcut(cursor.getLong(0), cursor.getString(1),
                        cursor.getBlob(2).toBitmap(), cursor.getString(3)))
            while (cursor.moveToNext())
        cursor.close()
        return list
    }

    fun isWebappExisted(url: String): Boolean {
        val cursor = query(TABLE_APP, arrayOf(ID), "$URL=?", arrayOf(url))
        val existed = cursor.moveToFirst()
        cursor.close()
        return existed
    }

    fun getWebappId(uuid: String): Long? {
        val cursor = query(TABLE_APP, arrayOf(ID), "$UUID=?", arrayOf(uuid))
        val id = if (cursor.moveToFirst()) cursor.getLong(0) else null
        cursor.close()
        return id
    }
}
