import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.composeMultiplatform)
	alias(libs.plugins.composeCompiler)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ksp)
}

kotlin {
	androidTarget {
		@OptIn(ExperimentalKotlinGradlePluginApi::class)
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_11)
		}
	}

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach { iosTarget ->
		iosTarget.binaries.framework {
			baseName = "ComposeApp"
			isStatic = true
		}
	}

	sourceSets {

		androidMain.dependencies {
			implementation(compose.preview)
			implementation(libs.androidx.activity.compose)
			implementation(libs.androidx.compose.material3)
			implementation(libs.androidx.compose.icons.core)
			implementation(libs.androidx.compose.icons.extended)
			implementation(libs.androidx.documentfile)
			implementation(libs.androidx.media)
			implementation(libs.androidx.media3.common)
			implementation(libs.androidx.media3.exoplayer)
			implementation(libs.androidx.media3.session)
			implementation(libs.androidx.media3.ui)
			implementation(libs.androidx.navigation)
			implementation(libs.androidx.splashscreen)
			implementation(libs.koin.android)
			implementation(libs.koin.compose.viewmodel)
			implementation(libs.koin.compose.viewmodel.navigation)
			implementation(libs.koin.androidx.compose)
			implementation(libs.koin.androidx.compose.navigation)
		}
		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.ui)
			implementation(compose.components.resources)
			implementation(compose.components.uiToolingPreview)
			implementation(libs.androidx.lifecycle.viewmodel)
			implementation(libs.androidx.lifecycle.runtime.compose)
			implementation(libs.jetbrains.datetime)
			implementation(libs.jetbrains.serialization.json)
			api(libs.koin.annotations)
			implementation(libs.koin.core)
		}
	}

	sourceSets.named("commonMain").configure {
		kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
	}
}

android {
	namespace = "au.com.idealogica.genxmusicplayer"
	compileSdk = libs.versions.android.compileSdk.get().toInt()

	defaultConfig {
		applicationId = "au.com.idealogica.genxmusicplayer"
		minSdk = libs.versions.android.minSdk.get().toInt()
		targetSdk = libs.versions.android.targetSdk.get().toInt()
		versionCode = 4
		versionName = "0.2.0"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
	buildTypes {
		getByName("release") {
			// Enables code shrinking, obfuscation, and optimization for only
			// your project's release build type. Make sure to use a build
			// variant with `isDebuggable=false`.
			isMinifyEnabled = true

			// Enables resource shrinking, which is performed by the
			// Android Gradle plugin.
			isShrinkResources = true

			proguardFiles(
				// Includes the default ProGuard rules files that are packaged with
				// the Android Gradle plugin. To learn more, go to the section about
				// R8 configuration files.
				getDefaultProguardFile("proguard-android-optimize.txt"),

				// Includes a local, custom Proguard rules file
				"proguard-rules.pro"
			)

			ndk {
				debugSymbolLevel = "FULL"
			}
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	lint {
		abortOnError = false
	}
}

dependencies {
	debugImplementation(compose.uiTooling)
	add("kspCommonMainMetadata", libs.koin.ksp.compiler)
	add("kspAndroid", libs.koin.ksp.compiler)
	add("kspIosX64", libs.koin.ksp.compiler)
	add("kspIosArm64", libs.koin.ksp.compiler)
	add("kspIosSimulatorArm64", libs.koin.ksp.compiler)
}

// Trigger Common Metadata Generation from Native tasks
project.tasks.withType(KotlinCompilationTask::class.java).configureEach {
	if (name != "kspCommonMainKotlinMetadata") {
		dependsOn("kspCommonMainKotlinMetadata")
	}
}
