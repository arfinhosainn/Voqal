package app.voqal.com.feature.chat.presentation.model

import androidx.compose.ui.unit.Dp

data class TransformingSheetTransition(
    val progress: Float,
    val sheetHeight: Dp,
    val cornerRadius: Dp,
    val elevation: Dp,
    
    val roomBar: MotionState,
    val header: MotionState,
    val messages: MotionState,
    val input: MotionState,
    val dragHandle: MotionState
)
