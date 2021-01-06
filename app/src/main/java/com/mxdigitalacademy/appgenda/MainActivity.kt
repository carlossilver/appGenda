package com.mxdigitalacademy.appgenda

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private var listaVisual: ListView? = null
    private var adaptador: CustomAdapter? = null
    private var vistaBusqueda:SearchView? = null

    companion object{
        var listaObjContactos: ArrayList<ObjContacto> = ArrayList()

        fun agregarContacto(contacto: ObjContacto){
            listaObjContactos.add(contacto)
        }

        fun getContactoTelPrincipal(tel1: String): ObjContacto?{
            for (contacto in listaObjContactos){
                if (contacto.getTelefonoPrincipal() == tel1)
                    return contacto
            }
            return null
        }

        fun elimilarContactoPorTelefono(tel1: String){
            for (x:Int in 0 .. listaObjContactos.size){
                if (listaObjContactos[x].getTelefonoPrincipal() == tel1){
                    listaObjContactos.remove(listaObjContactos[x])
                    break
                }
            }
        }

        fun existeTelefonoEnAgenda(tel1: String): Boolean{
            var existe = false
            for (contacto in listaObjContactos){
                existe = existe or (contacto.getTelefonoPrincipal() == tel1)
            }
            return existe
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        habilitarSearchView(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addContact -> {
                val intent = Intent(this,NewContact::class.java)
                startActivity(intent)
                restaurarElemsVisuales()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun habilitarSearchView(menu: Menu?){
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val itemBusqueda = menu?.findItem(R.id.app_bar_search)
        vistaBusqueda = itemBusqueda?.actionView as SearchView
        vistaBusqueda?.queryHint = "Buscar"

        vistaBusqueda?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        vistaBusqueda?.setOnQueryTextFocusChangeListener { _, b ->
            if (!b)
                inicializarListView(listaObjContactos)
        }

        vistaBusqueda?.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0?.isEmpty()!!){
                    inicializarListView(listaObjContactos)
                    return false
                }
                else{
                    inicializarListView(adaptador?.filter(listaObjContactos,p0)!!)
                    return true
                }
            }

        })
    }

    private fun iniciarToolbar(){
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.app_name)
        setSupportActionBar(toolbar)
    }

    private fun inicializarListView(contactos: ArrayList<ObjContacto>){
        listaVisual = findViewById(R.id.listaContactos)

        adaptador = CustomAdapter(this, contactos)
        listaVisual?.adapter = adaptador
        accionesListView()
    }

    private fun restaurarElemsVisuales(){
        vistaBusqueda?.setQuery("",true)//borrar el input en el searchView
        inicializarListView(listaObjContactos)//restaurar la lista, y no queden datos obsoletos en pantalla
    }

    private fun accionesListView(){
        listaVisual?.setOnItemClickListener { view, _, i, _ ->
            val nroTelefonoClick= view[i].findViewById<TextView>(R.id.tvNumTelefono).text.toString()

            val intent = Intent(this,InfoContacto::class.java)
            intent.putExtra("nroTelefonoClick",nroTelefonoClick)
            startActivity(intent)

            restaurarElemsVisuales()
        }
    }

    private fun agregarContactosDePrueba(){
        if (listaObjContactos.isEmpty()){
            listaObjContactos.add(ObjContacto(R.drawable.walter_white,"Walter","White","3478598723","","ww@bb.com"))
            listaObjContactos.add(ObjContacto(R.drawable.jesse_pinkman,"Jesse","Pinkman","112783498","2734892893","jesseP@gmail.com"))
            listaObjContactos.add(ObjContacto(R.drawable.gus_fring,"Gus","Fring","1132456745","","fring@gmail.com"))
            listaObjContactos.add(ObjContacto(R.drawable.walter_white,"Walter","Fake","312398723","23423434","fake@kk.com"))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        agregarContactosDePrueba()
        iniciarToolbar()
        inicializarListView(listaObjContactos)
    }

    override fun onResume() {
        super.onResume()
        adaptador?.notifyDataSetChanged()
    }
}