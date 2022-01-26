package com.cooper.wordle.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun BottomSheet(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Divider(
                modifier = Modifier
                    .width(24.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(1.dp)),
                thickness = 4.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            content()
        }
    }
}