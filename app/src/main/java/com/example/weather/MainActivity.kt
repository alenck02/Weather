package com.example.weather

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weather.ui.theme.WeatherTheme
import com.google.android.gms.location.LocationServices
import java.util.Locale
import android.Manifest
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var initialLocation: String = "위치 불러오는 중..."

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            getLastLocation()
        }
    }

    fun onLocationRefresh(
        context: Context,
        onAddressUpdated: (String) -> Unit
    ) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.adminArea ?: ""
                    val district = address.subLocality ?: address.subAdminArea ?: ""
                    val finalAddress = "$city $district"
                    onAddressUpdated(finalAddress)
                } else {
                    onAddressUpdated("주소를 불러올 수 없음")
                }
            } ?: run {
                onAddressUpdated("위치 정보 없음")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkLocationPermissionAndRequest()

        setContent {
            WeatherTheme {
                var locationText by remember { mutableStateOf(initialLocation) }

                LaunchedEffect(Unit) {
                    getCurrentLocation(this@MainActivity) { newAddress ->
                        locationText = newAddress
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        locationText = locationText,
                        onLocationRefresh = {
                            onLocationRefresh(context = this@MainActivity) { newAddress ->
                                locationText = newAddress
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkLocationPermissionAndRequest() {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.KOREA)
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: ""
                    val district = addresses[0].subLocality ?: ""
                    initialLocation = "$city $district"
                }
            }
        }
    }
}

fun getCurrentLocation(context: Context, onAddressReceived: (String) -> Unit) {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
    ) {
        onAddressReceived("위치 권한 없음")
        return
    }

    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val city = address.locality ?: address.adminArea ?: ""
                val district = address.subLocality ?: address.subAdminArea ?: ""
                onAddressReceived("$city $district")
            } else {
                onAddressReceived("주소 정보 없음")
            }
        } else {
            onAddressReceived("위치 정보 없음")
        }
    }.addOnFailureListener {
        onAddressReceived("위치 요청 실패")
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, locationText: String, onLocationRefresh: () -> Unit) {
    val context = LocalContext.current

    val skyBlue = Color(ContextCompat.getColor(context, R.color.skyBlue))
    val white = Color(ContextCompat.getColor(context, R.color.white))

    val myFontFamily = FontFamily(
        Font(R.font.oswald_bold)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location_re),
                    contentDescription = "location",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            onLocationRefresh()
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = locationText, color = skyBlue, fontSize = 22.sp, fontFamily = myFontFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "25℃", color = skyBlue, fontSize = 40.sp, fontFamily = myFontFamily)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "맑음", color = skyBlue, fontSize = 20.sp, fontFamily = myFontFamily)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "30℃ / 23℃", color = skyBlue, fontSize = 18.sp, fontFamily = myFontFamily)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "체감온도 27℃", color = skyBlue, fontSize = 18.sp, fontFamily = myFontFamily)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .background(color = skyBlue, shape = RoundedCornerShape(20.dp))
                    .padding(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("미세먼지", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("보통", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("초미세먼지", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("나쁨", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("습도", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("73%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.9f)
                    .background(color = skyBlue, shape = RoundedCornerShape(20.dp))
                    .padding(15.dp)
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("오늘", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("20%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("29℃/20℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("내일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("40%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("25℃/18℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("금요일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("10%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("35℃/25℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("토요일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("10%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("34℃/24℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("일요일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("60%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("23℃/14℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("월요일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("80%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("21℃/12℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text("화요일", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(50.dp))
                        Text("10%", color = white, fontSize = 18.sp, fontFamily = myFontFamily)
                        Text("32℃/22℃", color = white, fontSize = 18.sp, fontFamily = myFontFamily, modifier = Modifier
                            .width(100.dp))
                    }
                }
            }
        }
    }
}