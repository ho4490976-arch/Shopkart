package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberOrangeAccent
import com.example.ui.theme.IndigoNavyPrimary
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SpinSlice(val title: String, val rewardPoints: Int, val color: Color)

@Composable
fun SpinWheelCanvas(
    onRewardClaimed: (Int, String) -> Unit
) {
    val slices = remember {
        listOf(
            SpinSlice("100 Points", 100, Color(0xFF1A237E)),
            SpinSlice("500 Points", 500, Color(0xFFFF9800)),
            SpinSlice("200 Points", 200, Color(0xFF388E3C)),
            SpinSlice("Better Luck", 0, Color(0xFFD32F2F)),
            SpinSlice("1000 Points", 1000, Color(0xFF7B1FA2)),
            SpinSlice("50 Points", 50, Color(0xFF0288D1))
        )
    }

    val rotationAnim = remember { Animatable(0f) }
    var isSpinning by remember { mutableStateOf(false) }
    var winningSlice by remember { mutableStateOf<SpinSlice?>(null) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = AmberOrangeAccent)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Daily Spin & Win Wheel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = IndigoNavyPrimary
                    )
                )
            }
            Text(
                text = "Spin every 24 hours to earn free reward points & discount cash!",
                fontSize = 11.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Wheel Box Container
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Canvas Wheel
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotationAnim.value)
                ) {
                    val sweepAngle = 360f / slices.size
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2

                    slices.forEachIndexed { index, slice ->
                        drawArc(
                            color = slice.color,
                            startAngle = index * sweepAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                    }
                }

                // Center Pin Indicator
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SPIN", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = IndigoNavyPrimary)
                }

                // Top Pointer Arrow
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopCenter)
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2, size.height)
                        lineTo(0f, 0f)
                        lineTo(size.width, 0f)
                        close()
                    }
                    drawPath(path, color = Color.Yellow)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spin Action Button
            Button(
                onClick = {
                    if (isSpinning) return@Button
                    isSpinning = true
                    winningSlice = null

                    val randomTargetSliceIndex = Random.nextInt(slices.size)
                    val targetSlice = slices[randomTargetSliceIndex]
                    val extraTurns = 5 * 360f
                    val sliceAngle = 360f / slices.size
                    val targetDegree = extraTurns + (360f - (randomTargetSliceIndex * sliceAngle)) - (sliceAngle / 2)

                    scope.launch {
                        rotationAnim.snapTo(0f)
                        rotationAnim.animateTo(
                            targetValue = targetDegree,
                            animationSpec = tween(durationMillis = 3500, easing = EaseInOutCubic)
                        )
                        isSpinning = false
                        winningSlice = targetSlice
                        if (targetSlice.rewardPoints > 0) {
                            onRewardClaimed(targetSlice.rewardPoints, targetSlice.title)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberOrangeAccent),
                enabled = !isSpinning
            ) {
                Text(
                    text = if (isSpinning) "SPINNING WHEEL..." else "TAP TO SPIN WHEEL",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            // Reward Message
            winningSlice?.let { slice ->
                Spacer(modifier = Modifier.height(12.dp))
                if (slice.rewardPoints > 0) {
                    Text(
                        text = "🎉 Congratulations! You won ${slice.title}!",
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Better luck next time! Try again tomorrow.",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
