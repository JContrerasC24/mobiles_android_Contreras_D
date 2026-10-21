# PROMPTS.md: Fase 2, Mejora con IA

Asistente utilizado: Claude (Anthropic)

## Prompt 1: Planteamiento de la mejora
**Prompt:**
> Mejora la interfaz con IA: agrega un badge con contador en el ítem "Favoritos" del drawer, mostrando cuántos productos marcó el usuario como favorito desde el DropdownMenu de cada producto. Una acción en el DropdownMenu debe reflejarse visualmente en el Drawer. Trabaja en la rama mejora-ia con mínimo 3 commits.

**Resultado:** La IA propuso elevar el estado de favoritos a `AppNavegacion` (state hoisting) y compartirlo con `TarjetaProducto` y `AppDrawer`.

## Prompt 2: Estado y acción en el DropdownMenu
**Prompt:** (describe aquí lo que pediste para alternar favorito en el menú)
**Resultado:** `TarjetaProducto` recibe `esFavorito` y `onToggleFavorito`; la opción cambia entre "Favoritos" y "Quitar de favoritos".

## Prompt 3: Badge en el drawer
**Prompt:** (describe aquí lo que pediste para el badge)
**Resultado:** `AppDrawer` recibe `cantidadFavoritos` y muestra un `Badge` en el ítem Favoritos.

## Prompt 4: Pantalla Favoritos
**Prompt:** (describe aquí lo que pediste)
**Resultado:** `PantallaFavoritos` lista solo los productos marcados.

## Ajustes manuales
- (Escribe aquí qué cambiaste o corregiste tú, por ejemplo errores de imports o paquetes.)