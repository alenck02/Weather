package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.weather.OpenAPI.RetrofitInstance
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.weather.OpenAPI.CurrentWeatherResponse
import com.example.weather.OpenAPI.ForecastItem
import com.example.weather.OpenAPI.ForecastResponse
import com.example.weather.ui.theme.WeatherTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val initialLocation = "위치 불러오는 중..."
    private var currentLat = 0.0
    private var currentLon = 0.0
    private lateinit var apiKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiKey = getString(R.string.api_key)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                getCurrentLocation { lat, lon, address ->
                    currentLat = lat
                    currentLon = lon
                    locationTextState.value = address

                    fetchCurrentWeather(lat, lon, currentWeatherState)
                    fetchForecast(lat, lon, forecastState)
                }
            } else {
                locationTextState.value = "위치 권한 없음"
            }
        }

        setContent {
            WeatherTheme {
                locationTextState = remember { mutableStateOf(initialLocation) }
                currentWeatherState = remember { mutableStateOf<CurrentWeatherResponse?>(null) }
                forecastState = remember { mutableStateOf<List<ForecastItem>>(emptyList()) }

                LaunchedEffect(Unit) {
                    checkLocationPermission()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WeatherMainScreen(
                            locationText = locationTextState.value,
                            onLocationRefresh = { refreshLocation() },
                            currentWeather = currentWeatherState.value,
                            forecastList = forecastState.value
                        )
                    }
                }
            }
        }
    }

    private lateinit var locationTextState: MutableState<String>
    private lateinit var currentWeatherState: MutableState<CurrentWeatherResponse?>
    private lateinit var forecastState: MutableState<List<ForecastItem>>

    private fun checkLocationPermission() {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        val granted = fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED
        if (granted) {
            getCurrentLocation { lat, lon, address ->
                currentLat = lat
                currentLon = lon
                locationTextState.value = address

                fetchCurrentWeather(lat, lon, currentWeatherState)
                fetchForecast(lat, lon, forecastState)
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun refreshLocation() {
        getCurrentLocation { lat, lon, address ->
            currentLat = lat
            currentLon = lon
            locationTextState.value = address

            fetchCurrentWeather(lat, lon, currentWeatherState)
            fetchForecast(lat, lon, forecastState)
        }
    }

    private fun getCurrentLocation(onAddressReceived: (Double, Double, String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            onAddressReceived(0.0, 0.0, "권한 없음")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.KOREA)
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = addresses?.getOrNull(0)?.locality ?: ""
                val district = addresses?.getOrNull(0)?.subLocality ?: ""
                onAddressReceived(location.latitude, location.longitude, "$city $district")
            } else {
                onAddressReceived(0.0, 0.0, "위치 정보 없음")
            }
        }
    }

    private fun fetchCurrentWeather(lat: Double, lon: Double, state: MutableState<CurrentWeatherResponse?>) {
        RetrofitInstance.api.getCurrentWeather(lat, lon, apiKey)
            .enqueue(object : Callback<CurrentWeatherResponse> {
                override fun onResponse(call: Call<CurrentWeatherResponse>, response: Response<CurrentWeatherResponse>) {
                    if (response.isSuccessful) state.value = response.body()
                }

                override fun onFailure(call: Call<CurrentWeatherResponse>, t: Throwable) {
                    state.value = null
                }
            })
    }

    private fun fetchForecast(lat: Double, lon: Double, state: MutableState<List<ForecastItem>>) {
        RetrofitInstance.api.getForecast(lat, lon, apiKey)
            .enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    if (response.isSuccessful) state.value = response.body()?.list ?: emptyList()
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    state.value = emptyList()
                }
            })
    }

    @Composable
    fun WeatherMainScreen(
        locationText: String,
        onLocationRefresh: () -> Unit,
        currentWeather: CurrentWeatherResponse?,
        forecastList: List<ForecastItem>
    ) {
        val skyBlue = Color(getColor(R.color.skyBlue))
            val myFontFamily = FontFamily(Font(R.font.oswald_bold))

        val dailyForecast = remember(forecastList) {
            forecastList.groupBy { item ->
                val cal = Calendar.getInstance().apply { timeInMillis = item.dt * 1000 }
                cal.get(Calendar.DAY_OF_YEAR)
            }.map { (_, items) ->
                val maxTemp = items.maxOfOrNull { it.main.temp_max } ?: 0.0
                val minTemp = items.minOfOrNull { it.main.temp_min } ?: 0.0
                val weatherMain = items.firstOrNull()?.weather?.firstOrNull()?.main ?: ""
                Triple(maxTemp, minTemp, weatherMain)
            }
        }

        val dailyForecastFilled = mutableListOf<Triple<Double, Double, String>>()
        for (i in 0 until 7) {
            if (i < dailyForecast.size) dailyForecastFilled.add(dailyForecast[i])
            else dailyForecastFilled.add(Triple(0.0, 0.0, "데이터 없음"))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.location_re),
                    contentDescription = "location",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { onLocationRefresh() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = locationText, color = skyBlue, fontSize = 22.sp, fontFamily = myFontFamily)
            }

            Spacer(modifier = Modifier.height(16.dp))

            currentWeather?.let { current ->
                Text("${current.main.temp.toInt()}℃", fontSize = 50.sp, color = skyBlue, fontFamily = myFontFamily)
                Text(current.weather.firstOrNull()?.main ?: "", fontSize = 25.sp, color = skyBlue, fontFamily = myFontFamily)
                Spacer(modifier = Modifier.height(8.dp))
                Text("체감: ${current.main.feels_like.toInt()}℃, 습도: ${current.main.humidity}%", fontSize = 20.sp, fontWeight = FontWeight.Medium, fontStyle = FontStyle.Italic, color = skyBlue, fontFamily = myFontFamily)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(skyBlue.copy(alpha = 0.1f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        "7일 예보",
                        fontSize = 20.sp,
                        color = skyBlue,
                        fontFamily = myFontFamily,
                        lineHeight = 28.sp,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    dailyForecastFilled.forEachIndexed { index, (max, min, weatherMain) ->
                        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, index) }
                        val dayOfWeek = SimpleDateFormat("E", Locale.KOREA).format(cal.time)
                        val dateStr = SimpleDateFormat("MM/dd", Locale.KOREA).format(cal.time)

                        val displayText = if (weatherMain == "데이터 없음") {
                            "$dateStr ($dayOfWeek): 데이터 없음"
                        } else {
                            "$dateStr ($dayOfWeek): $weatherMain, ${max.toInt()}℃/${min.toInt()}℃"
                        }

                        Text(
                            text = displayText,
                            fontSize = 16.sp,
                            color = skyBlue,
                            fontFamily = myFontFamily,
                            lineHeight = 24.sp,
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}