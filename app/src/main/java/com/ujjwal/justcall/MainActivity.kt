package com.ujjwal.justcall

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.telecom.TelecomManager
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher

class MainActivity : Activity() {
    private val gold = Color.rgb(201,169,110)
    private val bg = Color.rgb(9,11,17)
    private val panel = Color.rgb(24,27,36)
    private lateinit var number: EditText
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNeededPermissions()
        buildScreen()
    }

    private fun requestNeededPermissions() {
        val missing = arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)
            .filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), 20)
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 26, 22, 18)
            setBackgroundColor(bg)
        }
        val header = TextView(this).apply {
            text = "JUST CALL"
            textSize = 28f
            letterSpacing = .16f
            setTextColor(gold)
            typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
        }
        root.addView(header)
        root.addView(TextView(this).apply {
            text = "Premium Phone"
            textSize = 13f
            setTextColor(Color.LTGRAY)
        })
        number = EditText(this).apply {
            hint = "Search name or enter number"
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 20f
            singleLine = true
            setPadding(18, 14, 18, 14)
            setBackgroundColor(panel)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }
        val params = LinearLayout.LayoutParams(-1, -2).apply { topMargin = 26; bottomMargin = 12 }
        root.addView(number, params)
        val call = Button(this).apply {
            text = "CALL  •  SIM"
            setTextColor(Color.BLACK)
            setBackgroundTintList(android.content.res.ColorStateList.valueOf(gold))
            setOnClickListener { placeCall(number.text.toString()) }
        }
        root.addView(call, LinearLayout.LayoutParams(-1, 54))
        val defaultBtn = Button(this).apply {
            text = "Set as Default Phone App"
            setOnClickListener { requestDefaultDialer() }
        }
        root.addView(defaultBtn)
        val tabs = TextView(this).apply {
            text = "RECENTS                         CONTACTS"
            textSize = 14f
            setTextColor(gold)
            setPadding(0, 24, 0, 12)
        }
        root.addView(tabs)
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val scroll = ScrollView(this).apply { addView(list) }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { showContacts(s?.toString().orEmpty()) }
            override fun afterTextChanged(s: Editable?) {}
        })
        setContentView(root)
        showContacts("")
    }

    private fun requestDefaultDialer() {
        val rm = getSystemService(RoleManager::class.java)
        if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_DIALER)) {
            startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_DIALER), 100)
        } else {
            Toast.makeText(this, "Default dialer role is not available on this device", Toast.LENGTH_LONG).show()
        }
    }

    private fun placeCall(raw: String) {
        val value = raw.trim()
        if (value.isBlank()) { Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show(); return }
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 21); return
        }
        try {
            val telecom = getSystemService(TELECOM_SERVICE) as TelecomManager
            val accounts = telecom.callCapablePhoneAccounts
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode(value)))
            if (accounts.size > 1) {
                val chooser = Intent(Intent.ACTION_CHOOSER).apply {
                    putExtra(Intent.EXTRA_INTENT, intent)
                    putExtra(Intent.EXTRA_TITLE, "Choose SIM")
                }
                startActivity(chooser)
            } else startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to start call: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showContacts(query: String) {
        list.removeAllViews()
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            addRow("Allow Contacts permission to search contacts", ""); return
        }
        try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER),
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ? OR " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?",
                arrayOf("%$query%", "%$query%"),
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            cursor?.use {
                var count = 0
                while (it.moveToNext() && count < 35) {
                    addRow(it.getString(0) ?: "Unknown", it.getString(1) ?: "")
                    count++
                }
            }
            if (list.childCount == 0) addRow("No matching contacts", "")
        } catch (_: Exception) { addRow("Contacts unavailable", "") }
    }

    private fun addRow(name: String, phone: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(14, 13, 14, 13)
            setBackgroundColor(panel)
        }
        row.addView(TextView(this).apply { text = name; textSize = 16f; setTextColor(Color.WHITE) })
        if (phone.isNotBlank()) row.addView(TextView(this).apply { text = phone; textSize = 13f; setTextColor(gold) })
        row.setOnClickListener { if (phone.isNotBlank()) { number.setText(phone); number.setSelection(number.text.length) } }
        val lp = LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 8 }
        list.addView(row, lp)
    }
}
