package mx.tecnm.tepic.ladm_u3_ejercicio3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var extra=intent.extras

        editTextA1.setText(extra!!.getString("nombre"))
        editTextA2.setText(extra!!.getString("telefono"))
        editTextA3.setText(extra!!.getString("domicilio"))
        button.setOnClickListener {
            baseRemota.collection("persona")
                .document(id)
                .update("nombre",editTextA1.text.toString(),
                    "telefono",editTextA2.text.toString(),
                "domicilio",editTextA3.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(this,"SE ACTUALIZO",Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setTitle("ERROR")
                        .setMessage("NO SE PUDO ACTUALIZAR")
                        .setPositiveButton("OK"){d,i->}
                        .show()
                }
        }
        button2.setOnClickListener {
            finish()
        }
    }
}