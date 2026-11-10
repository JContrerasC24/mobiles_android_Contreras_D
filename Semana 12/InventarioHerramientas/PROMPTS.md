# InventarioHerramientas · v1.0

## Prompt mejorado

Rediseña InventarioHerramientas en Jetpack Compose tomando como referencia las cinco pantallas adjuntas. Adapta su jerarquía visual al uso real del taller. Usa una paleta sobria, texto legible y componentes coherentes en acceso, catálogo, detalle, favoritos y administración. Facilita encontrar una herramienta, consultar su disponibilidad, guardarla y volver al catálogo. Conserva los permisos de usuario y administrador, la autenticación y los flujos existentes de préstamo, devolución e historial. Utiliza datos reales del proyecto. Implementa los cambios en mejora-ia con al menos tres commits, incorpora tres pruebas unitarias del ViewModel y verifica la compilación y el arranque en el emulador. Documenta las consultas, los cambios, los aprendizajes y las verificaciones en PROMPTS.md. Abre un PR hacia main y publica v1.0 con el APK adjunto.

## Consultas y decisiones

- La petición inicial fue recuperar el proyecto y Gradle. El cierre al iniciar se diagnosticó con Logcat. Firebase Authentication requería procesar el archivo local google-services.json mediante Google Services.
- La petición de diseño usó como referencia una app de alquiler. Este inventario trabaja con préstamos y devoluciones. Las tarjetas muestran código, ubicación, condición y disponibilidad del registro.
- Para reducir el recorrido hasta una herramienta se incorporaron búsqueda por nombre o código, filtro de disponibles y acceso al detalle al tocar la tarjeta. El menú Opciones reúne actualización, cierre de sesión y administración según el rol.
- Acceso permite mostrar u ocultar la contraseña. Catálogo y favoritos comparten tarjetas y un control de favorito con descripción accesible. Detalle destaca nombre y código. Administración presenta los dos totales reales en tarjetas y conserva la confirmación de limpieza.
- Se aplicó un tema Material 3 de tonos verde petróleo, superficies claras y ámbar en los indicadores.

## Verificación y aprendizajes

- Gradle completó testDebugUnitTest y assembleDebug. Las tres pruebas de LoginViewModel comprueban recuperación de sesión, rechazo de contraseña vacía sin invocar autenticación y normalización del correo antes de autenticar.
- Se instaló el APK de depuración en el emulador. MainActivity abrió correctamente. Durante el rediseño se verificó el catálogo con una sesión de administrador, la búsqueda, el filtro y herramientas reales visibles.
- Separar el repositorio de autenticación permitió probar las decisiones del ViewModel sin depender de Firebase. Reutilizar la tarjeta mantiene la interacción con favoritos coherente entre pantallas.
- La verificación cubrió compilación, pruebas unitarias y arranque. No se ejecutaron operaciones reales de préstamo, devolución o limpieza de caché como parte de esta entrega.

## Reproducir

Colocar la configuración Firebase de la aplicación en app/google-services.json y configurar el SDK de Android. Desde la carpeta del proyecto ejecutar en Windows:

```cmd
gradlew.bat testDebugUnitTest assembleDebug
```

El APK se genera en app/build/outputs/apk/debug/app-debug.apk.
