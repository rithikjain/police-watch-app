package com.dscvit.policewatch.ui.supervisor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscvit.policewatch.R
import com.dscvit.policewatch.databinding.ActivitySupervisorBinding
import com.dscvit.policewatch.models.Officer
import com.dscvit.policewatch.models.PatrollingPoint
import com.dscvit.policewatch.ui.auth.PhoneNumberActivity
import com.dscvit.policewatch.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.net.ssl.SSLSocketFactory


@AndroidEntryPoint
class SupervisorActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "SupervisorActivity"
    }

    private lateinit var binding: ActivitySupervisorBinding
    private val viewModel: SupervisorViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var patrollingPointsAdapter: PatrollingPointsRecyclerViewAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private var webSocketClient: WebSocketClient? = null
    private var isMapReady = false
    private val officerMarkersMap = hashMapOf<Int, Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySupervisorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupListeners()
        //setupObservers()
        setupMap()
    }

    override fun onResume() {
        super.onResume()
        connectToWebsocket()
    }

    override fun onPause() {
        super.onPause()
        webSocketClient?.close()
    }

    private fun setupViews() {
        patrollingPointsAdapter = PatrollingPointsRecyclerViewAdapter(Constants.PATROLLING_POINTS) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.bottomSheet.scrollTo(0, 0)
            onPatrollingPointSelected(it)
        }

        binding.patrollingPointsRecyclerView.apply {
            adapter = patrollingPointsAdapter
            layoutManager =
                LinearLayoutManager(this@SupervisorActivity, LinearLayoutManager.VERTICAL, false)
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.scrollIndicatorImageView.rotation = 180f
                } else {
                    binding.scrollIndicatorImageView.rotation = 0f
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setupListeners() {
        binding.moreOptionsImageView.setOnClickListener {
            val popUpMenu = PopupMenu(this, binding.moreOptionsImageView)
            popUpMenu.menuInflater.inflate(R.menu.pop_up_menu, popUpMenu.menu)

            popUpMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.sign_out) {
                    signOut()
                }
                return@setOnMenuItemClickListener true
            }

            popUpMenu.show()
        }
    }

    private fun setupObservers() {
        viewModel.idToken.observe(this) {
            // DO something with the ID Token here
            Log.d(TAG, it ?: "")
        }
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun signOut() {
        Firebase.auth.signOut()
        viewModel.resetSavedUserToken()
        viewModel.setUserSignedOut()
        navigateToPhoneNumberActivity()
    }

    private fun navigateToPhoneNumberActivity() {
        val intent = Intent(this, PhoneNumberActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        isMapReady = true

        binding.bottomSheet.visibility = View.VISIBLE

        try {
            val success =
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        map.setMinZoomPreference(10f)

        addPatrollingPoints()

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(24.534854, 93.756798))
            .zoom(13f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun onPatrollingPointSelected(patrollingPoint: PatrollingPoint) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(patrollingPoint.latitude, patrollingPoint.longitude))
            .zoom(15f)
            .build()
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addPatrollingPoints() {
        val iconGenerator = IconGenerator(this)
        iconGenerator.setStyle(IconGenerator.STYLE_BLUE)

        for (patrollingPoint in Constants.PATROLLING_POINTS) {
            val coordinates = LatLng(patrollingPoint.latitude, patrollingPoint.longitude)

            map.addMarker(
                MarkerOptions()
                    .position(coordinates)
                    .icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(patrollingPoint.name)))
                    .anchor(iconGenerator.anchorU, iconGenerator.anchorV)
            )

            map.addCircle(
                CircleOptions()
                    .center(coordinates)
                    .radius(200.0)
                    .strokeColor(Color.parseColor("#7087CEEB"))
                    .fillColor(Color.parseColor("#6087CEEB"))
            )
        }
    }

    private fun updateOfficerLocation(officer: Officer) {
        val id = officer.patrollerID
        if (officerMarkersMap.containsKey(id)) {
            officerMarkersMap[id]?.position = LatLng(officer.coordinates.x, officer.coordinates.y)
        } else {
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(24.539628, 93.755377))
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_police_circle))
                    .title("Police Officer")
            )
            if (marker != null) officerMarkersMap[id] = marker
        }
    }

    private fun connectToWebsocket() {
        val uri = URI("wss://${Constants.BASE_URL}/ws/patroller/location")
        val socketFactory: SSLSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val headers = HashMap<String, String>()
        headers["Authorization"] = viewModel.getUserToken()

        val gson = Gson()

        webSocketClient = object : WebSocketClient(uri, headers) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "WebSocket onOpen")
            }

            override fun onMessage(message: String?) {
                Log.d(TAG, "WebSocket onMessage: $message")
                val officer = gson.fromJson(message ?: "", Officer::class.java)
                runOnUiThread {
                    if (isMapReady) updateOfficerLocation(officer)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket onClose: $reason")
            }

            override fun onError(ex: Exception?) {
                Log.d(TAG, "WebSocket onError: ${ex?.message}")
            }
        }

        webSocketClient?.setSocketFactory(socketFactory)
        webSocketClient?.connect()
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}