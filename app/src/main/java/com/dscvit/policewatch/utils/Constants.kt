package com.dscvit.policewatch.utils

import com.dscvit.policewatch.models.PatrollingPoint

object Constants {
    const val BASE_URL = "police-watch-testing.herokuapp.com"

    const val SHARED_PREF_NAME = "police_watch_pref"
    const val SHARED_PREF_USER_TOKEN = "SHARED_PREF_USER_TOKEN"
    const val SHARED_PREF_IS_USER_SIGNED_IN = "SHARED_PREF_IS_USER_SIGNED_IN"
    const val SHARED_PREF_USER = "SHARED_PREF_USER"

    val PATROLLING_POINTS = listOf(
        PatrollingPoint(name = "PHUBALA", latitude = 24.534854, longitude = 93.756798),
        PatrollingPoint(name = "MOIRANG LAMKHAI", latitude = 24.497119, longitude = 93.765306),
        PatrollingPoint(name = "MOIRANG BAZAR", latitude = 24.5010371, longitude = 93.7763709),
        PatrollingPoint(name = "TERAKHONGSANGBI", latitude = 24.460078, longitude = 93.7387724),
        PatrollingPoint(name = "KWAKTA BAZAR", latitude = 24.4483570, longitude = 93.7303077),
        PatrollingPoint(name = "MOIRANGKHUNOU", latitude = 24.4715618, longitude = 93.7834335),
        PatrollingPoint(name = "CHINGMEI BAZAR", latitude = 24.4799539, longitude = 93.8075781),
        PatrollingPoint(name = "THANGALAWAI", latitude = 24.453129, longitude = 93.794158),
        PatrollingPoint(name = "KUMBI BAZAR", latitude = 24.4319071, longitude = 93.8089262),
        PatrollingPoint(name = "SAGANG BAZAR", latitude = 24.383513, longitude = 93.829091),
        PatrollingPoint(name = "SENDRA CHINGKHONG", latitude = 24.514771, longitude = 93.792946),
        PatrollingPoint(name = "THANGA HAORENG BAZAR", latitude = 24.534954, longitude = 93.834310),
        PatrollingPoint(name = "THANGA LAWAI BAZAR", latitude = 24.450076, longitude = 93.791402),
        PatrollingPoint(name = "SAITON BAZAR", latitude = 24.427649, longitude = 93.751113),
        PatrollingPoint(name = "NAMBOL BAZAR", latitude = 24.720118, longitude = 93.841043),
        PatrollingPoint(name = "OINAM BAZAR", latitude = 24.693607, longitude = 93.802060),
        PatrollingPoint(name = "KHWAIRAKPAN", latitude = 24.637793, longitude = 93.786518),
        PatrollingPoint(name = "BISHNUPUR BAZAR", latitude = 24.628557, longitude = 93.762565),
        PatrollingPoint(name = "BLOCK LAMKHAI", latitude = 24.614447, longitude = 93.761507),
        PatrollingPoint(name = "KHA POTSHANGBAM", latitude = 24.590660, longitude = 93.766169),
        PatrollingPoint(name = "PROJECT THONGKHONG", latitude = 24.578521, longitude = 93.764918),
        PatrollingPoint(name = "CHINGPHU FOOT HILLS", latitude = 24.693829, longitude = 93.802344),
        PatrollingPoint(name = "NEAR WAROI GARDEN", latitude = 24.725635, longitude = 93.770930),
        PatrollingPoint(name = "NEAR FCI GODOWN, BPR", latitude = 24.629872, longitude = 93.755026),
        PatrollingPoint(
            name = "DAMUDOR TEMPLE, TOUBUL",
            latitude = 24.622944,
            longitude = 93.800704
        ),
        PatrollingPoint(
            name = "ADJOINING AREA OF NGAIKHONG AND NGAIKHONG SIPHAI",
            latitude = 24.635756,
            longitude = 93.786299
        ),
        PatrollingPoint(
            name = "CHOTHE LAMKHAI (NEAR LOYOLA SCHOOL CROSSING)",
            latitude = 24.610719,
            longitude = 93.756855
        ),
        PatrollingPoint(
            name = "ZOUZANGTEK ROAD NEAR VDF POST",
            latitude = 24.651079,
            longitude = 93.687861
        ),
        PatrollingPoint(
            name = "LAIMATON ROAD NEAR VDF POST",
            latitude = 24.635139,
            longitude = 93.719254
        ),
    )

    val POLICE_STATION_POINTS = listOf(
        PatrollingPoint(name = "NAMBOL PS", latitude = 24.720817, longitude = 93.842528),
        PatrollingPoint(name = "BISHNUPUR PS", latitude = 24.6190992, longitude = 93.7617968),
        PatrollingPoint(name = "LOKTAK PS", latitude = 24.5797212, longitude = 93.7434042),
        PatrollingPoint(name = "MOIRANG PS", latitude = 24.4989, longitude = 93.7646),
        PatrollingPoint(name = "KUMBI PS", latitude = 24.42503, longitude = 93.812715),
        PatrollingPoint(name = "KEIBUL LAMJAO PS", latitude = 24.480043, longitude = 93.809159),
        PatrollingPoint(name = "PGCI PS", latitude = 24.436522, longitude = 93.723255),
    )
}