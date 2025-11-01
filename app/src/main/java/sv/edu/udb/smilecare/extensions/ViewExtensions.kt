package sv.edu.udb.smilecare.extensions

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.toggleVisibility() {
    this.visibility = if (this.isVisible) View.GONE else View.VISIBLE
}

fun EditText.isEmpty(): Boolean {
    return this.text.toString().trim().isEmpty()
}

fun EditText.getTextTrimmed(): String {
    return this.text.toString().trim()
}

fun TextView.getTextTrimmed(): String {
    return this.text.toString().trim()
}

fun View.setDebouncedClickListener(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (System.currentTimeMillis() - lastClickTime < debounceTime) return
            lastClickTime = System.currentTimeMillis()
            action()
        }
    })
}

fun View.fadeIn(duration: Long = 300L) {
    this.alpha = 0f
    this.show()
    this.animate()
        .alpha(1f)
        .setDuration(duration)
        .start()
}

fun View.fadeOut(duration: Long = 300L) {
    this.animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction { this.hide() }
        .start()
}