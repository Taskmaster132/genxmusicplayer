package au.com.idealogica.genxmusicplayer.ui.splashactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import au.com.idealogica.genxmusicplayer.ui.mainactivity.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		val splashScreen = installSplashScreen()
		super.onCreate(savedInstanceState)
		splashScreen.setKeepOnScreenCondition { true }
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}
}