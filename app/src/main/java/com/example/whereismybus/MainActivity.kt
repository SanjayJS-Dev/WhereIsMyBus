package com.example.whereismybus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GolfCourse
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.sharp.DepartureBoard
import androidx.compose.material.icons.sharp.DirectionsBus
import androidx.compose.material.icons.sharp.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whereismybus.ui.theme.Linen
import com.example.whereismybus.ui.theme.Spruce
import com.example.whereismybus.ui.theme.WhereIsMyBusTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhereIsMyBusTheme {
                val scrollState = rememberScrollState()
                Column(modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(bottom = 20.dp)) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(Spruce)
                    ) {
                        Row(modifier = Modifier
                            .statusBarsPadding()
                            .padding(20.dp)
                        ) {
                            Text(
                                text = "Where is my Bus ?",
                                fontSize = 20.sp,
                                fontFamily = FontFamily(Font(R.font.inter_black)),
                                color = Linen
                            )
                            Icon(
                                imageVector = Icons.Sharp.DirectionsBus,
                                contentDescription = "Bus Logo",
                                tint = Linen
                            )
                        }
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xff91d2ff))
                            .border(
                                width = 1.dp,
                                color = Color(0xff91d2ff),
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "SEARCH LIVE BUSES",
                                fontFamily = FontFamily(Font(R.font.inter_bold)),
                                fontSize = 15.sp,
                            )
                            Text(
                                text = "ഇപ്പോൾ വരാൻ പോകുന്ന ബസുകൾ തിരയുക",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_regular)),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Sharp.Sensors,
                            contentDescription = "Live Arrivals",
                            tint = Color(0xff007f73),
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        )
                    }

                    Column(modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                    ) {
                        var source by remember {
                            mutableStateOf("")
                        }
                        var expandedS by remember {
                            mutableStateOf(false)
                        }
                        var locating by remember {
                            mutableStateOf(false)
                        }
                        var locatingFinished by remember {
                            mutableStateOf(false)
                        }
                        ExposedDropdownMenuBox(expanded = expandedS, onExpandedChange = { expandedS=!expandedS }) {
                            OutlinedTextField(
                                value = source,
                                onValueChange = { source = it },
                                label = {
                                    Text(text = "Choose Starting Point")
                                },
                                placeholder = {
                                    Text(text = "Start Typing...")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = "Source"
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { source = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear Source"
                                        )
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedS, onDismissRequest = { expandedS = false }) {
                                DropdownMenuItem(
                                    text = { Text(text = if (!locating) "Locate Nearest Bus Stop" else "Locating Nearest Bus Stop", fontFamily = FontFamily(Font(R.font.inter_regular))) },
                                    onClick = { locating = true },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Filled.MyLocation,
                                            contentDescription = "Source"
                                        )
                                    },
                                    trailingIcon = {
                                        if(locating) {
                                            CircularProgressIndicator(
                                                color = Color.Gray,
                                                modifier = Modifier
                                                    .size(30.dp)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        var destination by remember {
                            mutableStateOf("")
                        }
                        var expandedD by remember {
                            mutableStateOf(false)
                        }
                        ExposedDropdownMenuBox(expanded = expandedD, onExpandedChange = { expandedD=!expandedD }) {
                            OutlinedTextField(
                                value = destination,
                                onValueChange = { destination = it },
                                label = {
                                    Text(text = "Choose Destination")
                                },
                                placeholder = {
                                    Text(text = "Start Typing...")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.GolfCourse,
                                        contentDescription = "Destination"
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { destination = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear Destination"
                                        )
                                    }
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expandedD, onDismissRequest = { expandedD = false }) {

                            }
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                        Button(
                            onClick = { searchBuses(source,destination) },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Linen,
                                containerColor = Spruce
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "SEARCH BUSES",
                                fontFamily = FontFamily(Font(R.font.inter_bold)),
                                fontSize = 15.sp,
                                modifier = Modifier.padding(end = 5.dp)
                            )
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Bus Search")
                        }
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .padding(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xff91d2ff))
                            .border(
                                width = 1.dp,
                                color = Color(0xff91d2ff),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp),
                        ) {
                            Text(
                                text = "TRACK A BUS",
                                fontFamily = FontFamily(Font(R.font.inter_bold)),
                                fontSize = 15.sp,
                            )
                            Text(
                                text = "ഒരു ബസിന്റെ വിവരം തേടുക",
                                fontFamily = FontFamily(Font(R.font.anek_malayalam_regular)),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Sharp.DepartureBoard,
                            contentDescription = "Live Arrivals",
                            tint = Color(0xff007f73),
                            modifier = Modifier
                                .padding(10.dp)
                        )
                    }
                    var busNumber by remember {
                        mutableStateOf("")
                    }
                    var searchingBus by remember {
                        mutableStateOf(false)
                    }
                    OutlinedTextField(
                        value = busNumber,
                        onValueChange = { busNumber = it },
                        label = {
                            Text(text = "Enter Bus Number")
                        },
                        placeholder = {
                            Text(text = "Eg: 70001")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.DirectionsBus,
                                contentDescription = "Bus Number"
                            )
                        },
                        trailingIcon = {
                            if (!searchingBus)
                            IconButton(onClick = { searchBus(busNumber) }) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search Icon",
                                )
                            }
                            else CircularProgressIndicator(
                                color = Spruce,
                                modifier = Modifier.size(25.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp)
                    )
                    Bus(this@MainActivity,"16305","AVM","MALAYATTOOR KOTHAMANGALAM","LIMITED STOP ORDINARY")
                }
            }
        }
    }
    private fun searchBuses(src: String, dest: String) {
        if(src == "") {
            Toast.makeText(this,"Choose Starting Point",Toast.LENGTH_SHORT).show()
        } else if (dest == "") {
            Toast.makeText(this,"Choose Destination Point",Toast.LENGTH_SHORT).show()
        } else {

        }
    }
    private fun searchBus(busNumber: String) {
        if (busNumber == "") {
            Toast.makeText(this,"Enter Bus Number",Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun Bus(classPass:Context, busNumber: String, operator:String,route:String,category:String) {
    Box(modifier = Modifier
        .padding(20.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Spruce)
        .border(
            width = 1.dp,
            color = Spruce,
            shape = RoundedCornerShape(10.dp)
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 5.dp)) {
                Icon(
                    imageVector = Icons.Filled.DirectionsBus,
                    contentDescription = "Operator",
                    tint = Linen,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = operator,
                    fontFamily = FontFamily(Font(R.font.inter_black)),
                    fontSize = 25.sp,
                    color = Linen
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 5.dp)) {
                Icon(
                    imageVector = Icons.Filled.Route,
                    contentDescription = "Operator",
                    tint = Linen,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(20.dp)
                )
                Text(
                    text = route,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontSize = 15.sp,
                    color = Linen
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.StopCircle,
                    contentDescription = "Category",
                    tint = Linen,
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .size(20.dp)
                )
                Text(
                    text = category,
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontSize = 15.sp,
                    color = Linen
                )
            }
            Button(
                onClick = { continueTracking(classPass,busNumber) },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Spruce,
                    containerColor = Linen
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = "CONTINUE TRACKING",
                    fontFamily = FontFamily(Font(R.font.inter_bold)),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Directions,
                    contentDescription = "Continue Tracking"
                )
            }
        }
    }
}

fun continueTracking(classPass: Context,busNumber: String) {
    val intent = Intent(classPass,Track::class.java)
    intent.putExtra("busNumber",busNumber)
    classPass.startActivity(intent)
}
