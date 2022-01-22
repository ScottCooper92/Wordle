package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun TestScreen() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

    // Create references for the composables to constrain
    val (grid, keyboard) = createRefs()
    val guidelineBottom = createGuidelineFromBottom(0.01f)

    Box(
        modifier = Modifier
            .background(Color.Red)
            .constrainAs(grid) {
                linkTo(parent.start, parent.end, startMargin = 32.dp, endMargin = 32.dp)
                linkTo(
                    parent.top,
                    keyboard.top,
                    topMargin = 16.dp,
                    bottomMargin = 24.dp,
                    bias = 0f
                )

                val ratio = Dimension.ratio("5:6")
                //height = ratio
                width = ratio
                height = Dimension.fillToConstraints
            }
    )

    Box(
        modifier = Modifier
            .background(Color.Blue)
            .constrainAs(keyboard) {
                linkTo(parent.start, parent.end, startMargin = 32.dp, endMargin = 32.dp)
                linkTo(grid.bottom, guidelineBottom, bias = 1f)

                width = Dimension.matchParent
                height = Dimension.value(150.dp)
            }
    )

    }
}

@Preview
@Composable
fun PreviewTestScreen() {
    TestScreen()
}