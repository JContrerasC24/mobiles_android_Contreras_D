\# CentroTareas - Laboratorio 14



\## Información del proyecto



\- Institución: TECSUP

\- Carrera: Diseño y Desarrollo de Software

\- Estudiante: Jose Abraham Contreras Cabrera

\- Curso: Programación en Móviles

\- Laboratorio: Semana 14 - Hilos en Android

\- Lenguaje: Kotlin

\- Interfaz: Jetpack Compose

\- Arquitectura: MVVM + Repository



\## 1. Descripción del proyecto



CentroTareas es una aplicación Android desarrollada para

comprender el funcionamiento de los hilos y las corrutinas.



El proyecto permite estudiar cómo ejecutar tareas que requieren

varios segundos sin afectar la capacidad de respuesta de la

interfaz de usuario.



Durante el laboratorio se implementarán tres funcionalidades:



1\. Una tarea con barra de progreso y cancelación.

2\. Un cálculo de números primos en segundo plano.

3\. Dos cargas simuladas para comparar la ejecución

&#x20;  secuencial y paralela.



\## 2. Arquitectura del proyecto



La aplicación utiliza MVVM + Repository.



\- Model: representa los estados de las operaciones.

\- View: muestra la interfaz mediante Jetpack Compose.

\- ViewModel: administra los estados y las tareas.

\- Repository: proporciona los datos y ejecuta las operaciones.



\## 3. Observación del Paso 1



\### Objetivo



Comprobar qué sucede cuando una tarea larga se ejecuta

directamente en el hilo principal de Android.



\### Prueba realizada



Se implementó una función llamada tareaLargaMala(),

que utiliza Thread.sleep(4000) para generar una espera

de cuatro segundos.



Se agregó un botón llamado "Tarea larga (MAL)" y

otro llamado "Tócame", que incrementa un contador.



\### ¿Qué ocurrió al ejecutar la tarea?



\[Escribe aquí lo que observaste al presionar los botones

durante los cuatro segundos.]



\### ¿Por qué no apareció inmediatamente "Trabajando..."?



\[Explica aquí qué ocurrió con la actualización de

la interfaz y el hilo principal.]



\### ¿Qué sucedería si la espera fuera de diez segundos?



\[Explica qué podría ocurrir si Android detecta que

la aplicación no responde durante demasiado tiempo.]



\## 4. Próximas implementaciones



En los siguientes pasos se reemplazará Thread.sleep()

por corrutinas para evitar que la interfaz se congele.



También se implementarán barras de progreso,

cancelación de tareas, cálculos en segundo plano

y operaciones paralelas.



\## 5. Evidencias



Las capturas de pantalla y los resultados de las pruebas

se incorporarán al finalizar la primera fase del laboratorio.

