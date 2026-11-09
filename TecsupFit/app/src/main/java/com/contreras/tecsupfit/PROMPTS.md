# Registro de Prompts - Mejora IA (Opción B - TECSUP Fit)

**Mejora implementada:** Agregado de retroalimentación visual (Snackbar) y un pequeño retraso simulando "procesamiento" antes de navegar a la confirmación, mejorando la experiencia de usuario (UX).

**Prompt utilizado:**
"Actúa como un experto en Jetpack Compose. Tengo una pantalla `DetalleScreen` con un botón 'Reservar cupo' que navega directamente a otra pantalla. Genera el código para envolver mi contenido en un Scaffold propio, agregar un `SnackbarHostState`, y que al presionar el botón se ejecute una corrutina que muestre un Snackbar con el mensaje 'Procesando reserva...', espere 1 segundo (delay), y luego ejecute la navegación."