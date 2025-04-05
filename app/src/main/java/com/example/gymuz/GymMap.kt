package com.example.gymuz // Upewnij się, że pakiet jest poprawny

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.example.gymuz.databinding.FragmentGymMapBinding // Importuj ViewBinding (zalecane)
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import android.graphics.PointF // Potrzebne dla queryRenderedFeatures

class GymMap : Fragment(), OnMapReadyCallback { // <-- Implementacja OnMapReadyCallback

    // Użyj ViewBinding dla bezpiecznego dostępu do widoków
    private var _binding: FragmentGymMapBinding? = null
    private val binding get() = _binding!! // Ta właściwość jest ważna tylko między onCreateView a onDestroyView.

    private var mapView: MapView? = null // Zmienione na var i nullable
    private var maplibreMap: MapLibreMap? = null // Zmienione na var i nullable

    // Stałe dla ID źródeł, warstw i ikon
    private companion object {
        const val GYM_SOURCE_ID = "gym-source"
        const val GYM_LAYER_ID = "gym-layer"
        const val GYM_ICON_ID = "gym-icon"
    }

    // Lista przechowująca dane siłowni
    private val gymFeatures = mutableListOf<Feature>()

    // --- Cykl życia Fragmentu ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicjalizacja MapLibre (ważne, by zrobić to wcześnie)
        // Używamy `requireContext()` bo w onCreate Fragment jest już przyłączony do Contextu
        MapLibre.getInstance(requireContext(), null)

