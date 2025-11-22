package com.hohar.foodmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory
import com.hohar.foodmart.ui.theme.FoodMartTheme

class MainActivity : ComponentActivity() {
    private var foodList: ArrayList<Food> = arrayListOf()
    private var foodCategories: ArrayList<FoodCategory> = arrayListOf()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodMartTheme {
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