package com.example.momentary.ui.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.momentary.R
import com.example.momentary.ui.common.Avatar

@Composable
fun AppDrawerHeader(
    modifier: Modifier = Modifier,
    name: String,
    email: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Avatar(
            name = name,
            isOwnStory = true
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Black
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Image(
                    painter = painterResource(R.drawable.ic_certified),
                    contentDescription = stringResource(R.string.cd_story),
                    modifier = Modifier
                        .size(16.dp)
                        .offset(y = (-1).dp)
                )
            }

            Text(
                text = email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
