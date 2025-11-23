package com.hohar.foodmart


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.hohar.foodmart.model.Food
import com.hohar.foodmart.model.FoodCategory
import com.hohar.foodmart.ui.theme.FoodMartTheme
import com.hohar.foodmart.ui.theme.Purple80
import com.hohar.foodmart.viewmodel.FoodMartViewModel
import com.hohar.foodmart.viewmodel.UiState

class MainActivity : ComponentActivity() {

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
                                text = "Loading FoodMart...",
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                    is UiState.Success -> {
                        val sheetState = rememberModalBottomSheetState()
                        var showBottomSheet by remember { mutableStateOf(false) }
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar ={
                                TopAppBar(
                                    title = { Text(text = "Food") }
                                )
                            }
                        ) { innerPadding ->
                            val foodList by viewModel.foodList.collectAsState()
                            val foodCategories by viewModel.foodCategories.collectAsState()
                            Column(
                                modifier = Modifier.fillMaxSize().
                                    padding(innerPadding)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    Text(
                                        text = "Filter",
                                        color = Purple80,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .clickable(onClick = {
                                                showBottomSheet = true
                                            })
                                    )
                                }
                                FoodList(foodList as ArrayList<Food>,
                                    foodCategories as ArrayList<FoodCategory>
                                )
                                if (showBottomSheet) {
                                    ModalBottomSheet(
                                        onDismissRequest = {
                                            showBottomSheet = false
                                        },
                                        sheetState = sheetState
                                    ) {
                                        // Sheet content
                                        FoodCategoryFilters(
                                            foodCategories as ArrayList<FoodCategory>
                                        )
                                    }
                                }
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
fun FoodList(foodList: ArrayList<Food>, foodCategory: ArrayList<FoodCategory>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(foodList) { food ->
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
            contentDescription = food.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(text = food.name ?: "")
        Text(text = "$"+food.price.toString())
        foodCategory.forEach { category ->
            if (category.uuid == food.category_uuid) {
                Text(text = category.name ?: "")
            }
        }
    }
}

@Composable
fun FoodCategoryFilters(foodCategory: ArrayList<FoodCategory>){
    Column(modifier = Modifier
        .fillMaxWidth()) {
        foodCategory.forEach { category ->
            Row(modifier = Modifier
                .padding(8.dp)) {
                Text(text = category.name ?: "")
                Spacer(modifier = Modifier.weight(1f))
                var checked by remember { mutableStateOf(false) }
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    }
                )
            }
         }
    }
}

