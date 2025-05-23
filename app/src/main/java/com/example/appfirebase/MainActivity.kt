package com.example.appfirebase

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appfirebase.ui.theme.AppFirebaseTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFirebaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppAula()
                }
            }
        }
    }
}

@Composable
fun AppAula() {
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var cidade by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }

    val userList = remember { mutableStateListOf<String>() }

    // Função para buscar os usuários da coleção do Firestore
    fun fetchUsers() {
        val db = Firebase.firestore
        userList.clear() // Limpa a lista antes de recarregar

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.data
                    val tela = "${data["nome"]} ${data["sobrenome"]} - ${data["cidade"]} - ${data["idade"]} anos"
                    userList.add(tela)

                    // No logcat aparece, mas eu quis colocar na tela também
                    Log.d(TAG, "${document.id} => $data")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Erro ao buscar documentos.", exception)
            }
    }

    Column(
        Modifier
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            Text("App Firebase", fontSize = 30.sp)
        }

        // Campo Nome
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome:") }
            )
        }

        // Campo Sobrenome
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            TextField(
                value = sobrenome,
                onValueChange = { sobrenome = it },
                label = { Text("Sobrenome:") }
            )
        }

        // Campo Endereço
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            TextField(
                value = endereco,
                onValueChange = { endereco = it },
                label = { Text("Endereço:") }
            )
        }

        // Campo Cidade
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            TextField(
                value = cidade,
                onValueChange = { cidade = it },
                label = { Text("Cidade:") }
            )
        }

        // Campo Idade
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            TextField(
                value = idade,
                onValueChange = { idade = it },
                label = { Text("Idade:") }
            )
        }

        // Botão Cadastrar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Ação de cadastrar
                    val db = Firebase.firestore

                    // Create a new user with a first and last name
                    val user = hashMapOf(
                        "nome" to nome,
                        "sobrenome" to sobrenome,
                        "endereco" to endereco,
                        "cidade" to cidade,
                        "idade" to idade
                    )

                    // Add a new document with a generated ID
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                            // Atualiza a lista de usuários
                            fetchUsers()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    // Limpa os campos após cadastro
                    nome = ""
                    sobrenome = ""
                    endereco = ""
                    idade = ""
                    cidade = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cadastrar", fontSize = 25.sp)
            }
        }

        // Botão Exibir
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            Arrangement.Center
        ) {
            Button(
                onClick = {
                    // Chama a função para atualizar a lista de usuários
                    fetchUsers()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text("Exibir", fontSize = 20.sp)
            }
        }

        // Lista de usuários exibida na tela
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            if (userList.isNotEmpty()) {
                Text("Usuários cadastrados:", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                userList.forEachIndexed { index, user ->
                    Text(text = "${index + 1}) $user", fontSize = 14.sp, modifier = Modifier.padding(4.dp))
                }
            }
        }
    }
}


@Preview
@Composable
fun AppPreview() {
    AppFirebaseTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppAula()
        }
    }
}
