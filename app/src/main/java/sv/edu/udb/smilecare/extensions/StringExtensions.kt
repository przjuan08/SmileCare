package sv.edu.udb.smilecare.extensions

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
    return this.matches(emailRegex.toRegex())
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^[27]\\d{3}-\\d{4}\$"
    return this.matches(phoneRegex.toRegex())
}

fun String.formatPhone(): String {
    return if (this.length == 8 && this.all { it.isDigit() }) {
        "${this.substring(0, 4)}-${this.substring(4)}"
    } else {
        this
    }
}

fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        if (word.length > 1) {
            word.substring(0, 1).uppercase() + word.substring(1).lowercase()
        } else {
            word.uppercase()
        }
    }
}