package com.example.integradora_ut2

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.integradora_ut2.ui.theme.Integradora_UT2Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Integradora_UT2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp()
                }
            }
        }
    }
}

//Estructura de navegacion de la app
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    var opcionseleccionada by remember { mutableStateOf("cartelera") }

    Scaffold(
        topBar = {
            //Titulo y color de la cabecera
            TopAppBar(
                title = { Text(text = "Cine") },
                colors = TopAppBarDefaults.topAppBarColors(Color.Red)
            )
        },
        bottomBar = {
            NavigationBar {
                //Opciones de navegacion de la app
                NavigationBarItem(
                    selected = opcionseleccionada == "mapa",
                    onClick = {
                        navController.navigate("mapa")
                        opcionseleccionada = "mapa"
                    },
                    icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Mapas") },
                    label = { Text(text = "Mapa") }
                )
                NavigationBarItem(
                    selected = opcionseleccionada == "cartelera",
                    onClick = {
                        navController.navigate("cartelera")
                        opcionseleccionada = "cartelera"
                    },
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Cartelera") },
                    label = { Text(text = "Cartelera") }
                )
                NavigationBarItem(
                    selected = opcionseleccionada == "temporizador",
                    onClick = {
                        navController.navigate("temporizador")
                        opcionseleccionada = "temporizador"
                    },
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "Temporizador") },
                    label = { Text(text = "Temporizador") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = paddingValues.calculateBottomPadding())
        ) {
            // Navegación entre pantallas
            NavHost(navController = navController, startDestination = "cartelera") {
                composable("mapa") {
                    PantallaMapa()
                }
                composable("cartelera") {
                    PantallaCartelera()
                }
                composable("temporizador") {
                    PantallaTemporizador()
                }
            }
        }
    }
}

