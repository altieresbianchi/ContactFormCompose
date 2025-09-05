package br.com.phaneronsoft.contactform.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.phaneronsoft.contactform.model.ContactModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Random

class ContactViewModel() : ViewModel() {
    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    // One-shot UI events (e.g., snackbar)
    private val _events = MutableSharedFlow<ContactUiEvent>()
    val events: SharedFlow<ContactUiEvent> = _events.asSharedFlow()

    // --------------- INTENTS (UI -> ViewModel) ---------------
    fun onNameChanged(newValue: String) {
        val current = _formState.value
        val updatedForm = current.form.copy(name = newValue)
        val (errors, canSubmit) = validate(updatedForm)
        _formState.value = current.copy(form = updatedForm, errors = errors, canSubmit = canSubmit)
    }

    fun onEmailChanged(newValue: String) {
        val current = _formState.value
        val updatedForm = current.form.copy(email = newValue)
        val (errors, canSubmit) = validate(updatedForm)
        _formState.value = current.copy(form = updatedForm, errors = errors, canSubmit = canSubmit)
    }

    fun onSubjectChanged(newValue: String) {
        val current = _formState.value
        val updatedForm = current.form.copy(subject = newValue)
        val (errors, canSubmit) = validate(updatedForm)
        _formState.value = current.copy(form = updatedForm, errors = errors, canSubmit = canSubmit)
    }

    fun onMessageChanged(newValue: String) {
        val current = _formState.value
        val updatedForm = current.form.copy(message = newValue)
        val (errors, canSubmit) = validate(updatedForm)
        _formState.value = current.copy(form = updatedForm, errors = errors, canSubmit = canSubmit)
    }

    fun onSubmit() {
        val current = _formState.value
        val (errors, canSubmit) = validate(current.form)
        if (!canSubmit) {
            _formState.value = current.copy(errors = errors, canSubmit = false)
            return
        }
        _formState.value = current.copy(isSubmitting = true)

        val contactModel = ContactModel(
            name = current.form.name,
            email = current.form.email,
            subject = current.form.subject,
            message = current.form.message,
        )
        viewModelScope.launch {
            try {
                // TODO: Call your use case or repository
                //val result = submitContactUseCase.invoke(contactModel)

                // Pegar o valor booleano como randomico
                val result = Random().nextBoolean()
                if (result) {
                    _events.emit(ContactUiEvent.SubmitSuccess)
                    // Clear form on success
                    _formState.value = FormState()
                } else {
                    _events.emit(ContactUiEvent.SubmitError())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.emit(ContactUiEvent.SubmitError(e))
            } finally {
                // Reset submitting state
                _formState.value = _formState.value.copy(isSubmitting = false)
            }
        }
    }

    // --------------- VALIDAÇÃO ---------------
    private fun validate(form: ContactForm): Pair<ContactErrors, Boolean> {
        val nameError = if (form.name.isBlank()) ValidationError.Required else null
        val emailError = when {
            form.email.isBlank() -> ValidationError.Required
            !EMAIL_REGEX.matches(form.email.trim()) -> ValidationError.InvalidEmail
            else -> null
        }
        val subjectError = if (form.subject.isBlank()) ValidationError.Required else null
        val messageError = if (form.message.isBlank()) ValidationError.Required else null

        val errors = ContactErrors(
            name = nameError,
            email = emailError,
            subject = subjectError,
            message = messageError,
        )
        val canSubmit = listOf(nameError, emailError, subjectError, messageError).all { it == null }
        return errors to canSubmit
    }

    companion object {
        private val EMAIL_REGEX = Regex(
            pattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            option = RegexOption.IGNORE_CASE
        )
    }
}

// ------------ UI STATE PARA FORMULÁRIO ------------
data class FormState(
    val form: ContactForm = ContactForm(),
    val errors: ContactErrors = ContactErrors(),
    val canSubmit: Boolean = false,
    val isSubmitting: Boolean = false,
)

data class ContactForm(
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val message: String = "",
)

data class ContactErrors(
    val name: ValidationError? = null,
    val email: ValidationError? = null,
    val subject: ValidationError? = null,
    val message: ValidationError? = null,
)

// One-time UI events for Contact screen
sealed interface ContactUiEvent {
    data object SubmitSuccess : ContactUiEvent
    data class SubmitError(val throwable: Throwable? = null) : ContactUiEvent
}