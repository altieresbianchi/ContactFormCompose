package br.com.phaneronsoft.contactform.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.phaneronsoft.contactform.ui.theme.AppColor

@Composable
fun AppButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Solid,
    buttonSize: AppButtonSize = AppButtonSize.Normal,
    buttonColor: Color = AppColor.Primary,
    textColor: Color = AppColor.White,
    fullWidth: Boolean = true,
    enabled: Boolean = true,
) {
    val baseModifier = if (fullWidth) modifier.fillMaxWidth() else modifier
    val sizedModifier = baseModifier.height(buttonSize.height)

    when (style) {
        AppButtonStyle.Solid -> {
            Button(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor,
                    disabledContainerColor = buttonColor.copy(alpha = 0.38f),
                    disabledContentColor = textColor.copy(alpha = 0.38f),
                ),
            ) {
                Text(
                    text = label,
                    style = when (buttonSize) {
                        AppButtonSize.Mini -> MaterialTheme.typography.bodyMedium
                        AppButtonSize.Large -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    },
                    fontWeight = FontWeight.W600
                )
            }
        }

        AppButtonStyle.Outline -> {
            OutlinedButton(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, buttonColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = textColor,
                    disabledContentColor = textColor.copy(alpha = 0.38f),
                ),
            ) {
                Text(
                    text = label,
                    style = when (buttonSize) {
                        AppButtonSize.Mini -> MaterialTheme.typography.bodyMedium
                        AppButtonSize.Large -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    },
                    fontWeight = FontWeight.W600
                )
            }
        }

        AppButtonStyle.Text -> {
            TextButton(
                onClick = onClick,
                modifier = sizedModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = textColor,
                    disabledContentColor = textColor.copy(alpha = 0.38f),
                ),
            ) {
                Text(
                    text = label,
                    style = when (buttonSize) {
                        AppButtonSize.Mini -> MaterialTheme.typography.bodyMedium
                        AppButtonSize.Large -> MaterialTheme.typography.titleLarge
                        else -> MaterialTheme.typography.titleMedium
                    },
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

enum class AppButtonStyle { Solid, Outline, Text }

enum class AppButtonSize(val height: Dp) {
    Mini(36.dp),
    Normal(44.dp),
    Large(56.dp);
}

// -------------------- PREVIEWS --------------------
@Preview(showBackground = true)
@Composable
private fun AppButtonSolidPreview() {
    AppButton(
        label = "Continuar",
        onClick = {},
        style = AppButtonStyle.Solid,
        buttonColor = AppColor.Primary,
        textColor = AppColor.White,
        buttonSize = AppButtonSize.Normal,
        fullWidth = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppButtonOutlinePreview() {
    AppButton(
        label = "Cancelar",
        onClick = {},
        style = AppButtonStyle.Outline,
        buttonColor = AppColor.Primary,
        textColor = AppColor.Primary,
        buttonSize = AppButtonSize.Mini,
        fullWidth = false,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppButtonTextPreview() {
    AppButton(
        label = "Pular",
        onClick = {},
        style = AppButtonStyle.Text,
        textColor = MaterialTheme.colorScheme.primary,
        buttonSize = AppButtonSize.Large,
    )
}