//Pantalla con temporizador
@Composable
fun PantallaTemporizador() {
    var tiempousuario by remember { mutableStateOf("") }
    var tiemporestante by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Texto para ingresar el tiempo
        OutlinedTextField(
            value = tiempousuario,
            onValueChange = { tiempousuario = it },
            label = { Text("Tiempo en segundos") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Texto para mostrar el tiempo restante
        Text(
            text =
            if (tiemporestante > 0 ) {
                "Tiempo restante: $tiemporestante seg"
            } else {
                "Ingrese el tiempo y pulse el botón"
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón para iniciar la cuenta atrás
        Button(
            onClick = {
                val segundos = tiempousuario.toIntOrNull()
                if (segundos != null) {
                    if (segundos > 0) {
                        tiemporestante = segundos
                        coroutineScope.launch(Dispatchers.Default) {
                            while (tiemporestante > 0) {
                                Thread.sleep(1000)
                                tiemporestante--
                            }
                        }
                    }
                }else{
                    Toast.makeText(contexto,"Debe rellenar el campo",Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            //Texto del boton
            Text(text = "Comenzar")
        }
    }
}

//Pantalla con mapa
@Composable
fun PantallaMapa() {
    //Posicion en el mapa
    val cine = LatLng(28.1275897, -15.4541283)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cine, 10f)
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        //Marcador
        Marker(
            state = MarkerState(position = cine),
            title = "Cine Yelmo Las Arenas",
            snippet = "Marcador en el Cine Yelmo Las Arenas"
        )
    }
}

//Pantalla de la base de datos
//Definir la base de datos
val bd = FirebaseFirestore.getInstance()

//Accedemos a la coleccion Peliculas
val peliculas = bd.collection("Peliculas")

//Listado de peliculas
val listapeliculas = mutableStateListOf<Peliculas>()

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCartelera() {
    val navController = rememberNavController()
    val estadoDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val corutineScope = rememberCoroutineScope()
    var opcionSeleccionada by remember { mutableStateOf("todos") }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                //Texto del drawer
                Text(
                    text = "Menú",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = Center
                )
                // Opciones del drawer
                NavigationDrawerItem(
                    label = { Text(text = "Todos") },
                    selected = opcionSeleccionada == "todos",
                    onClick = {
                        navController.navigate("todos")
                        opcionSeleccionada = "todos"
                        corutineScope.launch { estadoDrawer.close() }
                    },
                    icon = { Icon(Icons.Filled.Menu, null) }
                )
                NavigationDrawerItem(
                    label = { Text("Buscar") },
                    selected = opcionSeleccionada == "buscar",
                    onClick = {
                        navController.navigate("buscar")
                        opcionSeleccionada = "buscar"
                        corutineScope.launch { estadoDrawer.close() }
                    },
                    icon = { Icon(Icons.Filled.Search, null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Insertar") },
                    selected = opcionSeleccionada == "insertar",
                    onClick = {
                        navController.navigate("insertar")
                        opcionSeleccionada = "insertar"
                        corutineScope.launch { estadoDrawer.close() }
                    },
                    icon = { Icon(Icons.Filled.Add, null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Actualizar") },
                    selected = opcionSeleccionada == "actualizar",
                    onClick = {
                        navController.navigate("actualizar")
                        opcionSeleccionada = "actualizar"
                        corutineScope.launch { estadoDrawer.close() }
                    },
                    icon = { Icon(Icons.Filled.Edit, null) }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Borrar") },
                    selected = opcionSeleccionada == "borrar",
                    onClick = {
                        navController.navigate("borrar")
                        opcionSeleccionada = "borrar"
                        corutineScope.launch { estadoDrawer.close() }
                    },
                    icon = { Icon(Icons.Filled.Delete, null) }
                )
            }
        },
        drawerState = estadoDrawer
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Menú")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                corutineScope.launch { estadoDrawer.open() }
                            }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
            ) {
                // Navegación entre las opciones
                NavHost(navController = navController, startDestination = "todos") {
                    composable("buscar") {
                        PantallaBuscar()
                    }
                    composable("insertar") {
                        PantallaInsertar()
                    }
                    composable("actualizar") {
                        PantallaActualizar()
                    }
                    composable("todos") {
                        PantallaTodos()
                    }
                    composable("borrar") {
                        PantallaBorrar()
                    }
                }
            }
        }
    }
}


//Buscar por id un documento
@Composable
fun PantallaBuscar() {
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Buscar", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        var id by remember { mutableStateOf("") }
        var titulo by remember { mutableStateOf("") }
        var año by remember { mutableStateOf("") }
        var clasificacion by remember { mutableStateOf("") }

        //Campo para la insercion de la id
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "Identificador") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        //Para mostrar el resultado devuelto de la bd
        Text(text = "Titulo: $titulo")
        Text(text = "Año: $año")
        Text(text = "Clasificacion: $clasificacion")

        //Boton para enviar la id a la bd
        Button(
            onClick = {
                if(id.isNotEmpty()){
                peliculas.document(id.toString()).get().addOnSuccessListener {
                    if (it.exists()) {
                        id = it.get("id").toString()
                        titulo = it.get("titulo").toString()
                        año = it.get("año").toString()
                        clasificacion = it.get("clasificacion").toString()
                        Toast.makeText(contexto, "Registro encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(contexto, "Registros no encontrado", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                }else{
                    Toast.makeText(contexto, "Debe introducir una id", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            //texto del boton
            Text(text = "Buscar por id")
        }

    }
}

//Insertar documentos en la bd
@Composable
fun PantallaInsertar() {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Insertar", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        var id by remember { mutableStateOf("") }
        var titulo by remember { mutableStateOf("") }
        var año by remember { mutableStateOf("") }
        var clasificacion by remember { mutableStateOf("") }

        //Campo para insertar la id
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "Identificador") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar el titulo
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(text = "Titulo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar el año
        OutlinedTextField(
            value = año,
            onValueChange = { año = it },
            label = { Text(text = "Año") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar la clasificacion
        OutlinedTextField(
            value = clasificacion,
            onValueChange = { clasificacion = it },
            label = { Text(text = "Clasificacion") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Boton para enviar los datos
        Button(
            onClick = {
                if(id.isNotEmpty() && titulo.isNotEmpty() && año.isNotEmpty() && clasificacion.isNotEmpty()){
                    val peli = Peliculas(id, titulo, año.toInt(), clasificacion)
                    peliculas.document(id).set(peli)
                    Toast.makeText(contexto, "Registro Insertado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(contexto,"Debe rellenar todos los campos",Toast.LENGTH_SHORT).show()
                }

            }
        ) {
            //Texto del boton
            Text("Insertar")
        }
    }
}


//Actualizar documentos
@Composable
fun PantallaActualizar() {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Actualizar", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        var id by remember { mutableStateOf("") }
        var titulo by remember { mutableStateOf("") }
        var año by remember { mutableStateOf("") }
        var clasificacion by remember { mutableStateOf("") }

        //Campo para insertar la id
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "Identificador") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar el titulo
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text(text = "Titulo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar el año
        OutlinedTextField(
            value = año,
            onValueChange = { año = it },
            label = { Text(text = "Año") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Campo para insertar la clasificacion
        OutlinedTextField(
            value = clasificacion,
            onValueChange = { clasificacion = it },
            label = { Text(text = "Clasificacion") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            singleLine = true
        )

        //Boton para enviar los datos
        Button(
            onClick = {
                if(id.isNotEmpty() && titulo.isNotEmpty() && año.isNotEmpty() && clasificacion.isNotEmpty()) {
                    peliculas.document(id).set(
                        hashMapOf(
                            "id" to id.toString(),
                            "titulo" to titulo.toString(),
                            "año" to año.toInt(),
                            "clasificacion" to clasificacion.toString()
                        )
                    )
                    Toast.makeText(contexto, "Registro Actualizado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(contexto, "Debe rellenar todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            //Texto del boton
            Text("Actualizar")
        }
    }
}

//Eliminar un documento de la bd
@Composable
fun PantallaBorrar() {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Borrar", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        var id by remember { mutableStateOf("") }

        //Campo para insertar la id
        OutlinedTextField(
            value = id,
            onValueChange = { id = it },
            label = { Text(text = "Identificador") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        //Boton para enviar los datos
        Button(
            onClick = {
                if(id.isNotEmpty()) {
                    peliculas.document(id.toString()).get().addOnSuccessListener {
                        if (it.exists()) {
                            it.reference.delete()
                            Toast.makeText(contexto, "Registro eliminado", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(contexto, "Registros no encontrado", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }else{
                    Toast.makeText(contexto, "Debe introducir una id", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        ) {
            //Texto del boton
            Text(text = "Borrar por id")
        }

    }
}

//Mostrar todos los documentos
@Composable
fun PantallaTodos() {
    rellenalista()
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        //Mostrar en el lazy column el listado de peliculas de la bd
        items(listapeliculas) { p ->
            Mostrarpelicula(p)
        }
    }
}
//Crear la estructura para mostrar la informacion de cada pelicula
@Composable
fun Mostrarpelicula(p: Peliculas) {
    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .width(16.dp))
    Text(text = "Indentificador: ${p.id}")
    Text(text = "Titulo: ${p.titulo}")
    Text(text = "Año: ${p.año.toString()}")
    Text(text = "Clasificacion: ${p.clasificacion}")
    HorizontalDivider(modifier = Modifier
        .fillMaxWidth()
        .width(16.dp))
}

//Crear una lista con todas las peliculas de la bd
private fun rellenalista() {
    listapeliculas.clear()
    peliculas.get().addOnSuccessListener {
        for (docum in it) {
            val p = Peliculas(
                docum.data.get("id").toString(),
                docum.data.get("titulo").toString(),
                docum.data.get("año").toString().toInt(),
                docum.data.get("clasificacion").toString()
            )
            listapeliculas.add(p)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Integradora_UT2Theme {
        MyApp()
    }
}