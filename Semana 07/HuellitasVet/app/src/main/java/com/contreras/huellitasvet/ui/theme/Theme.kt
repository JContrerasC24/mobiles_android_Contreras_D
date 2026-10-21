package com.contreras.huellitasvet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contreras.huellitasvet.model.Rol

private fun claro(vet: Boolean) = lightColorScheme(
    primary = Color(if(vet) 0xFF4556C9 else 0xFF4962DE), onPrimary = Color.White,
    primaryContainer = Color(if(vet) 0xFFE6E9FF else 0xFFE7EDFF),
    onPrimaryContainer = Color(if(vet) 0xFF34439C else 0xFF344AA7),
    background = Color(if(vet) 0xFFF2F3FB else 0xFFF0F4FF),
    onBackground = Color(0xFF172131), surface = Color.White, onSurface = Color(0xFF172131),
    surfaceVariant = Color(if(vet) 0xFFEAECFA else 0xFFE8EEFC), onSurfaceVariant = Color(0xFF627083),
    outline = Color(0xFFD9DFE6), outlineVariant = Color(0xFFE7EBEF),
    secondary = Color(0xFF64748B), secondaryContainer = Color(0xFFE2E8F0),
    onSecondaryContainer = Color(0xFF475569), tertiary = Color(0xFFD97706),
    tertiaryContainer = Color(0xFFFFF3C4), onTertiaryContainer = Color(0xFF92400E),
    error = Color(0xFFDC2626), errorContainer = Color(0xFFFEE2E2), onErrorContainer = Color(0xFF991B1B)
)
private fun oscuro(vet:Boolean) = darkColorScheme(
    primary=Color(if(vet)0xFFBAC2FF else 0xFFB4C5FF),onPrimary=Color(0xFF17254E),
    primaryContainer=Color(if(vet)0xFF303A72 else 0xFF2B3E75),onPrimaryContainer=Color(0xFFE5F3FF),
    background=Color(0xFF101820),surface=Color(0xFF1A2530),onSurface=Color(0xFFE8EEF5),
    surfaceVariant=Color(0xFF253441),onSurfaceVariant=Color(0xFFACB8C6),
    outline=Color(0xFF495969),outlineVariant=Color(0xFF334353),
    tertiary=Color(0xFFFBBF24),tertiaryContainer=Color(0xFF483714),onTertiaryContainer=Color(0xFFFDE68A)
)
private val VetTypography = Typography(
    headlineLarge=TextStyle(fontFamily=FontFamily.SansSerif,fontWeight=FontWeight.Bold,fontSize=28.sp,lineHeight=34.sp),
    headlineMedium=TextStyle(fontWeight=FontWeight.Bold,fontSize=27.sp,lineHeight=34.sp),
    titleLarge=TextStyle(fontWeight=FontWeight.SemiBold,fontSize=18.sp,lineHeight=24.sp),
    titleMedium=TextStyle(fontWeight=FontWeight.SemiBold,fontSize=15.sp,lineHeight=21.sp),
    titleSmall=TextStyle(fontWeight=FontWeight.SemiBold,fontSize=13.sp,lineHeight=18.sp),
    bodyLarge=TextStyle(fontSize=15.sp,lineHeight=22.sp),
    bodyMedium=TextStyle(fontSize=14.sp,lineHeight=21.sp),
    bodySmall=TextStyle(fontSize=12.sp,lineHeight=18.sp),
    labelLarge=TextStyle(fontWeight=FontWeight.SemiBold,fontSize=13.sp,lineHeight=18.sp),
    labelMedium=TextStyle(fontWeight=FontWeight.Medium,fontSize=11.sp,lineHeight=15.sp),
    labelSmall=TextStyle(fontSize=10.sp,lineHeight=14.sp)
)
@Composable
fun VetTheme(rol:Rol,darkTheme:Boolean=isSystemInDarkTheme(),content:@Composable ()->Unit) {
    MaterialTheme(colorScheme=if(darkTheme)oscuro(rol==Rol.ADMIN) else claro(rol==Rol.ADMIN),
        typography=VetTypography,shapes=Shapes(small=RoundedCornerShape(10.dp),medium=RoundedCornerShape(22.dp),
            large=RoundedCornerShape(28.dp)),content=content)
}
@Composable
fun HuellitasVetTheme(darkTheme:Boolean=isSystemInDarkTheme(),content:@Composable ()->Unit) {
    VetTheme(Rol.CLIENTE,darkTheme,content)
}
