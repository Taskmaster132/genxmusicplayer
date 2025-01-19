package au.com.idealogica.genxmusicplayer.core.domain

sealed interface DataError : GenXMusicPlayerError {
	enum class Remote: DataError {
		REQUEST_TIMEOUT,
		TOO_MANY_REQUESTS,
		NO_INTERNET,
		SERVER,
		SERIALIZATION,
		UNKNOWN
	}

	enum class Local: DataError {
		DISK_FULL,
		UNKNOWN
	}
}