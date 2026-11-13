# Cierre de mantenimiento en Cloud Firestore

Fragmento para integrar dentro de `match /databases/{database}/documents` en las reglas existentes. Requiere que el perfil de la cuenta esté en usuarios/{uid}, que solo un administrador autorizado pueda asignar roles y que la app escriba ambos documentos en una transacción. Revisar con las reglas publicadas antes de desplegar.

```javascript
function adminDeTaller() {
  return request.auth != null
    && get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.rol == 'admin';
}

function cierreValido(id, mid, antes, despues, mov) {
  return mov.keys().hasAll(['herramientaId', 'codigoHerramienta', 'nombreHerramienta',
      'tipo', 'responsable', 'registradoPorUid', 'fecha', 'condicion', 'observacion'])
    && mov.keys().hasOnly(['herramientaId', 'codigoHerramienta', 'nombreHerramienta',
      'tipo', 'responsable', 'registradoPorUid', 'fecha', 'condicion', 'observacion'])
    && antes.condicion == 'MANTENIMIENTO'
    && antes.disponibilidad == 'DISPONIBLE'
    && antes.get('prestamoActivoId', null) in [null, '']
    && despues.diff(antes).affectedKeys().hasOnly(['condicion', 'ultimoMovimientoId'])
    && despues.condicion in ['BUENA', 'DANADA']
    && despues.ultimoMovimientoId == mid
    && mov.tipo == 'FIN_MANTENIMIENTO'
    && mov.herramientaId == id
    && mov.codigoHerramienta == antes.codigo && mov.nombreHerramienta == antes.nombre
    && mov.condicion == despues.condicion
    && mov.registradoPorUid == request.auth.uid
    && mov.responsable == request.auth.token.email
    && mov.fecha == request.time
    && mov.observacion is string && mov.observacion.size() > 0 && mov.observacion.size() <= 500;
}

match /herramientas/{id} {
  allow update: if adminDeTaller()
    && request.resource.data.ultimoMovimientoId is string
    && !exists(/databases/$(database)/documents/movimientos/$(request.resource.data.ultimoMovimientoId))
    && cierreValido(id, request.resource.data.ultimoMovimientoId, resource.data, request.resource.data,
      getAfter(/databases/$(database)/documents/movimientos/$(request.resource.data.ultimoMovimientoId)).data);
}

match /movimientos/{mid} {
  allow create: if adminDeTaller()
    && request.resource.data.tipo == 'FIN_MANTENIMIENTO'
    && request.resource.data.herramientaId is string
    && cierreValido(request.resource.data.herramientaId, mid,
      get(/databases/$(database)/documents/herramientas/$(request.resource.data.herramientaId)).data,
      getAfter(/databases/$(database)/documents/herramientas/$(request.resource.data.herramientaId)).data,
      request.resource.data);
}
```
