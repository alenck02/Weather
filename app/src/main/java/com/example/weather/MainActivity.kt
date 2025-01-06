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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.ui.theme.SkyBlue
import com.example.weather.ui.theme.White
import kotlin.math.max

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SkyBlue)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeatherHeader(customFont)
            WeatherTemperature(customFont)
            WeatherCondition(customFont)
            WeatherInfoSection()
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
        text = "-2",
        fontFamily = customFont,
        fontSize = 40.sp,
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
            text = "6/-5",
            fontFamily = customFont,
            fontSize = 20.sp,
            color = White
        )
        Text(
            text = "체감온도 -4",
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
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        WeatherInfoItem(label = "미세먼지", value = "50 µg/m³")
        WeatherInfoItem(label = "습도", value = "40%")
        WeatherInfoItem(label = "바람", value = "5 m/s")
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