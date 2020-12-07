package mx.tecnm.tepic.ladm_u3_ejercicio3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var datos = ArrayList<String>()
    var listaID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        baseRemota.collection("persona")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException!=null){
                    mensaje("Error no se pudo recuperar data desde NUBE")
                    return@addSnapshotListener
                }
                datos.clear()
                listaID.clear()
                var cadena =""
                for(registro in querySnapshot!!){
                    cadena = "Nombre: ${registro.getString("nombre")}\nTelefono: ${registro.getString("telefono")}\nDomicilio ${registro.getString("domicilio")}"
                    datos.add(cadena)
                    listaID.add(registro.id)
                }
                var adaptador = ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,datos)
                listaV.adapter=adaptador
                listaV.setOnItemClickListener { parent, view, position, id ->
                    mostrarAlertEliminarActualizar(position)
                }

            }

        Button.setOnClickListener {
        insertar()
    }
}

    private fun mostrarAlertEliminarActualizar(posicion:Int) {
        var idLista = listaID.get(posicion)
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage("¿Que desea hacer con \n ${datos.get(posicion)}?")
            .setPositiveButton("Eliminar"){d,i-> eliminar(idLista)}
            .setNeutralButton("CANCELAR")  {d,i->}
            .setNegativeButton("ACTUALIZAR"){d,i->llamarVentanaActualizar(idLista)}
            .show()
    }

    private fun llamarVentanaActualizar(idLista: String) {
        //Racuperar los datos
        baseRemota.collection("persona")
            .document(idLista)
            .get()//sobre un id determinado
            .addOnSuccessListener {
                var ventana = Intent(this,MainActivity2::class.java)
                ventana.putExtra("id",idLista)
                ventana.putExtra("nombre",it.getString("nombre"))
                ventana.putExtra("telefono",it.getString("telefono"))
                ventana.putExtra("domicilio",it.getString("domicilio"))
                startActivity(ventana)
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message!!,Toast.LENGTH_LONG).show()
            }
    }

    private fun eliminar(idLista: String) {
        baseRemota.collection("persona")
            .document(idLista)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this,"SE ELIMINO CON EXITO EL USUARIO",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                mensaje("Error:NO SE ELIMINO\n"+it.message)
            }
    }

    private fun insertar() {
        var datosInsertar = hashMapOf(// se esta mapeando la informacion
            "nombre" to editText.text.toString(),
            "telefono" to editText2.text.toString(),
            "domicilio" to editText3.text.toString()
        )
        /*
        una coleccion es u conjunto de documentos
        pertenecientes a un mismo JSON
        que es un documento
        un conjunto de datos de tipo CLAVE = VALOR
        */
        baseRemota.collection("persona")
            .add(datosInsertar as Any)//convierte en un dato canonic
            .addOnSuccessListener {
                //Se ejecuta en caso de que la transacción sea exitosa
                Toast.makeText(this,"Se inserto correctamente${it.id}",Toast.LENGTH_LONG).show()
                editText.setText("")
                editText2.setText("")
                editText3.setText("")

            }
            .addOnFailureListener {
                // se ejeuta en caso de ERROR
                mensaje("No se pudo insertar\n${it.message}")
            }

    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this)
            .setMessage(s)
            .setTitle("Atecion")
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}