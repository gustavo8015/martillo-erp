/* Capa de acceso a la API REST de Martillo ERP.
   Separada de la lógica de pantalla (SRP): si cambia la URL o el transporte,
   solo se modifica este archivo. */
const API = (function () {
  const BASE = (window.MARTILLO_API_URL || 'http://localhost:8080') + '/api';
  let modoDemo = false;

  // Datos de respaldo para poder demostrar la interfaz sin el backend levantado.
  const demo = {
    piezas: [
      { id: 1, nombre: 'Jarrón de porcelana', descripcion: 'Jarrón chino del siglo XIX', categoria: 'Antigüedades', precioReserva: 1500000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-01T09:15:00' },
      { id: 2, nombre: 'Óleo sobre lienzo, paisaje', descripcion: 'Escuela colombiana, firmado', categoria: 'Arte', precioReserva: 4200000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-01T10:02:00' },
      { id: 3, nombre: 'Reloj de bolsillo en oro', descripcion: 'Movimiento suizo, con certificado', categoria: 'Joyería', precioReserva: 2800000, estado: 'DISPONIBLE', fechaRegistro: '2026-09-02T08:40:00' }
    ],
    consignantes: [
      { id: 1, nombreCompleto: 'Laura Gómez', tipoDocumento: 'CC', numeroDocumento: '123456789', telefono: '3001234567', email: 'laura@example.com', direccion: 'Calle 10 # 20-30, Bogotá', fechaRegistro: '2026-09-01T09:00:00' },
      { id: 2, nombreCompleto: 'Fundación Casa Antigua', tipoDocumento: 'NIT', numeroDocumento: '900123456-7', telefono: '6017654321', email: 'contacto@casaantigua.org', direccion: 'Carrera 7 # 45-12, Bogotá', fechaRegistro: '2026-09-01T11:30:00' }
    ]
  };

  function siguienteId(lista) {
    return lista.reduce((max, e) => Math.max(max, e.id), 0) + 1;
  }

  async function pedir(ruta, opciones) {
    const respuesta = await fetch(BASE + ruta, Object.assign({
      headers: { 'Content-Type': 'application/json' }
    }, opciones));
    if (!respuesta.ok) {
      let detalle = 'Error ' + respuesta.status;
      try {
        const cuerpo = await respuesta.json();
        detalle = cuerpo.mensaje || cuerpo.message || (cuerpo.errores && cuerpo.errores.join(', ')) || detalle;
      } catch (e) { /* la respuesta no traía cuerpo JSON */ }
      throw new Error(detalle);
    }
    return respuesta.status === 204 ? null : respuesta.json();
  }

  return {
    get enDemo() { return modoDemo; },

    async verificarConexion() {
      try {
        await pedir('/consignantes', { method: 'GET' });
        modoDemo = false;
      } catch (e) {
        modoDemo = true;
      }
      return !modoDemo;
    },

    async listarPiezas() {
      if (modoDemo) return demo.piezas.slice();
      return pedir('/piezas', { method: 'GET' });
    },

    async crearPieza(datos) {
      if (modoDemo) {
        const nueva = Object.assign({}, datos, {
          id: siguienteId(demo.piezas), estado: 'DISPONIBLE', fechaRegistro: new Date().toISOString()
        });
        demo.piezas.push(nueva);
        return nueva;
      }
      return pedir('/piezas', { method: 'POST', body: JSON.stringify(datos) });
    },

    async listarConsignantes() {
      if (modoDemo) return demo.consignantes.slice();
      return pedir('/consignantes', { method: 'GET' });
    },

    async crearConsignante(datos) {
      if (modoDemo) {
        const nuevo = Object.assign({}, datos, {
          id: siguienteId(demo.consignantes), fechaRegistro: new Date().toISOString()
        });
        demo.consignantes.push(nuevo);
        return nuevo;
      }
      return pedir('/consignantes', { method: 'POST', body: JSON.stringify(datos) });
    }
  };
})();
