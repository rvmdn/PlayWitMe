package com.playwithme.assistant.core.overlay.components

import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playwithme.assistant.core.state.ScanCommand
import com.playwithme.assistant.core.state.ScanStep
import com.playwithme.assistant.core.ui.theme.GameGold
import com.playwithme.assistant.core.ui.theme.Primary60
import kotlin.math.roundToInt

@Composable
fun ScannerOverlayPlaceholder(
    onScanClick: (ScanCommand) -> Unit,
    onCloseClick: () -> Unit,
    opacity: Float
) {
    val view = LocalView.current
    var captureRect by remember { mutableStateOf(Rect()) }
    var currentStep by remember { mutableStateOf(ScanStep.MAIN_INFO) }

    // Outer container: Completely transparent
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Scanner Bounding Box - takes up most space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(2.dp, GameGold) // The reticle
                    .background(Color.Transparent)
                    .onGloballyPositioned { layoutCoordinates ->
                        val location = IntArray(2)
                        view.getLocationOnScreen(location)
                        val pos = layoutCoordinates.positionInRoot()
                        
                        val x = location[0] + pos.x.roundToInt()
                        val y = location[1] + pos.y.roundToInt()
                        val w = layoutCoordinates.size.width
                        val h = layoutCoordinates.size.height
                        
                        // We capture whatever is inside this yellow border
                        captureRect = Rect(x, y, x + w, y + h)
                    }
            ) {
                // Step Indicator at top-left of the bounding box
                Text(
                    text = " ${currentStep.name} ",
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.background(GameGold)
                )
            }
            
            // Minimal Bottom Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("X", fontSize = 12.sp)
                }
                
                Button(
                    onClick = { 
                        onScanClick(ScanCommand(currentStep, captureRect))
                        
                        // Advance step
                        currentStep = when(currentStep) {
                            ScanStep.MAIN_INFO -> ScanStep.SKILL_1
                            ScanStep.SKILL_1 -> ScanStep.SKILL_2
                            ScanStep.SKILL_2 -> ScanStep.SKILL_3
                            ScanStep.SKILL_3 -> ScanStep.ULTIMATE
                            ScanStep.ULTIMATE -> ScanStep.MAIN_INFO
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GameGold.copy(alpha = 0.9f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("SCAN: ${currentStep.name}", color = Color.Black, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
        }
    }
}
