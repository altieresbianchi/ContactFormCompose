package br.com.phaneronsoft.contactform.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.phaneronsoft.contactform.R
import br.com.phaneronsoft.contactform.component.AppButton
import br.com.phaneronsoft.contactform.component.AppButtonSize
import br.com.phaneronsoft.contactform.component.AppButtonStyle
import br.com.phaneronsoft.contactform.component.SelectOptionsField
import br.com.phaneronsoft.contactform.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormScreen(modifier: Modifier = Modifier) {
    // ViewModel
    val viewModel: ContactViewModel = viewModel()
    val formState by viewModel.formState.collectAsState()


    val snackBarHostState = remember { SnackbarHostState() }
    var lastSnackBarIsSuccess by remember { mutableStateOf(false) }
    val successMessage = stringResource(R.string.contact_submit_success)
    val errorMessage = stringResource(R.string.contact_submit_error)

    LaunchedEffect(viewModel, successMessage, errorMessage) {
        viewModel.events.collect { event ->
            when (event) {
                is ContactUiEvent.SubmitSuccess -> {
                    lastSnackBarIsSuccess = true
                    snackBarHostState.showSnackbar(
                        message = successMessage,
                        duration = SnackbarDuration.Short,
                    )
                }

                is ContactUiEvent.SubmitError -> {
                    lastSnackBarIsSuccess = false
                    snackBarHostState.showSnackbar(
                        message = errorMessage,
                        duration = SnackbarDuration.Short,
                    )
                }
            }
        }
    }

    // Map typed validation errors to localized strings
    val nameErrorText = when (formState.errors.name) {
        ValidationError.Required -> stringResource(R.string.error_name_required)
        null -> null
        else -> null
    }
    val emailErrorText = when (formState.errors.email) {
        ValidationError.Required -> stringResource(R.string.error_email_required)
        ValidationError.InvalidEmail -> stringResource(R.string.error_email_invalid)
        null -> null
    }
    val subjectErrorText = when (formState.errors.subject) {
        ValidationError.Required -> stringResource(R.string.error_subject_required)
        null -> null
        else -> null
    }
    val messageErrorText = when (formState.errors.message) {
        ValidationError.Required -> stringResource(R.string.error_message_required)
        null -> null
        else -> null
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (lastSnackBarIsSuccess) AppColor.Green else AppColor.RedStrong,
                    contentColor = AppColor.White,
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.contact_form_screen),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                    )
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .imePadding()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                stringResource(R.string.contact_info_screen),
                modifier = Modifier.align(Alignment.Start),
                style = MaterialTheme.typography.bodyLarge,
                color = AppColor.SecondaryText,
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = formState.form.name,
                onValueChange = { newValue ->
                    if (newValue.length <= 60) {
                        viewModel.onNameChanged(newValue)
                    }
                },
                label = { Text(stringResource(R.string.label_name)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                isError = nameErrorText != null,
                supportingText = if (nameErrorText != null) {
                    { Text(nameErrorText) }
                } else null,
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = formState.form.email,
                onValueChange = { newValue ->
                    if (newValue.length <= 100) {
                        viewModel.onEmailChanged(newValue)
                    }
                },
                label = { Text(stringResource(R.string.label_email)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailErrorText != null,
                supportingText = if (emailErrorText != null) {
                    { Text(emailErrorText) }
                } else null,
            )
            Spacer(modifier = Modifier.height(4.dp))
            key(formState.form.subject) {
                SelectOptionsField(
                    label = stringResource(R.string.contact_label_subject),
                    options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
                    onOptionSelected = viewModel::onSubjectChanged,
                )
            }
            if (subjectErrorText != null) {
                Text(
                    text = subjectErrorText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.form.message,
                onValueChange = { newValue ->
                    if (newValue.length <= 1024) {
                        viewModel.onMessageChanged(newValue)
                    }
                },
                label = { Text(stringResource(R.string.label_message)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                maxLines = 5,
                minLines = 2,
                singleLine = false,
                isError = messageErrorText != null,
                supportingText = if (messageErrorText != null) {
                    { Text(messageErrorText) }
                } else null,
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                label = stringResource(R.string.label_send),
                fullWidth = true,
                style = AppButtonStyle.Solid,
                textColor = AppColor.White,
                buttonColor = AppColor.Primary,
                buttonSize = AppButtonSize.Normal,
                enabled = formState.canSubmit && !formState.isSubmitting,
                onClick = viewModel::onSubmit,
            )
        }
    }
}

@Preview("ContactScreenPreview")
@Composable
private fun ContactScreenPreview() {
    ContactFormScreen(modifier = Modifier)
}