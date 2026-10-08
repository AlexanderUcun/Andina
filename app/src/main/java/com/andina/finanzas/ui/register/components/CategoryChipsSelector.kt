package com.andina.finanzas.ui.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andina.finanzas.domain.model.Category

/**
 * Selector horizontal de categorías en chips redondeados con iconos alusivos.
 */
@Composable
fun CategoryChipsSelector(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Categoría",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                val isSelected = category.id == selectedCategoryId
                val categoryColor = com.andina.finanzas.ui.components.CategoryIconHelper.parseColor(category.colorHex)
                val icon = com.andina.finanzas.ui.components.CategoryIconHelper.getIcon(category.icon)

                val chipBg = if (isSelected) {
                    if (isDark) categoryColor.copy(alpha = 0.32f) else categoryColor.copy(alpha = 0.14f)
                } else {
                    if (isDark) Color(0xFF1E211D) else Color(0xFFF1EEE8)
                }

                val borderColor = if (isSelected) categoryColor else Color.Transparent

                Row(
                    modifier = Modifier
                        .height(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(chipBg)
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(21.dp)
                        )
                        .clickable(
                            role = Role.RadioButton,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onCategorySelected(category.id)
                            }
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
