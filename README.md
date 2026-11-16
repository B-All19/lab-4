# ArenaGT

Aplicación de consola para el laboratorio de polimorfismo. Incluye 26 archivos Java, sin dependencias externas y sin persistencia de datos.

## Requisitos y ejecución

Instalar un JDK 21 o posterior. Desde esta carpeta, ejecutar:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

En Windows se pueden ejecutar los mismos comandos desde PowerShell o CMD. En una terminal antigua, usar `chcp 65001` si los acentos no se muestran correctamente.

## Pruebas

```bash
java -cp out Pruebas
```

La última línea debe indicar 118 pruebas y todas aprobadas. Un fallo termina con `AssertionError`. Se incluye la evidencia real en `evidencia_pruebas.txt`, con caso, entrada, esperado y obtenido. La compilación se verificó con OpenJDK 21, usando `-Xlint:all`.

## Uso

El menú permite registrar participantes, crear torneos, cotizar, inscribir, cerrar inscripciones, programar y reprogramar eventos, registrar resultados y consultar reportes. Permanece disponible hasta elegir 0. Una entrada inválida muestra un mensaje y regresa al menú sin confirmar cambios.

Los códigos de evento se consultan con la opción 8. Fechas en formato `AAAA-MM-DD`, horas enteras de 0 a 23, marcadores enteros no negativos. Para relevos se escribe el tiempo con punto decimal o `D` para descalificado. En fútbol sala con penales, el menú solicita penales solamente si hay empate reglamentario.

## Datos iniciales

Hay ocho participantes de cuatro empresas. T1 es fútbol sala, T2 fútbol sala con penales, T3 baloncesto, T4 voleibol y T5 y T6 relevos. T1 a T4 están en juego y tienen todos sus eventos programados. T5 demuestra descalificaciones y T6 muestra los ocho puntajes de llegada. Ambos están finalizados desde la carga.

En T1, Alfa y Delta tienen 3 puntos y diferencia 0, pero Alfa tiene una victoria. Eco y Beta tienen 1 punto, con diferencias 0 y -1. T2 contiene una definición por penales. T4 tiene resultados 3-0, 3-1 y 3-2.

Los ingresos y los premios pagados comienzan en Q0.00. Se excluyen únicamente los premios que ya existían al terminar la precarga. Completar durante el uso normal un torneo que comenzó en juego sí genera su premio. Para probar inscripciones, crear un torneo nuevo y usar los participantes existentes o registrar otros.

## Diseño

`Disciplina` define límites, cuota, inscripción, generación de eventos y evaluación de resultados. `DisciplinaEncuentro` comparte parejas y marcadores. `FutbolSalaPenales` especializa `FutbolSala`. `Encuentro` y `Carrera` implementan `Evento` y comparten estado mediante composición con `FichaEvento`.

`Evento` implementa el orden por fecha, hora y sede mediante `Comparable`. `Calendario` cumple `Iterable` y ordena al iniciar cada recorrido. `FilaTabla` y `FilaEmpresa` implementan sus propios órdenes. `ArenaGT` valida los conflictos globales y confirma cambios después de validar. No se usa `instanceof` para decidir reglas deportivas.

Se normalizan códigos, empresas y sedes para evitar duplicados por mayúsculas o espacios. Los nombres se comparan alfabéticamente sin distinguir mayúsculas. Cuando dos filas empatan también por nombre, el código proporciona un orden estable. Las colecciones públicas son de solo lectura. El programa es de un solo usuario y no requiere concurrencia.

## Entregables

El análisis Word y el UML PNG se encuentran en `documentos`. El Word usa Aptos Display, 12 puntos, párrafos sin encabezados especiales y solo comas y puntos en su texto. El diagrama tiene cuatro paneles dentro de una sola imagen de alta resolución, se recomienda abrirlo y ampliar para leer los miembros. Los archivos DOT de los paneles se incluyen como fuente editable del diagrama.

El enunciado original pide un PDF que integre análisis, UML y pruebas, y la URL de un repositorio público. Este proyecto entrega el Word y PNG solicitados. No se ha publicado un repositorio. Para la entrega institucional, exportar e integrar los documentos en PDF y subir estos archivos a un repositorio propio.
