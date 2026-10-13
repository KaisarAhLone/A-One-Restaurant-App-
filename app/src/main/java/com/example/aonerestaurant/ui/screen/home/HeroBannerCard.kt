package com.example.aonerestaurant.ui.screen.home

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aonerestaurant.R
import com.example.aonerestaurant.data.model.FoodItem

@Composable
fun HeroBannerCard(
    specialItem: FoodItem?,
    onOrderNowClick: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemToOrder = specialItem ?: FoodItem(
        id = "1",
        name = "Chicken Biryani",
        price = 120.0,
        description = "Rich Flavour, Real Taste",
        categoryId = "1",
        rating = 4.9,
        isPopular = true,
        isTodaysSpecial = true
    )

    val itemPriceInt = itemToOrder.price.toInt()
    val origPriceInt = (itemPriceInt * 1.25).toInt()

    // Determine discount percentage — show 20% if price > 0, else hide
    val discountPct = if (itemPriceInt > 0 && origPriceInt > itemPriceInt) {
        val pct = ((origPriceInt - itemPriceInt).toDouble() / origPriceInt * 100).toInt()
        pct
    } else 0

    val imageUri = itemToOrder.imageUri

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .clickable { onOrderNowClick(itemToOrder) },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            // ── 1. Full-bleed background image ──────────────────────────────
            when {
                // base64 data URI
                !imageUri.isNullOrBlank() && imageUri.startsWith("data:image") -> {
                    val bitmap = remember(imageUri) {
                        try {
                            val b64 = imageUri.substringAfter("base64,")
                            val bytes = Base64.decode(b64, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        } catch (_: Exception) { null }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = itemToOrder.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        FallbackBg()
                    }
                }
                // http/https or content URI via Coil
                !imageUri.isNullOrBlank() && !imageUri.startsWith("res:") -> {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = itemToOrder.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // drawable resource or nothing → use static biryani photo
                else -> {
                    Image(
                        painter = painterResource(id = R.drawable.special_offer_biryani),
                        contentDescription = "Today's Special",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // ── 2. Left-dark → right-transparent gradient scrim ─────────────
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.00f to Color(0xFF1A0203).copy(alpha = 0.96f),
                                0.38f to Color(0xFF1A0203).copy(alpha = 0.88f),
                                0.55f to Color(0xFF1A0203).copy(alpha = 0.55f),
                                0.72f to Color(0xFF1A0203).copy(alpha = 0.12f),
                                1.00f to Color.Transparent
                            )
                        )
                    )
            )

            // ── 3. Left-side text content ────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // 👑 Today's Special Offer gold pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF9B828))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 9.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Today's Special Offer",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E0204)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Item name — first word white, rest gold
                    val nameParts = itemToOrder.name.split(" ", limit = 2)
                    if (nameParts.size >= 2) {
                        Text(
                            text = nameParts[0],
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 23.sp
                        )
                        Text(
                            text = nameParts[1],
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFF9B828),
                            lineHeight = 23.sp
                        )
                    } else {
                        Text(
                            text = itemToOrder.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            lineHeight = 23.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = itemToOrder.description.ifBlank { "Rich Flavour, Real Taste" },
                        fontSize = 10.5.sp,
                        color = Color(0xFFDCC8C8),
                        maxLines = 1
                    )
                }

                // Price row: [₹120] ₹150̶ [20% OFF]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (itemPriceInt > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF9B828))
                                .padding(horizontal = 8.dp, vertical = 3.5.dp)
                        ) {
                            Text(
                                text = "₹$itemPriceInt",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E0204)
                            )
                        }

                        Text(
                            text = "₹$origPriceInt",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA89090),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }

                    if (discountPct > 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFD90429))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$discountPct% OFF",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // ── 4. Carousel dots (bottom-center) ────────────────────────────
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 10.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.45f))
                )
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.45f))
                )
            }
        }
    }
}

/** Dark burgundy solid fallback when image fails */
@Composable
private fun FallbackBg() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF280306),
                        Color(0xFF480B10),
                        Color(0xFF240205)
                    )
                )
            )
    )
}
