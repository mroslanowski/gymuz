package com.example.gymuz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import org.maplibre.android.annotations.MarkerOptions

class GymMap : Fragment(), OnMapReadyCallback {

    private var mapView: MapView? = null
    private var maplibreMap: MapLibreMap? = null

    data class GymLocation(val name: String, val lat: Double, val lon: Double)

    private val zielonaGoraGyms = listOf(
        GymLocation("Funfit II Makowa 14", 51.90641432210927, 15.507526632656969),
        GymLocation("8K Centrum Sportu", 51.92156663931992, 15.512902094031643),
        GymLocation("Panteon Gym II", 51.92686078083327, 15.508363349388388),
        GymLocation("Alpha Gym", 51.94016221758262, 15.467922321526668),
        GymLocation("Fitness Park", 51.93335822592118, 15.48076263412257),
        GymLocation("Siłownia zewnętrzna przy VI Liceum", 51.92773204317605, 15.486752037127602),
        GymLocation("Siłownia zewnętrzna na Piast Polanie", 51.92496541016539, 15.488144865894736),
        GymLocation("Street Workout park", 51.92564631606819, 15.492760436026094),
        GymLocation("Akademia Ruchu nessfit Ewa Skorupka", 51.930647137137036, 15.493801792804561),
        GymLocation("JUST FIT", 51.92897769077887, 15.496235923855648),
        GymLocation("Funfit II Moniuszki 16", 51.934078324775, 15.500506751796554),
        GymLocation("Funfit II Pod Topolami", 51.941499168663796, 15.506633512946898),
        GymLocation("Siłownia NIKOGYM", 51.942384961366, 15.507575074868425),
        GymLocation("Yama Studio Treningu Personalnego", 51.93296396723015, 15.50905376910515),
        GymLocation("Natafitstudio", 51.932899519384115, 15.506809199901518),
        GymLocation("SALUS Park", 51.9339383608279, 15.507074392182906),
        GymLocation("Fabryka Formy", 51.93516112436158, 15.510169436355115),
        GymLocation("BodyRoom EMS", 51.94307056898072, 15.513965306798877),
        GymLocation("Panteon Gym", 51.933714391477764, 15.551680483966356),
        GymLocation("Funfit II Wrocławska 17", 51.93498419339352, 15.513137859761153),
        GymLocation("ABC treningu", 51.935981908457876, 15.525269844148816),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gym_map, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState) // Ważne dla cyklu życia MapView
        mapView?.getMapAsync(this) // Pobierz mapę asynchronicznie

        return view
    }

    override fun onMapReady(maplibreMap: MapLibreMap) {
        this.maplibreMap = maplibreMap

        // Użyj predefiniowanego stylu "Streets" z MapTiler
        // Dostępne są też inne, np. "Outdoor", "Basic", "Satellite", "Topo"
        maplibreMap.setStyle(Style.getPredefinedStyle("Streets")) { style ->
            // Styl załadowany - mapa jest gotowa

            // Ustaw pozycję kamery (Twój istniejący kod)
            val zielonaGora = LatLng(51.9356, 15.5064)
            val position = CameraPosition.Builder()
                .target(zielonaGora)
                .zoom(13.0) // Możesz dostosować zoom dla widoku ulic
                .build()
            maplibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1500)


            addGymMarkers(maplibreMap)

            // Opcjonalnie: Włącz lokalizację użytkownika (Twój istniejący kod)
            // enableLocationComponent(style)
        }
    }

    private fun addGymMarkers(map: MapLibreMap) {
        zielonaGoraGyms.forEach { gym ->
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(gym.lat, gym.lon))
                    .title(gym.name)
                // .snippet("Dodatkowe informacje") // Opcjonalny opis
            )
        }
    }

    // --- Zarządzanie cyklem życia MapView ---
    // To jest BARDZO WAŻNE, aby uniknąć wycieków pamięci i problemów

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
        mapView?.onDestroy()
        // Zapobiegaj wyciekom pamięci
        mapView = null
        maplibreMap = null
    }

    // Opcjonalnie: Włączanie warstwy lokalizacji użytkownika
    /*
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Sprawdź, czy uprawnienia lokalizacyjne są przyznane
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

            val locationComponent = maplibreMap?.locationComponent
            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(requireContext(), loadedMapStyle)
                .useDefaultLocationEngine(true) // Użyj domyślnego silnika lokalizacji
                .build()

            locationComponent?.activateLocationComponent(locationComponentActivationOptions)

            // Włącz widoczność komponentu lokalizacji (kropka/strzałka)
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               // Tutaj poproś o uprawnienia, jeśli nie są przyznane
               // ActivityCompat.requestPermissions(...)
                return
            }
            locationComponent?.isLocationComponentEnabled = true

            // Ustaw tryb kamery komponentu lokalizacji
            // locationComponent?.cameraMode = CameraMode.TRACKING
            // locationComponent?.renderMode = RenderMode.COMPASS

        } else {
             // Poproś użytkownika o uprawnienia (implementacja zależy od Ciebie)
             // np. używając ActivityResultLauncher
             // permissionsManager = PermissionsManager(this) // Potrzebny listener
             // permissionsManager.requestLocationPermissions(activity)
             println("Uprawnienia lokalizacyjne nie zostały przyznane.")
        }
    }
    */
    // Dodaj obsługę odpowiedzi na prośbę o uprawnienia (onRequestPermissionsResult), jeśli implementujesz lokalizację.
}