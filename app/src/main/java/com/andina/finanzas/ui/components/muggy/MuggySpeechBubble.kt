package com.andina.finanzas.ui.components.muggy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.ui.theme.AndinaAmber
import com.andina.finanzas.ui.theme.AndinaDeepForest
import com.andina.finanzas.ui.theme.AndinaMossGreen

/**
 * Componente de Burbuja de Diálogo ilustrada de Muggy.
 * Humaniza las interacciones y diagnósticos financieros en el Consultorio y en toda la app.
 */
@Composable
fun MuggySpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    greeting: String? = null,
    keyMetric: String? = null,
    pose: MuggyPose = MuggyPose.OBSERVING,
    avatarSize: Dp = 56.dp,
    containerColor: Color = Color(0xFFFFFFFF),
    actionContent: (@Composable () -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar de Muggy a un costado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                MuggyAvatar(
                    size = avatarSize,
                    pose = pose,
                    isInteractive = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AndinaMossGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Muggy",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AndinaDeepForest
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Bocadillo de diálogo estilizado
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0xFFF9F7F2))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFEBE6DC),
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .padding(14.dp)
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B19)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                }

                if (greeting != null) {
                    Text(
                        text = greeting,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = AndinaAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = text,
                    fontSize = 13.sp,
                    color = Color(0xFF4A453E),
                    lineHeight = 19.sp
                )

                if (keyMetric != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = keyMetric,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AndinaMossGreen
                    )
                }

                if (actionContent != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    actionContent()
                }
            }
        }
    }
}
