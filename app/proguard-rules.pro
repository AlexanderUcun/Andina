# ============================================================================
# 🛡️ REGLAS PROGUARD / R8 OFICIALES DE ANDINA FINANZAS
# Blindaje contra ofuscación agresiva en compilación Release (R8 Full Mode)
# ============================================================================

# --- 1. SQLCipher (Cifrado militar de Base de Datos) ---
-keep class net.zetetic.database.** { *; }
-keep class net.zetetic.database.sqlcipher.** { *; }
-dontwarn net.zetetic.database.**
-dontwarn net.zetetic.database.sqlcipher.**

# --- 2. Room Database & Paging 3 ---
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# --- 3. AndroidX BiometricPrompt & Android Keystore ---
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**
-keep class com.andina.finanzas.data.security.** { *; }

# --- 4. Kotlinx Serialization (Backup Manager & Navigation Routes) ---
-keepattributes *Annotation*, InnerClasses, Signature
-dontnote kotlinx.serialization.SerializationKt

# Preserva clases serializables y sus serializadores generados
-keep,allowobfuscation,allowshrinking @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# Preserva rutas type-safe de Navigation Compose y DTOs de backup
-keep class com.andina.finanzas.ui.navigation.** { *; }
-keep class com.andina.finanzas.data.export.** { *; }

# --- 5. Coroutines & Inyección Hilt / Dagger ---
-dontwarn kotlinx.coroutines.**
-keep class com.andina.finanzas.data.di.** { *; }
