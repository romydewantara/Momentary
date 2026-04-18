package com.example.momentary.ui.screen.onboarding.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.momentary.ui.screen.onboarding.model.getOnboardingContent
import kotlin.math.absoluteValue

@Composable
fun OnboardingPage(
    page: Int,
    pageOffset: Float,
    isLandscape: Boolean
) {
    val content = getOnboardingContent(page)
    val absOffset = pageOffset.absoluteValue

    val scale = 1f - (0.2f * absOffset.coerceIn(0f, 1f))
    val alpha = 1f - (0.5f * absOffset.coerceIn(0f, 1f))

    val baseModifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .graphicsLayer {
            translationX = pageOffset * size.width
            scaleX = scale
            scaleY = scale
            this.alpha = alpha
        }

    if (isLandscape) {
        Row(
            modifier = baseModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = content.image),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(content.title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(content.desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    } else {
        Column(
            modifier = baseModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = content.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(content.title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(content.desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
