package com.contreras.carritopoo

/**
 * ABSTRACCIÓN + ENCAPSULAMIENTO
 * Clase abstracta que define el "contrato" común de todo producto.
 * No se puede instanciar directamente (abstract), solo sus subclases.
 * 'cantidad' es protected: visible para las subclases, pero no desde fuera.
 */
abstract class Producto(
    val nombre: String,
    val precio: Double,
    protected var cantidad: Int
) {
    fun obtenerCantidad(): Int = cantidad

    fun aumentarCantidad(unidades: Int) {
        cantidad += unidades
    }

    // POLIMORFISMO: cada subclase puede redefinir cómo se calcula el importe
    open fun calcularImporte(): Double = precio * cantidad

    // Método abstracto: cada subclase está OBLIGADA a implementarlo a su manera
    abstract fun mostrarInfoAdicional(): String

    override fun toString(): String = "$nombre (x$cantidad)"
}

/**
 * HERENCIA
 * ProductoElectronico hereda todo de Producto y añade su propio atributo.
 */
class ProductoElectronico(
    nombre: String,
    precio: Double,
    cantidad: Int,
    private val mesesGarantia: Int
) : Producto(nombre, precio, cantidad) {

    override fun mostrarInfoAdicional(): String =
        "Garantia: $mesesGarantia meses"
}

/**
 * HERENCIA + POLIMORFISMO
 * ProductoPerecible también hereda de Producto, pero SOBREESCRIBE
 * calcularImporte() con una regla propia: si está por vencer, aplica
 * un 10% de descuento automático. Esto es polimorfismo real: el mismo
 * método calcularImporte() se comporta distinto según la subclase.
 */
class ProductoPerecible(
    nombre: String,
    precio: Double,
    cantidad: Int,
    private val diasParaVencer: Int
) : Producto(nombre, precio, cantidad) {

    override fun calcularImporte(): Double {
        val importeBase = precio * cantidad
        return if (diasParaVencer <= 3) importeBase * 0.9 else importeBase
    }

    override fun mostrarInfoAdicional(): String =
        "Vence en: $diasParaVencer dias" +
                if (diasParaVencer <= 3) " (10% dcto. por proximo vencimiento)" else ""
}

/**
 * Carrito trabaja con el TIPO BASE (Producto), sin saber si el objeto
 * real es Electronico o Perecible. Cuando llama a calcularImporte(),
 * Kotlin ejecuta automáticamente la versión correcta según el objeto
 * real (esto es polimorfismo en acción, no solo en la teoría).
 */
class Carrito(private val cliente: String) {

    private val productos = mutableListOf<Producto>()

    fun agregarProducto(producto: Producto) {
        productos.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    fun eliminarProducto(nombre: String): Boolean {
        return productos.removeIf { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun buscarProducto(nombre: String): Producto? {
        return productos.find { it.nombre.equals(nombre, ignoreCase = true) }
    }

    fun calcularSubtotal(): Double = productos.sumOf { it.calcularImporte() }

    fun calcularIGV(subtotal: Double): Double = subtotal * 0.18

    fun calcularTotal(subtotal: Double, igv: Double): Double = subtotal + igv

    fun calcularDescuento(total: Double): Double {
        return when {
            total > 5000 -> total * 0.10
            total > 3000 -> total * 0.05
            else -> 0.0
        }
    }

    fun productoMasCaro(): Producto? = productos.maxByOrNull { it.precio }

    fun mostrarDetalle() {
        println("--------- DETALLE DEL CARRITO ---------")
        productos.forEachIndexed { index, producto ->
            println(
                String.format(
                    "%d. %-20s x%d S/ %8.2f  [%s]",
                    index + 1,
                    producto.nombre,
                    producto.obtenerCantidad(),
                    producto.calcularImporte(),
                    producto.mostrarInfoAdicional()
                )
            )
        }
        println("---------------------------------------")
    }

    fun generarReporte() {
        println("Cliente: $cliente")
        println()
        mostrarDetalle()
        println("Cantidad de productos: ${productos.size}")

        val subtotal = calcularSubtotal()
        val igv = calcularIGV(subtotal)
        val total = calcularTotal(subtotal, igv)

        println(String.format("Subtotal: S/ %.2f", subtotal))
        println(String.format("IGV (18%%): S/ %.2f", igv))
        println(String.format("TOTAL A PAGAR: S/ %.2f", total))

        productoMasCaro()?.let {
            println("Producto mas caro: ${it.nombre} " + String.format("(S/ %.2f)", it.precio))
        }

        val descuento = calcularDescuento(total)
        if (descuento > 0) {
            val totalConDescuento = total - descuento
            println(String.format("Descuento aplicado: S/ %.2f", descuento))
            println(String.format("TOTAL CON DESCUENTO: S/ %.2f", totalConDescuento))
        } else {
            println("No se aplico descuento (el total no supera S/ 3000)")
        }
    }
}

fun main() {
    println("=========================================")
    println(" CARRITO DE COMPRAS - TIENDA TECSUP (POO)")
    println("=========================================")

    val carrito = Carrito("Jose Contreras")

    // Se agregan objetos de DISTINTAS subclases, pero el carrito
    // los trata a todos como "Producto" (polimorfismo).
    carrito.agregarProducto(ProductoElectronico("Laptop HP", 2500.0, 1, mesesGarantia = 12))
    carrito.agregarProducto(ProductoElectronico("Mouse Logitech", 45.5, 2, mesesGarantia = 6))
    carrito.agregarProducto(ProductoPerecible("Yogurt Griego", 8.5, 5, diasParaVencer = 2))
    carrito.agregarProducto(ProductoPerecible("Pan Integral", 6.0, 3, diasParaVencer = 10))

    println()
    carrito.generarReporte()

    println()
    val encontrado = carrito.buscarProducto("Mouse Logitech")
    println(if (encontrado != null) "Encontrado: $encontrado" else "No encontrado")

    carrito.eliminarProducto("Mouse Logitech")
    println("\nCarrito actualizado tras eliminar Mouse Logitech:")
    carrito.generarReporte()
}