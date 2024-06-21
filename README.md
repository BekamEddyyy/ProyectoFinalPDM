# Piano Speed Challenge

Piano Tiles es un juego de ritmo clásico con opciones de velocidad ajustables disponibles desde el inicio. Disfruta de una experiencia de juego sin anuncios y liviana en este entretenido y desafiante juego musical.

## Características

- **Opciones de Velocidad Ajustables**: Ajusta la configuración de velocidad del juego para que coincida con tu nivel de habilidad y preferencias desde el comienzo.

- **Experiencia Sin Anuncios**: Disfruta de un juego ininterrumpido sin anuncios disruptivos.

- **Tamaño de Descarga Reducido**: Mantén el almacenamiento de tu dispositivo libre con este juego liviano.

## Funcionalidades

### Inicio de la Actividad de Juego (GameActivity)
- La clase `GameActivity` es el punto de entrada principal para iniciar el juego. Configura la vista del juego (`GameView`) y define las opciones personalizables como la velocidad, la música y la vibración.
- El método `onCreate` establece la interfaz de usuario y configura las opciones de velocidad, música y vibración según las preferencias del usuario.
- Elimina la barra de notificaciones para proporcionar una experiencia de pantalla completa.
- Maneja la visualización y ocultación del botón de reinicio del juego.

### Ejecución del Juego (GameThread y GameView)
- La clase `GameThread` controla el bucle principal del juego, asegurando una tasa de refresco de 60 fotogramas por segundo.
- La clase `GameView` contiene la lógica principal del juego, dibujando las líneas, las puntuaciones y actualizando los objetos del juego en cada fotograma.
- `GameView` también maneja los eventos táctiles para detectar cuando el jugador toca los azulejos correctos o incorrectos.

### Gestión de Azulejos (Tile)
- La clase `Tile` define los objetos azulejos que el jugador debe tocar.
- Los azulejos se mueven de arriba a abajo y se actualizan continuamente.
- Detectan cuando son tocados y cambian de color en consecuencia, además de gestionar el estado de "Game Over".

## Jugabilidad

Piano Tiles es un juego de ritmo clásico donde tu objetivo es tocar las teclas a medida que descienden por la pantalla. Toca las teclas correctas para ganar puntos y alcanzar altas puntuaciones.

¡Diviértete jugando y desafiándote a ti mismo en Piano Tiles!
