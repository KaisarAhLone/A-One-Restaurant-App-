package com.example.aonerestaurant.ui.screen.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.aonerestaurant.R

@Composable
fun HomeHeaderBar(
    cartCount: Int,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ownerPhoneNumber = "+919871931413"

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(473f / 290f)
    ) {
        val width = maxWidth
        val height = maxHeight

        // 1. High-resolution authentic banner graphic
        Image(
            painter = painterResource(id = R.drawable.home_top_banner),
            contentDescription = "A One Biryani Header",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Interactive Avatar Button (Top-Left)
        Box(
            modifier = Modifier
                .offset(
                    x = width * (18f / 473f),
                    y = height * (8f / 290f)
                )
                .size(width * (50f / 473f))
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White)
                ) {
                    onProfileClick()
                }
        )

        // 4. Interactive Cart Button (Top-Right)
        Box(
            modifier = Modifier
                .offset(
                    x = width * (420f / 473f),
                    y = height * (10f / 290f)
                )
                .size(width * (42f / 473f))
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White)
                ) {
                    onCartClick()
                }
        )

        // 5. Dynamic Cart Badge
        if (cartCount == 0) {
            Box(
                modifier = Modifier
                    .offset(
                        x = width * (441f / 473f),
                        y = height * (11f / 290f)
                    )
                    .size(width * (20f / 473f))
                    .clip(CircleShape)
                    .background(Color(0xFF2E0509))
            )
        } else {
            Box(
                modifier = Modifier
                    .offset(
                        x = width * (440f / 473f),
                        y = height * (10f / 290f)
                    )
                    .size(width * (20f / 473f))
                    .clip(CircleShape)
                    .background(Color(0xFFE60023)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = cartCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 6. Interactive "Call Owner" Pill Button - Solid Vibrant Emerald Green
        Box(
            modifier = Modifier
                .offset(
                    x = width * (36f / 473f),
                    y = height * (214f / 290f)
                )
                .size(
                    width = width * (195f / 473f),
                    height = height * (48f / 290f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF00E676))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White)
                ) {
                    val intent = Intent(Intent.ACTION_DIAL, "tel:$ownerPhoneNumber".toUri())
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call Owner",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Call Owner",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        // 7. Interactive "WhatsApp" Pill Button - Solid Vibrant Neon Green
        Box(
            modifier = Modifier
                .offset(
                    x = width * (242f / 473f),
                    y = height * (214f / 290f)
                )
                .size(
                    width = width * (195f / 473f),
                    height = height * (48f / 290f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF25D366))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White)
                ) {
                    val url = "https://api.whatsapp.com/send?phone=${ownerPhoneNumber.replace("+", "")}&text=Hello%20A%20One%20Biryani!%20I%20want%20to%20order%20food."
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "WhatsApp Owner",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "WhatsApp",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}
