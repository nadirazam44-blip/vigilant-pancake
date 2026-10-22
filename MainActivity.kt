package com.zomaxit.app

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import android.content.Context
import android.net.Uri

class MainActivity : Activity() {
    private val bg = Color.rgb(7, 12, 22)
    private val panel = Color.rgb(16, 32, 50)
    private val cyan = Color.rgb(65, 215, 255)
    private val prefs by lazy { getSharedPreferences("zoma_access", Context.MODE_PRIVATE) }
    private val key1h = "ZOMA-1H-2026"
    private val keyPermanent = "ZOMA-PERM-2026"
    private val pickFile = 401

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasAccess()) showHome() else showActivation()
    }

    private fun hasAccess(): Boolean {
        return when (prefs.getString("type", "") ?: "") {
            "permanent" -> true
            "hour" -> {
                val valid = System.currentTimeMillis() < prefs.getLong("until", 0L)
                if (!valid) prefs.edit().clear().apply()
                valid
            }
            else -> false
        }
    }

    private fun root(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(28, 32, 28, 32)
        setBackgroundColor(bg)
    }

    private fun title(text: String, size: Float = 30f) = TextView(this).apply {
        this.text = text; textSize = size; setTextColor(cyan)
        typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        gravity = Gravity.CENTER; setPadding(0, 10, 0, 10)
    }

    private fun button(text: String, action: () -> Unit) = Button(this).apply {
        this.text = text; textSize = 17f; setTextColor(Color.WHITE)
        setBackgroundColor(panel); isAllCaps = false
        setOnClickListener { action() }
    }

    private fun showActivation() {
        val layout = root()
        layout.addView(title("ZOMA XIT"))
        layout.addView(TextView(this).apply {
            text = "ACTIVATION REQUIRED\nأدخل كود التفعيل للمتابعة"
            textSize = 15f; setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
            setPadding(0, 8, 0, 18)
        })
        val input = EditText(this).apply {
            hint = "Enter activation code"; setSingleLine(true)
            setTextColor(Color.WHITE); setHintTextColor(Color.GRAY)
            setPadding(18, 12, 18, 12); setBackgroundColor(panel)
        }
        layout.addView(input, LinearLayout.LayoutParams(-1, -2))
        layout.addView(button("ACTIVATE") {
            when (input.text.toString().trim()) {
                key1h -> {
                    prefs.edit().putString("type", "hour")
                        .putLong("until", System.currentTimeMillis() + 3600000L).apply()
                    showHome()
                }
                keyPermanent -> {
                    prefs.edit().putString("type", "permanent").apply(); showHome()
                }
                else -> Toast.makeText(this, "Invalid activation code", Toast.LENGTH_LONG).show()
            }
        }, LinearLayout.LayoutParams(-1, -2).apply { topMargin = 18 })
        layout.addView(TextView(this).apply {
            text = "Demo keys:\n1 hour: $key1h\nPermanent: $keyPermanent\n\nأكواد تجريبية فقط؛ ليست حماية آمنة."
            textSize = 12f; setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
            setPadding(0, 20, 0, 0)
        })
        setContentView(layout)
    }

    private fun showHome() {
        val layout = root()
        layout.addView(title("ZOMA XIT", 34f))
        layout.addView(TextView(this).apply {
            text = "PROXY • GAMING • TOOLS"; textSize = 12f
            setTextColor(Color.LTGRAY); gravity = Gravity.CENTER
            setPadding(0, 0, 0, 24)
        })
        layout.addView(button("ENTR  •  OPEN HAPP PROXY") {
            if (!launchPackage("com.happproxy", "Happ Proxy")) {
                Toast.makeText(this, "Install Happ first: https://play.google.com/store/apps/details?id=com.happproxy", Toast.LENGTH_LONG).show()
                try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.happproxy"))) } catch (_: Exception) {}
            }
        }, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 12 })
        layout.addView(button("IMPORT CONFIG FILE TO ENTR") {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "application/octet-stream", "text/plain"))
            }
            startActivityForResult(intent, pickFile)
        }, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 12 })
        layout.addView(button("free fire") {
            if (!launchPackage("com.dts.freefireth", "Free Fire")) {
                if (!launchPackage("com.dts.freefiremax", "Free Fire MAX")) {
                    Toast.makeText(this, "Free Fire / Free Fire MAX is not installed.", Toast.LENGTH_LONG).show()
                }
            }
        }, LinearLayout.LayoutParams(-1, 58).apply { bottomMargin = 12 })
        layout.addView(button("Change activation code") {
            prefs.edit().clear().apply(); showActivation()
        }, LinearLayout.LayoutParams(-1, 54))
        layout.addView(TextView(this).apply {
            text = "\nاختيار الملف يفتح مسار الاستيراد المتاح على جهازك.\nقد تحتاج استيراده يدويًا داخل Happ. ملف تجريبي لا يتصل بسيرفر."
            textSize = 12f; setTextColor(Color.GRAY); gravity = Gravity.CENTER
        })
        setContentView(layout)
    }

    @Deprecated("Deprecated in Android API, retained for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickFile && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/json")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                startActivity(viewIntent)
            } catch (_: Exception) {
                if (!launchPackage("com.happproxy", "Happ Proxy")) {
                    Toast.makeText(this, "تعذر فتح الملف في Happ. افتح Happ واستورد الملف من داخله.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "تم فتح Happ. إذا لم يظهر الملف، استورده من داخل التطبيق.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun launchPackage(pkg: String, label: String): Boolean {
        val intent = packageManager.getLaunchIntentForPackage(pkg)
        return if (intent != null) {
            try { startActivity(intent); true }
            catch (_: Exception) { Toast.makeText(this, "Unable to open $label", Toast.LENGTH_LONG).show(); false }
        } else false
    }
}
