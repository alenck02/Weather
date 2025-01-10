package com.example.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.ui.theme.SkyBlue
import com.example.weather.ui.theme.White

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme()
        }
    }
}

@Preview
@Composable
fun WeatherTheme() {
    MaterialTheme {
        val customFont = FontFamily(Font(R.font.poppins))
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp

        val spacing = when {
            screenHeight < 600.dp -> 8.dp
            screenHeight < 900.dp -> 16.dp
            else -> 24.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SkyBlue)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherHeader(customFont)
            Spacer(modifier = Modifier.height(spacing))

            WeatherTemperature(customFont)
            Spacer(modifier = Modifier.height(spacing))

            WeatherCondition(customFont)
            Spacer(modifier = Modifier.height(spacing))

            WeatherInfoSection()
            Spacer(modifier = Modifier.height(spacing))

            WeekWeatherInfo()
        }
    }
}

@Composable
fun WeatherHeader(customFont: FontFamily) {
    Row(
        modifier = Modifier
            .padding(top = 30.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.autorenew),
            contentDescription = null
        )
        Text(
            text = "서울 강남구",
            fontFamily = customFont,
            fontSize = 20.sp,
            color = White
        )
    }
}

@Composable
fun WeatherTemperature(customFont: FontFamily) {
    Text(
        text = "-2°",
        fontFamily = customFont,
        fontSize = 50.sp,
        color = White
    )
    Text(
        text = "맑음",
        fontFamily = customFont,
        fontSize = 20.sp,
        color = White
    )
}

@Composable
fun WeatherCondition(customFont: FontFamily) {
    Row(
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(end = 5.dp),
            text = "6°/-5°",
            fontFamily = customFont,
            fontSize = 20.sp,
            color = White
        )
        Text(
            text = "체감온도 -4°",
            fontFamily = customFont,
            fontSize = 20.sp,
            color = White
        )
    }
}

@Composable
fun WeatherInfoSection() {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .border(BorderStroke(2.dp, Color.White))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WeatherInfoItem(label = "미세먼지", value = "50 µg/m³")
        WeatherInfoItem(label = "습도", value = "40%")
        WeatherInfoItem(label = "바람", value = "5 m/s")
    }
}

@Composable
fun WeekWeatherInfo() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .border(BorderStroke(2.dp, Color.White))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        WeekWeatherInfoItem(
            label = "목요일",
            value = "20%",
            painterResource(R.drawable.cloud),
            painterResource(R.drawable.sunny),
            value1 = "6",
            value2 = "-5"
            )

        WeekWeatherInfoItem(
            label = "금요일",
            value = "40%",
            painterResource(R.drawable.snowing),
            painterResource(R.drawable.wind),
            value1 = "2",
            value2 = "-7"
        )

        WeekWeatherInfoItem(
            label = "토요일",
            value = "90%",
            painterResource(R.drawable.umbrella),
            painterResource(R.drawable.thunderstorm),
            value1 = "4",
            value2 = "1"
        )

        WeekWeatherInfoItem(
            label = "일요일",
            value = "20%",
            painterResource(R.drawable.sunny),
            painterResource(R.drawable.cloud),
            value1 = "8",
            value2 = "3"
        )

        WeekWeatherInfoItem(
            label = "월요일",
            value = "60%",
            painterResource(R.drawable.cloud),
            painterResource(R.drawable.snowing),
            value1 = "1",
            value2 = "-10"
        )

        WeekWeatherInfoItem(
            label = "화요일",
            value = "80%",
            painterResource(R.drawable.umbrella),
            painterResource(R.drawable.cloud),
            value1 = "3",
            value2 = "-3"
        )

        WeekWeatherInfoItem(
            label = "수요일",
            value = "90%",
            painterResource(R.drawable.thunderstorm),
            painterResource(R.drawable.umbrella),
            value1 = "2",
            value2 = "-6"
        )
    }
}

@Composable
fun WeatherInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = White
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = White
        )
    }
}

@Composable
fun WeekWeatherInfoItem(
    label: String,
    value: String,
    image1: Painter,
    image2: Painter,
    value1: String,
    value2: String
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = White
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = White
        )
        Image(
            painter = image1,
            contentDescription = null
        )
        Image(
            painter = image2,
            contentDescription = null
        )
        Text(
            text = "$value1°",
            fontSize = 16.sp,
            color = White
        )
        Text(
            text = "$value2°",
            fontSize = 16.sp,
            color = White
        )
    }
}