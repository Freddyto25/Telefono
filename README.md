# Teléfono Pro LZ

Marcador telefónico para Android, diseñado desde cero priorizando la accesibilidad
para personas ciegas y de baja visión (lectores de pantalla como Jieshuo/TalkBack).

## Compilación

Este repositorio incluye `.github/workflows/build.yml`: al subirlo a GitHub,
Actions compilará automáticamente un APK de depuración (`app-debug`) que podrás
descargar desde la pestaña "Actions" → el workflow ejecutado → "Artifacts".

Requisitos si compilas localmente: JDK 17, Android SDK (compileSdk 35),
Gradle 8.7 (se genera el wrapper automáticamente en CI).

## Nota importante sobre accesibilidad y toques (1 toque / 2 toques)

Este marcador **no** decide si tu lector de pantalla activa los elementos con
un toque o con doble toque: esa opción vive en la configuración del propio
lector de pantalla (Jieshuo/TalkBack → "Exploración táctil" / "modo de toque
directo"). Lo que sí garantiza esta app es que **todos** los botones (teclado,
contactos, llamadas recientes, etc.) están implementados como controles
estándar de Android (`Button`, `RecyclerView` con `View.OnClickListener`,
`contentDescription` correcto), en vez de zonas táctiles dibujadas a mano con
`onTouchEvent`. Ese es precisamente el motivo por el que muchos teclados de
terceros "no responden" a doble toque: interceptan el toque crudo y rompen la
integración con el framework de accesibilidad. Al usar los controles nativos,
Teléfono Pro LZ funciona igual de bien sea cual sea el modo configurado en el
lector de pantalla.

## Limitación conocida: "Finalizar llamada con el botón de encendido"

Android solo permite este comportamiento mediante el ajuste protegido del
sistema `Settings.System.INCALL_POWER_BUTTON_BEHAVIOR`, que requiere el
permiso `WRITE_SECURE_SETTINGS`. Ninguna app normal (no firmada por el
fabricante ni instalada como app del sistema) puede otorgárselo a sí misma;
solo se puede conceder por ADB:

```
adb shell pm grant com.lz.telefonoprolz android.permission.WRITE_SECURE_SETTINGS
```

El código ya incluye la lógica para leer/escribir ese ajuste si el permiso
está concedido (ver `PowerButtonEndCallHelper`), y explica esto al usuario en
Ajustes si el permiso no está disponible. Responder con la tecla de subir
volumen sí funciona sin permisos especiales, porque se maneja dentro de la
propia pantalla de llamada de la app.

## Qué incluye

- Teclado de marcación con soporte de extensión (marca el número, espera a
  que la llamada esté activa y marca automáticamente los DTMF de la
  extensión).
- Detección de doble SIM y selector de línea antes de llamar.
- Recientes (historial de llamadas) con hoja de acciones (Llamar, Mensaje,
  Info) e info de contacto con conteo de llamadas entrantes/salientes.
- Lista de contactos con búsqueda y alta de nuevo contacto.
- Servicio de llamada entrante/en curso accesible, con anunciador de
  llamadas por voz (Text-to-Speech).
- Filtrado de spam vía `CallScreeningService` + integración con Tasker
  mediante broadcasts (`com.lz.telefonoprolz.ACTION_INCOMING_CALL`).
- Ajustes: tema de alto contraste para baja visión, responder con volumen,
  responder por gesto o por toque, anunciador de llamadas.
- `minSdk 26` (Android 8.0) → `compileSdk/targetSdk 35` (Android 14/15,
  compatible con Android 16 cuando se publique el SDK correspondiente).

## Convertir en marcador predeterminado

`DefaultDialerHelper.kt` usa `RoleManager.ROLE_DIALER` en Android 10+ y
`TelecomManager.ACTION_CHANGE_DEFAULT_DIALER` como respaldo en versiones
anteriores.
