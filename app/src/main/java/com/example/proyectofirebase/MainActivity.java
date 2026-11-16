package com.example.proyectofirebase;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText etNombre, etEmail;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1) Referenciar la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        tvResultado = findViewById(R.id.tvResultado);
        Button btnGuardar = findViewById(R.id.btnGuardar);
        Button btnPost = findViewById(R.id.btnPost);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            if (nombre.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            writeUserData("user1", nombre, email, "https://ejemplo.com/foto.png");
        });

        btnPost.setOnClickListener(v ->
                writeNewPost("user1", etNombre.getText().toString(), "foto.png",
                        "Mi primer post", "Hola Firebase"));

        leerUsuarios();
    }

    // 2) Escribir con set()
    private void writeUserData(String userId, String name, String email, String imageUrl) {
        Usuario user = new Usuario(name, email, imageUrl);
        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(a -> Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // 3) Actualizar varios lugares a la vez con update()
    private void writeNewPost(String uid, String username, String picture, String title, String body) {
        String key = mDatabase.child("posts").push().getKey();

        Map<String, Object> postData = new HashMap<>();
        postData.put("author", username);
        postData.put("uid", uid);
        postData.put("title", title);
        postData.put("body", body);
        postData.put("starCount", 0);
        postData.put("authorPic", picture);

        Map<String, Object> updates = new HashMap<>();
        updates.put("/posts/" + key, postData);
        updates.put("/user-posts/" + uid + "/" + key, postData);

        mDatabase.updateChildren(updates);
    }

    // 4) Leer datos en tiempo real
    private void leerUsuarios() {
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder sb = new StringBuilder("Datos en Firebase:\n");
                for (DataSnapshot child : snapshot.getChildren()) {
                    Usuario u = child.getValue(Usuario.class);
                    if (u != null) {
                        sb.append(child.getKey()).append(": ")
                                .append(u.username).append(" - ").append(u.email).append("\n");
                    }
                }
                tvResultado.setText(sb.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvResultado.setText("Error: " + error.getMessage());
            }
        });
    }
}