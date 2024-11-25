package com.wakeupdev.weatherforecast

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

//val weatherState by viewModel.weatherState.observeAsState()
//
//when (weatherState) {
//    is WeatherState.Loading -> {
//        CircularProgressIndicator()
//    }
//    is WeatherState.Success -> {
//        val weather = (weatherState as WeatherState.Success).weatherData
//        Text("Current temperature: ${weather.currentTemperature}°C")
//    }
//    is WeatherState.Error -> {
//        Text("Error: ${(weatherState as WeatherState.Error).message}")
//    }
//}

//@Composable
//fun WeatherSearchScreen(viewModel: WeatherViewModel = viewModel()) {
//    var city by remember { mutableStateOf("") }
//    val weatherData by viewModel.weatherData.collectAsState()
//    val error by viewModel.error.collectAsState()
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp)
//    ) {
//        TextField(
//            value = city,
//            onValueChange = { city = it },
//            label = { Text("Enter City") }
//        )
//        Button(onClick = { viewModel.fetchWeather(city) }) {
//            Text("Search")
//        }
//
//        weatherData?.let { weather ->
//            Text("City: ${weather.name}")
//            Text("Temp: ${weather.main.temp}°C")
//            Text("Description: ${weather.weather.first().description}")
//        }
//
//        error?.let {
//            Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
//        }
//    }
//}