        // Przygotuj dane siłowni (w realnej aplikacji z API/bazy)
        prepareGymData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Użyj ViewBinding do inflacji layoutu
        _binding = FragmentGymMapBinding.inflate(inflater, container, false)
        // Pobierz referencję do MapView z bindingu
        mapView = binding.mapView // Zakładając, że ID w XML to "mapView"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Przekaż stan zapisu do MapView
        mapView?.onCreate(savedInstanceState)
        // Pobierz mapę asynchronicznie, `this` odnosi się do Fragmentu,
        // który implementuje OnMapReadyCallback
        mapView?.getMapAsync(this)
    }

    // --- Implementacja OnMapReadyCallback ---

    override fun onMapReady(maplibreMap: MapLibreMap) {
        this.maplibreMap = maplibreMap // Zapisz referencję do mapy

        // Ustaw styl mapy (np. publiczny styl MapLibre)
        val styleUrl = "https://demotiles.maplibre.org/style.json"
        // Alternatywnie styl MapTiler (wymaga klucza API w string.xml lub BuildConfig)
        // val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=TWOJ_KLUCZ_MAPTILER"

        maplibreMap.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
            // Styl załadowany - czas dodać nasze dane

            // 1. Ustaw początkową pozycję kamery (np. centrum Twojego miasta)
            val initialPosition = CameraPosition.Builder()
                .target(LatLng(52.2297, 21.0122)) // Warszawa jako przykład
                .zoom(11.0)
                .build()
            maplibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(initialPosition), 1000)

            // 2. Dodaj ikonę dla siłowni do stylu
            addGymIconToStyle(style)

            // 3. Dodaj źródło danych GeoJSON z siłowniami
            addGymSourceToStyle(style)

            // 4. Dodaj warstwę do wyświetlania ikon/nazw siłowni
            addGymLayerToStyle(style)

            // 5. (Opcjonalnie) Dodaj listener kliknięć na markery
            addMapClickListener()

            Toast.makeText(requireContext(), "Mapa gotowa!", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Metody pomocnicze do konfiguracji mapy ---

    private fun prepareGymData() {
        // Przykładowe dane (zastąp prawdziwymi!)
        addGym("Siłownia Centralna", 52.231, 21.010)
        addGym("Fitness Klub Południe", 52.215, 21.025)
        addGym("Power Gym Północ", 52.245, 20.990)
    }

    private fun addGym(name: String, latitude: Double, longitude: Double) {
        val point = Point.fromLngLat(longitude, latitude)
        val properties = JsonObject().apply {
            addProperty("name", name)
            // Możesz dodać więcej właściwości
            // addProperty("address", "Ulica Przykładowa 1")
        }
        gymFeatures.add(Feature.fromGeometry(point, properties))
    }

    private fun addGymIconToStyle(style: Style) {
        // Upewnij się, że masz drawable `ic_gym_pin` w res/drawable
        getBitmapFromVectorDrawable(R.drawable.ic_gym_pin)?.let { iconBitmap ->
            style.addImage(GYM_ICON_ID, iconBitmap)
        } ?: run {
            Log.e("Gym_Map", "Nie udało się załadować ikony siłowni (R.drawable.ic_gym_pin).")
            // Można tu dodać fallback do domyślnej ikony, jeśli jest potrzebny
        }
    }

    private fun addGymSourceToStyle(style: Style) {
        val gymCollection = FeatureCollection.fromFeatures(gymFeatures)
        val gymSource = GeoJsonSource(GYM_SOURCE_ID, gymCollection)
        style.addSource(gymSource)
    }

    private fun addGymLayerToStyle(style: Style) {
        val gymLayer = SymbolLayer(GYM_LAYER_ID, GYM_SOURCE_ID).withProperties(
            // Ikona
            PropertyFactory.iconImage(GYM_ICON_ID),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconSize(0.8f),
            // Tekst (nazwa)
            PropertyFactory.textField(PropertyFactory.get("name")), // Pobierz 'name' z GeoJSON
            PropertyFactory.textSize(12f),
            PropertyFactory.textColor(ContextCompat.getColor(requireContext(), android.R.color.black)),
            PropertyFactory.textAnchor(PropertyFactory.TEXT_ANCHOR_TOP),
            PropertyFactory.textOffset(arrayOf(0f, 1.0f)), // Odsuń tekst pod ikonę
            PropertyFactory.textAllowOverlap(true),
            PropertyFactory.textIgnorePlacement(true)
        )
        style.addLayer(gymLayer)
    }

    private fun addMapClickListener() {
        maplibreMap?.addOnMapClickListener { point ->
            val screenPoint: PointF = maplibreMap?.projection?.toScreenLocation(point) ?: return@addOnMapClickListener false
            val features = maplibreMap?.queryRenderedFeatures(screenPoint, GYM_LAYER_ID)

            if (features != null && features.isNotEmpty()) {
                // Kliknięto na siłownię
                val clickedFeature = features[0]
                val gymName = clickedFeature.getStringProperty("name") ?: "Nieznana siłownia"
                // val address = clickedFeature.getStringProperty("address")

                Toast.makeText(requireContext(), "Kliknięto: $gymName", Toast.LENGTH_SHORT).show()

                // Tutaj logika otwierania szczegółów itp.

                true // Zdarzenie obsłużone
            } else {
                false // Zdarzenie nieobsłużone
            }
        }
    }

    // --- Konieczne metody cyklu życia dla MapView w Fragmncie ---

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ważne! Usuń listener kliknięć, gdy widok jest niszczony
        // maplibreMap?.removeOnMapClickListener(this::handleMapClick) // Jeśli używasz referencji do metody
        // Jeśli używasz lambdy jak powyżej, MapLibre powinien sobie poradzić, ale jawne usunięcie jest bezpieczniejsze
        // jeśli masz bardziej złożone listenery.

        mapView?.onDestroy() // Niszczymy MapView razem z widokiem fragmentu
        maplibreMap = null    // Czyścimy referencję do mapy
        mapView = null       // Czyścimy referencję do MapView
        _binding = null      // Czyścimy binding - bardzo ważne!
    }


    // --- Metoda pomocnicza do konwersji Drawable na Bitmap ---
    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        val drawable: Drawable = ContextCompat.getDrawable(requireContext(), drawableId) ?: return null
        val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()

        val bitmap = Bitmap.createBitmap(
            wrappedDrawable.intrinsicWidth,
            wrappedDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        wrappedDrawable.setBounds(0, 0, canvas.width, canvas.height)
        wrappedDrawable.draw(canvas)
        return bitmap
    }

    // Usunąłem metody newInstance i parametry ARG_PARAM, bo nie były używane w kontekście mapy.
    // Jeśli ich potrzebujesz do przekazywania danych do fragmentu, możesz je przywrócić.
}