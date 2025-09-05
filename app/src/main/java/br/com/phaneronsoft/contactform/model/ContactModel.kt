package br.com.phaneronsoft.contactform.model

data class ContactModel(
    val name: String,
    val email: String,
    val subject: String,
    val message: String,
)