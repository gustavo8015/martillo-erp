# ADR-003. reCAPTCHA v3 verificado en el servidor

- **Estado:** aceptada
- **Fecha:** 24 de septiembre de 2026
- **Sprint:** 3 (HU-11)

## Contexto

El inicio de sesion necesita distinguir a una persona de un programa
automatico que intente adivinar contrasenas. La version 2 de reCAPTCHA
presenta un desafio visual de identificacion de imagenes, que es una prueba
cognitiva y puede chocar con el criterio 3.3.8 de WCAG 2.2, de autenticacion
accesible. El mismo sprint entrega un atributo de accesibilidad, de modo que
seria contradictorio introducir una barrera de ese tipo.

## Decision

Se usa reCAPTCHA v3, que no presenta desafio visual y devuelve una puntuacion
de 0,0 a 1,0 por cada solicitud. La verificacion se hace siempre en el
servidor y el intento se acepta solo si la respuesta indica exito, la accion
declarada es `login` y la puntuacion es de al menos 0,5. Cuando falla, el
sistema rechaza sin revelar el motivo y registra el evento en la pista de
auditoria.

## Consecuencias

- No se introduce una barrera cognitiva en el inicio de sesion.
- El sistema depende de un tercero: si Google no responde, la verificacion
  falla cerrada y nadie inicia sesion.
- La clave secreta se entrega por la variable de entorno
  `MARTILLO_RECAPTCHA_SECRETO` y nunca se escribe en el repositorio, en linea
  con la revision automatica de credenciales del flujo de integracion continua.
- Como reCAPTCHA envia senales del navegador a Google, la politica de
  tratamiento de datos del sistema debe informarlo.
- Queda pendiente registrar el dominio de la interfaz publicada y crear las
  claves en una cuenta de Google. Mientras tanto la interfaz emite un token de
  demostracion y el servidor aplica la misma regla de puntuacion.
