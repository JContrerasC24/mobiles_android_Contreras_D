# PROMPTS.md - Fase 2 (rama mejora-ia)

Mejora implementada: cancelar una cita Confirmada desde "Mis citas",
con un AlertDialog de confirmación antes de aplicar el cambio.

## Prompt 1 — Implementación de la mejora

**Lo que pedí:**
"Implementa una mejora funcional para el módulo de citas: permitir
cancelar una cita que está en estado Confirmada, mostrando un
AlertDialog de confirmación antes de aplicar el cambio. Debe
integrarse con el estilo visual ya existente (Material3, paleta
morada) y sin usar ViewModel, siguiendo el mismo patrón
remember/mutableStateOf que ya uso en el resto del proyecto."

**Lo que la IA generó (resumen):**
- Un nuevo valor `CANCELADA` en el enum `EstadoCita`.
- Un botón "Cancelar" en `CitaCard`, visible solo cuando la cita está
  en estado `CONFIRMADA`.
- Un `AlertDialog` en `MisCitasScreen`, controlado por una sola
  variable `citaACancelar: Cita?` (null = diálogo cerrado), que al
  confirmar cambia `cita.estado` a `CANCELADA`.

**Qué tuve que corregir:**
Definí yo mismo los colores del nuevo badge (`RojoCancelada` /
`RojoCanceladaBg`) para que combinaran con la paleta morada que ya
tenía en `Color.kt`, en vez de dejar un color genérico.

## Prompt 2 — Aclaración sobre dónde aplicar los cambios

**Lo que pedí:**
"¿Estos cambios se deben implementar en archivos nuevos, o se
trabajan directamente sobre los archivos que ya tengo (CitaCard.kt y
MisCitasScreen.kt)?"

**Lo que la IA respondió (resumen):**
Confirmó que se editan los archivos existentes, no se crean nuevos,
porque la mejora extiende una funcionalidad que ya existía. Explicó
que al estar parado en la rama `mejora-ia` (creada desde `main`), Git
mantiene automáticamente dos versiones separadas de esos archivos —
una en `main` sin la mejora y otra en `mejora-ia` con ella — por lo
que editar los archivos originales no afecta lo ya entregado en la
Fase 1.

**Qué tuve que corregir:**
Nada de código; fue una validación conceptual antes de tocar los
archivos, para no arriesgar el trabajo ya subido en `main`.

## Prompt 3 — Corrección de un error puntual

**Lo que pedí:**
"Me da error de `dp` en MisCitasScreen.kt, corrígelo, pero no muevas
nada más del código."

**Lo que la IA generó (resumen):**
Identificó que faltaba el import `androidx.compose.ui.unit.dp` y
devolvió el archivo completo con esa única línea agregada, sin tocar
el resto de la lógica ni el formato existente.

**Qué tuve que corregir:**
Nada adicional; solo verifiqué línea por línea que el resto del
archivo quedara idéntico antes de reemplazarlo, tal como pedí.

---

**Conclusión:** la mejora se construyó en 3 intercambios: uno para
generar la funcionalidad completa, uno de aclaración conceptual sobre
el flujo de ramas en Git, y uno de corrección de un error de
compilación. En los tres casos revisé el código generado antes de
aceptarlo y ajusté detalles (colores, verificación de que no se
alterara nada fuera de lo pedido) para que se integrara correctamente
con el resto del proyecto.