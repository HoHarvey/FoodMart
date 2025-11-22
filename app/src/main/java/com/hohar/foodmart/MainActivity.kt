package com.hohar.foodmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory
import com.hohar.foodmart.ui.theme.FoodMartTheme
import com.hohar.foodmart.viewmodel.FoodMartViewModel
import com.hohar.foodmart.viewmodel.UiState

class MainActivity : ComponentActivity() {
    private var foodList: ArrayList<Food> = arrayListOf()
    private var foodCategories: ArrayList<FoodCategory> = arrayListOf()


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodMartTheme {
                // Get ViewModel instance for state management
                val viewModel: FoodMartViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                when (uiState) {
                    // Show loading indicator while fetching data
                    is UiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = "Loading podcasts...",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                    is UiState.Success -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar ={
                                TopAppBar(
                                    title = { Text(text = "Food") }
                                )
                            }
                        ) { innerPadding ->
                            Greeting(
                                name = "Android",
                                modifier = Modifier.padding(innerPadding)
                            )
                            Column(
                                modifier = Modifier.fillMaxSize().padding(innerPadding)
                            ) {
                                FoodList(foodList, foodCategories)
                            }
                        }
                    }
                    is UiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = (uiState as UiState.Error).message,
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(
                                onClick = { viewModel.retry() },
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun FoodList(foodList: ArrayList<Food>, foodCategory: ArrayList<FoodCategory>) {
    Column() {
        foodList.forEach { food ->
            FoodItem(food, foodCategory)
        }
    }
}

@Composable
fun FoodItem(food: Food, foodCategory: ArrayList<FoodCategory>){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(food.image_url),
            contentDescription = food.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(text = food.title ?: "")
        Text(text = food.price.toString())
        foodCategory.forEach { category ->
            if (category.uuid == food.category_uuid) {
                Text(text = category.name ?: "")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FoodMartTheme {
        Greeting("Android")
    }
}