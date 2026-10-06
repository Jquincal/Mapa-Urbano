package com.mapaurbano.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.User
import com.mapaurbano.app.feature.common.MuniLogo
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchPrimaryButton

@Composable
fun LoginScreen(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
    onForgotPassword: () -> Unit = {},
    onUnavailableFeature: (String) -> Unit = {},
) {
    ScrollableFeatureScreen(title = "Iniciar sesión", modifier = modifier, onBack = onBack) {
        AuthHero("Bienvenido a MuniReport", "Ingresá para guardar reportes y seguir su avance.")
        message?.let { AuthNotice(it) }
        StitchCard {
            AuthField(email, onEmailChange, "Correo electrónico", Icons.Rounded.Email, ContentType.EmailAddress)
            AuthField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                icon = Icons.Rounded.Lock,
                contentType = ContentType.Password,
                password = true,
                keyboardActions = KeyboardActions(onDone = { onLogin() }),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = true, onCheckedChange = {})
                Text("Recordarme", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onForgotPassword) { Text("Olvidé mi contraseña") }
            }
            StitchPrimaryButton(text = "Ingresar", onClick = onLogin, enabled = email.isNotBlank() && password.isNotBlank())
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("  o continúa con  ", style = MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { onUnavailableFeature("Acceso con Google") },
                    modifier = Modifier.weight(1f).sizeIn(minHeight = 48.dp),
                ) {
                    Icon(Icons.Rounded.AccountBox, contentDescription = null)
                    Text("Google", modifier = Modifier.padding(start = 6.dp))
                }
                OutlinedButton(
                    onClick = { onUnavailableFeature("Acceso biométrico") },
                    modifier = Modifier.weight(1f).sizeIn(minHeight = 48.dp),
                ) {
                    Icon(Icons.Rounded.Fingerprint, contentDescription = null)
                    Text("Biometría", modifier = Modifier.padding(start = 6.dp))
                }
            }
            TextButton(onClick = onRegister, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("¿No tenés cuenta? Registrate")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    displayName: String,
    email: String,
    password: String,
    confirmPassword: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onBack: () -> Unit,
) {
    val passwordsDiffer = confirmPassword.isNotEmpty() && password != confirmPassword
    ScrollableFeatureScreen(title = "Crear cuenta", modifier = modifier, onBack = onBack) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(MuniColors.PrimaryFixed, RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.Groups, contentDescription = null, tint = MuniColors.Primary, modifier = Modifier.size(18.dp))
            Text(
                "COMUNIDAD ACTIVA",
                modifier = Modifier.padding(start = 6.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MuniColors.Primary,
            )
        }
        AuthHero("Únete a MuniReport", "Tu cuenta permite consultar Mis reportes sin guardar códigos manualmente.")
        message?.let { AuthNotice(it) }
        StitchCard {
            AuthField(displayName, onDisplayNameChange, "Nombre y apellido", Icons.Rounded.Person, ContentType.PersonFullName)
            AuthField(email, onEmailChange, "Correo electrónico", Icons.Rounded.Email, ContentType.EmailAddress)
            AuthField(password, onPasswordChange, "Contraseña", Icons.Rounded.Lock, ContentType.NewPassword, password = true)
            AuthField(
                confirmPassword,
                onConfirmPasswordChange,
                "Repetir contraseña",
                Icons.Rounded.Lock,
                ContentType.NewPassword,
                password = true,
                isError = passwordsDiffer,
            )
            UnavailableIdentityField("DNI / Identificador cívico")
            UnavailableIdentityField("Distrito de residencia")
        }
        StitchPrimaryButton(
            text = "Crear mi cuenta ciudadana",
            onClick = onRegister,
            enabled = displayName.isNotBlank() && email.isNotBlank() && password.length >= 8 && password == confirmPassword,
        )
    }
}

@Composable
fun ProfileScreen(
    user: User,
    modifier: Modifier = Modifier,
    message: String? = null,
    onBack: () -> Unit,
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onViewReports: () -> Unit = {},
    onLogout: () -> Unit,
    onDeactivateAccount: () -> Unit = {},
) {
    ScrollableFeatureScreen(title = "Mi perfil", modifier = modifier, onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuniColors.PrimaryFixed, RoundedCornerShape(22.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(MuniColors.SurfaceContainerLowest, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Person, contentDescription = "Foto de perfil", tint = MuniColors.Primary, modifier = Modifier.size(48.dp))
            }
            Text(user.displayName, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(user.email, color = MuniColors.OnSurfaceVariant)
            Text("Cuenta Ciudadana Demo", modifier = Modifier.background(MuniColors.SurfaceContainer, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp))
        }
        message?.let { AuthNotice(it) }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatCard("Reportes", "3", Modifier.weight(1f))
            StatCard("Resueltos", "1", Modifier.weight(1f))
        }
        StitchCard {
            ProfileRow(Icons.Rounded.Badge, "Datos personales", user.displayName, onEditProfile)
            ProfileRow(Icons.Rounded.Place, "Distrito", "No requerido", onEditProfile)
            ProfileRow(Icons.Rounded.Lock, "Seguridad", "Cambiar contraseña", onChangePassword)
        }
        StitchCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = MuniColors.Primary)
                Text("Notificaciones de avance", modifier = Modifier.weight(1f).padding(start = 10.dp))
                Switch(checked = true, onCheckedChange = {})
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.History, contentDescription = null, tint = MuniColors.Primary)
                TextButton(onClick = onViewReports, modifier = Modifier.weight(1f)) { Text("Ver historial reciente") }
            }
        }
        OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp)) {
            Text("Cerrar sesión")
        }
        TextButton(onClick = onDeactivateAccount, modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp)) {
            Icon(Icons.Rounded.PersonOff, contentDescription = null)
            Text("Desactivar cuenta", color = MuniColors.Error, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun AuthHero(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 14.dp)) {
        Box(modifier = Modifier.size(86.dp).background(MuniColors.PrimaryFixed, CircleShape), contentAlignment = Alignment.Center) {
            MuniLogo(Modifier.size(52.dp))
        }
        Text(title, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.semantics { heading() })
        Text(subtitle, color = MuniColors.OnSurfaceVariant)
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentType: ContentType,
    password: Boolean = false,
    isError: Boolean = false,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().semantics { this.contentType = contentType },
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        singleLine = true,
        isError = isError,
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (contentType == ContentType.EmailAddress) KeyboardType.Email else KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = keyboardActions,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MuniColors.SurfaceContainerLow,
            unfocusedContainerColor = MuniColors.SurfaceContainerLow,
            focusedBorderColor = MuniColors.OutlineVariant,
            unfocusedBorderColor = MuniColors.OutlineVariant,
        ),
    )
}

@Composable
private fun AuthNotice(text: String) {
    StitchCard(containerColor = MuniColors.Orange100) {
        Text(text, color = MuniColors.Orange700)
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    StitchCard(modifier = modifier, containerColor = MuniColors.SurfaceContainerLowest) {
        Text(label.uppercase(), style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
        Text(value, style = androidx.compose.material3.MaterialTheme.typography.headlineSmall, color = MuniColors.Primary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun UnavailableIdentityField(label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MuniColors.OnSurfaceVariant)
            Text("NO REQUERIDO", style = MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MuniColors.SurfaceContainerLow, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 13.dp),
        ) {
            Text(
                "No requerido en esta versión",
                style = MaterialTheme.typography.bodySmall,
                color = MuniColors.OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MuniColors.Primary)
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(value, color = MuniColors.OnSurfaceVariant)
        }
        TextButton(onClick = onClick) { Text("Editar") }
    }
}
