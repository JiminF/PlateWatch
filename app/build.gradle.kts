plugins { alias(libs.plugins.android.application); alias(libs.plugins.kotlin.android); alias(libs.plugins.kotlin.compose);  alias(libs.plugins.ksp) }
android {
    namespace="co.intecdl.platewatch"; compileSdk=35
    defaultConfig { applicationId="co.intecdl.platewatch"; minSdk=24; targetSdk=35; versionCode=3; versionName="0.2.0" 
    javaCompileOptions{
        arguments += maoOF(
            "room.schemaLocation" to "$projectDir/schemas"
        )
    }
    }
    buildTypes { release { isMinifyEnabled=true; proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro") } }
    compileOptions { sourceCompatibility=JavaVersion.VERSION_17; targetCompatibility=JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget="17" }
    buildFeatures { compose=true }
   
}
dependencies {
    implementation(libs.core.ktx); implementation(libs.activity.compose); implementation(libs.lifecycle.runtime)
    implementation(platform(libs.compose.bom)); implementation(libs.compose.ui); implementation(libs.compose.tooling.preview); implementation(libs.material3)
    implementation(libs.camera.core); implementation(libs.camera.camera2); implementation(libs.camera.lifecycle); implementation(libs.camera.view)
    implementation(libs.coroutines.android); debugImplementation(libs.compose.tooling); testImplementation(libs.junit)
    implementation(libs.room.runtime)
    implementation(lib.room.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    ksp(libs.room.compiler)

    testImplementation(libs.room.testing)
}
