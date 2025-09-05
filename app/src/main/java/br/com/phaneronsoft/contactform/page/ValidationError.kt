package br.com.phaneronsoft.contactform.page

sealed interface ValidationError {
    data object Required : ValidationError
    data object InvalidEmail : ValidationError
}
