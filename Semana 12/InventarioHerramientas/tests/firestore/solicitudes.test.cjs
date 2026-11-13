const fs = require('node:fs');
const {initializeTestEnvironment, assertSucceeds, assertFails} = require('@firebase/rules-unit-testing');
const {doc,setDoc,getDoc,updateDoc,writeBatch,serverTimestamp,collection,query,where,getDocs} = require('firebase/firestore');
(async () => {
 const env = await initializeTestEnvironment({projectId:'demo-toolrent',firestore:{host:'127.0.0.1',port:8180,rules:fs.readFileSync('../../firestore.rules','utf8')}});
 try {
  await env.clearFirestore();
  await env.withSecurityRulesDisabled(async ctx => {
   const db=ctx.firestore();
   for(const [uid,rol] of [['user1','usuario'],['user2','usuario'],['boss','admin']]) await setDoc(doc(db,'usuarios',uid),{rol});
   await setDoc(doc(db,'herramientas','HER-001'),{codigo:'HER-001',nombre:'Alicate',descripcion:'',ubicacion:'Taller',condicion:'BUENA',disponibilidad:'DISPONIBLE'});
  });
  const u=env.authenticatedContext('user1').firestore(), other=env.authenticatedContext('user2').firestore(), admin=env.authenticatedContext('boss').firestore();
  const sid='user1_HER-001', path='solicitudes/'+sid;
  const data={uid:'user1',herramientaId:'HER-001',nombreHerramienta:'Alicate',responsable:'Ana',dias:2,observacion:'Prueba',estado:'PENDIENTE',fecha:serverTimestamp(),actualizado:serverTimestamp()};
  await assertSucceeds(getDoc(doc(u,path)));
  await assertFails(setDoc(doc(other,path),data));
  await assertSucceeds(setDoc(doc(u,path),data));
  await assertFails(setDoc(doc(u,path),data));
  await assertFails(getDoc(doc(other,path)));
  await assertSucceeds(getDocs(query(collection(u,'solicitudes'),where('uid','==','user1'))));
  await assertFails(getDocs(collection(u,'solicitudes')));
  await assertFails(updateDoc(doc(u,path),{estado:'APROBADA',actualizado:serverTimestamp()}));
  await assertSucceeds(updateDoc(doc(admin,path),{estado:'APROBADA',actualizado:serverTimestamp()}));
  await assertFails(updateDoc(doc(admin,path),{estado:'COMPLETADA',movimientoId:'fake',actualizado:serverTimestamp()}));
  const pago={tarifaCentimos:700,dias:2,totalCentimos:1400,metodo:'EFECTIVO',referencia:'',estado:'PAGADO',moneda:'PEN'};
  function prestar(db,mid,completar=true,total=1400) {
   const b=writeBatch(db);
   b.set(doc(db,'movimientos',mid),{herramientaId:'HER-001',codigoHerramienta:'HER-001',nombreHerramienta:'Alicate',tipo:'SALIDA',responsable:'Ana',registradoPorUid:'boss',fecha:serverTimestamp(),condicion:'BUENA',observacion:'',prestamoId:mid,solicitudId:sid,alquiler:{...pago,totalCentimos:total}});
   b.update(doc(db,'herramientas','HER-001'),{disponibilidad:'PRESTADA',prestamoActivoId:mid,ultimoMovimientoId:mid});
   if(completar) b.update(doc(db,path),{estado:'COMPLETADA',movimientoId:mid,actualizado:serverTimestamp()});
   return b.commit();
  }
  await assertFails(prestar(u,'user-payment'));
  await assertFails(prestar(admin,'missing-completion',false));
  await assertFails(prestar(admin,'bad-total',true,1));
  await assertSucceeds(prestar(admin,'approved-payment'));
  await assertFails(prestar(admin,'duplicate-payment'));
  console.log('PASS: own request, no duplicate, private list, admin approval, atomic verified payment, invalid total and duplicate payment rejected.');
 } finally { await env.cleanup(); }
})().catch(e=>{console.error(e);process.exitCode=1});
