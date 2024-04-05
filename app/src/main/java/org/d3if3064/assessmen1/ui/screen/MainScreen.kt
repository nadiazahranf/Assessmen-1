package org.d3if3064.assessmen1.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.material.datepicker.MaterialDatePicker
import org.d3if3064.assessmen1.R
import org.d3if3064.assessmen1.navigation.Screen
import org.d3if3064.assessmen1.ui.theme.Assessmen1Theme
import java.util.Calendar
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {

    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(
                        onClick = {
                            navController.navigate((Screen.About.route))
                        }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) {padding ->
        ScreenContent(
            modifier = Modifier.padding(padding),
            selectedDate = selectedDate
        )
    }
}

@SuppressLint("StringFormatMatches")
@Composable
fun ScreenContent(modifier: Modifier, selectedDate: MutableState<Calendar>) {
    var saldo by remember { mutableStateOf("") }
    var saldoError by remember { mutableStateOf(false) }

    var masuk by remember { mutableStateOf("") }

    var keluar by remember { mutableStateOf("") }

    var hasil by remember { mutableStateOf(0f) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.tambah),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        DatePicker(selectedDate = selectedDate)

        OutlinedTextField(
            value = saldo,
            onValueChange = { saldo = it },
            label = { Text(text = stringResource(R.string.saldo_awal))},
            isError = saldoError,
            trailingIcon = { IconPicker(saldoError, "Rp")},
            supportingText = { ErrorHint(saldoError)},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = masuk,
            onValueChange = { masuk = it },
            label = { Text(text = stringResource(R.string.pemasukan))},
            trailingIcon = { Text(text = "Rp")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = keluar,
            onValueChange = { keluar = it },
            label = { Text(text = stringResource(R.string.pengeluaran))},
            trailingIcon = { Text(text = "Rp")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    saldoError = (saldo == "" || saldo == "0")
                    if (saldoError) return@Button

                    hasil = hitungUang(saldo.toFloat(), masuk.toFloat(), keluar.toFloat())
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(R.string.hitung))
            }
            Button(
                onClick = {
                    //set ulang nilai
                    saldo = ""
                    masuk = ""
                    keluar = ""
                    hasil = 0f
                    //set ulang status error
                    saldoError = false
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
            ) {
                Text(text = stringResource(id = R.string.reset))
            }
        }
        if (hasil != 0f) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )
            Text(
                text = stringResource(R.string.hasil_x,hasil),
                style = MaterialTheme.typography.titleLarge
            )
            Button(
                onClick = {
                    shareData(
                        context = context,
                        message = context.getString(R.string.bagikan_template, saldo, masuk, keluar, hasil)
                    )
                },
                modifier = Modifier.padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(id = R.string.bagikan))
            }
        }
    }
}

@Composable
fun DatePicker(selectedDate: MutableState<Calendar>){
    val context = LocalContext.current
    OutlinedTextField(
        value = "${selectedDate.value.get(Calendar.DAY_OF_MONTH)}-${selectedDate.value.get(Calendar.MONTH)+1}-${selectedDate.value.get(Calendar.YEAR)}",
        onValueChange = {},
        label = { Text(text = stringResource(R.string.tanggal))},
        trailingIcon = {
            IconButton(
                onClick = {
                    showDatePickerDialog(context, selectedDate)
                }) {
                    Icon(Icons.Filled.DateRange, contentDescription = null)
            }
        },
        readOnly = true,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun showDatePickerDialog(context: Context, selectedDate: MutableState<Calendar>){
    val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Pilih Tanggal")
        .setSelection(selectedDate.value.timeInMillis)
        .build()
    datePicker.addOnPositiveButtonClickListener{
        selectedDate.value = Calendar.getInstance().apply {
            time = Date(it)
        }
    }
    datePicker.show((context as AppCompatActivity).supportFragmentManager, datePicker.toString())
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT,message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null){
        context.startActivity(shareIntent)
    }
}

private fun hitungUang(saldo: Float, masuk: Float, keluar: Float): Float {
    return saldo + masuk - keluar
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    }else{
        Text(text = unit)
    }
}

@Composable
fun ErrorHint(isError: Boolean){
    if (isError) {
        Text(text = stringResource(R.string.input_invalid))
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScreenPreview() {
    Assessmen1Theme {
        MainScreen(rememberNavController())
    }
}