package au.com.idealogica.genxmusicplayer.model

import kotlinx.serialization.json.Json

object GenXJson {
	fun configure(): Json {
		return Json {
			isLenient = true
			ignoreUnknownKeys = true
			encodeDefaults = true
			explicitNulls = false
		}
	}
}