# =====================================================================
# main.py
# Monopoly - Hardware de dados con RFID, botón, display y USB serial
#
# Máximo 4 jugadores.
#
# Protocolo con Java:
#
# PING
#   -> PONG
#
# REGISTRAR;J001
#   -> ESPERANDO_RFID;J001
#   -> RFID;J001;<UID>
#   -> RETIRAR_RFID;J001
#   -> RFID_RETIRADO;J001
#
# TIRAR;J001
#   -> ESPERANDO_RFID;J001
#   -> RFID_OK;J001
#   -> RETIRAR_RFID;J001
#   -> RFID_RETIRADO;J001
#   -> ESPERANDO_BOTON;J001
#   -> DADOS;J001;dado1;dado2;suma
#
# VALIDAR_PAGO;J001
#   -> ESPERANDO_RFID;J001
#   -> RFID_OK;J001
#   -> RETIRAR_RFID;J001
#   -> RFID_RETIRADO;J001
#   -> PAGO_OK;J001
# =====================================================================

from machine import Pin
from time import sleep_us, sleep_ms
from time import ticks_ms, ticks_diff
import random
import sys
from mfrc522 import MFRC522


# =====================================================================
# CLASE Display
# =====================================================================

class Display:

    NUMEROS = {
        0: "abcdef",
        1: "bc",
        2: "abdeg",
        3: "abcdg",
        4: "bcfg",
        5: "acdfg",
        6: "acdefg",
        7: "abc",
        8: "abcdefg",
        9: "abcdfg",
    }

    APAGADA = ""

    def __init__(
        self,
        PinesSegmentos,
        PinComun1,
        PinComun2,
        AnodoComun=True,
        TiempoSegmentoUs=1000
    ):
        self.Segmentos = {}

        for Letra, NumeroGp in PinesSegmentos.items():
            self.Segmentos[Letra] = Pin(NumeroGp, Pin.OUT)

        self.Comun1 = Pin(PinComun1, Pin.OUT)
        self.Comun2 = Pin(PinComun2, Pin.OUT)
        self.TiempoSegmentoUs = TiempoSegmentoUs

        self.SegOn = 0 if AnodoComun else 1
        self.SegOff = 1 - self.SegOn

        self.ComOn = 1 if AnodoComun else 0
        self.ComOff = 1 - self.ComOn

        self.Apagar()

    def Letras(self, Numero):
        return self.NUMEROS[Numero]

    def Apagar(self):
        for Segmento in self.Segmentos.values():
            Segmento.value(self.SegOff)

        self.Comun1.value(self.ComOff)
        self.Comun2.value(self.ComOff)

    def _DibujarCifra(self, Comun, Letras):
        if not Letras:
            return

        Comun.value(self.ComOn)

        for Letra in Letras:
            self.Segmentos[Letra].value(self.SegOn)
            sleep_us(self.TiempoSegmentoUs)
            self.Segmentos[Letra].value(self.SegOff)

        Comun.value(self.ComOff)

    def Mostrar(self, Letras1, Letras2, DuracionMs):
        self.Apagar()

        if not Letras1 and not Letras2:
            sleep_ms(DuracionMs)
            return

        Inicio = ticks_ms()

        while ticks_diff(ticks_ms(), Inicio) < DuracionMs:
            self._DibujarCifra(self.Comun1, Letras1)
            self._DibujarCifra(self.Comun2, Letras2)

    def MostrarNumero(self, Numero, DuracionMs):
        self.Mostrar(
            self.Letras(Numero // 10),
            self.Letras(Numero % 10),
            DuracionMs
        )

    def FijarCifra2(self, Letras2):
        self.Apagar()

        if not Letras2:
            return

        self.Comun2.value(self.ComOn)

        for Letra in Letras2:
            self.Segmentos[Letra].value(self.SegOn)

    def Parpadear(
        self,
        Letras1,
        Letras2,
        Veces,
        EncendidoMs,
        ApagadoMs
    ):
        for _ in range(Veces):
            self.Mostrar(
                Letras1,
                Letras2,
                EncendidoMs
            )

            self.Mostrar(
                self.APAGADA,
                self.APAGADA,
                ApagadoMs
            )


# =====================================================================
# CLASE Boton
# =====================================================================

class Boton:

    def __init__(self, NumeroGp):
        self.PinBoton = Pin(
            NumeroGp,
            Pin.IN,
            Pin.PULL_UP
        )

        self.EstadoAnterior = self.PinBoton.value()

    def FuePulsado(self):
        Valor = self.PinBoton.value()

        Pulsado = (
            Valor == 0
            and self.EstadoAnterior == 1
        )

        self.EstadoAnterior = Valor

        return Pulsado

    def Ignorar(self):
        self.EstadoAnterior = self.PinBoton.value()


# =====================================================================
# CLASE LectorRFID
# =====================================================================

class LectorRFID:

    def __init__(
        self,
        Rst,
        Miso,
        Mosi,
        Sck,
        Sda,
        TiempoRetiroMs=500
    ):
        self.Lector = MFRC522(
            spi_id=None,
            sck=Sck,
            miso=Miso,
            mosi=Mosi,
            cs=Sda,
            rst=Rst
        )

        self.TiempoRetiroMs = TiempoRetiroMs

        self.TarjetaActual = None

        self.UltimaLectura = 0

    def Version(self):
        return self.Lector.version()

    def EstaConectado(self):
        return self.Version() not in (
            0x00,
            0xFF
        )

    @staticmethod
    def _UidATexto(Uid):
        return "".join(
            "{:02X}".format(b)
            for b in Uid[:4]
        )

    def _Leer(self):
        Estado, _ = self.Lector.request(
            self.Lector.REQALL
        )

        if Estado != self.Lector.OK:
            return None

        Estado, Uid = self.Lector.anticoll()

        if Estado != self.Lector.OK:
            return None

        return self._UidATexto(Uid)

    def TarjetaNueva(self):
        UidTexto = self._Leer()

        Ahora = ticks_ms()

        if UidTexto is not None:
            self.UltimaLectura = Ahora

            if UidTexto != self.TarjetaActual:
                self.TarjetaActual = UidTexto

                return UidTexto

            return None

        if (
            self.TarjetaActual is not None
            and ticks_diff(
                Ahora,
                self.UltimaLectura
            ) > self.TiempoRetiroMs
        ):
            self.TarjetaActual = None

        return None

    def Olvidar(self):
        self.TarjetaActual = None

    def EsperarRetiro(self):
        """
        Espera hasta que no haya ninguna tarjeta sobre el lector
        durante al menos TiempoRetiroMs.
        """

        InicioSinTarjeta = None

        while True:
            # OJO: el método se llama _Leer con L mayúscula.
            Uid = self._Leer()

            Ahora = ticks_ms()

            if Uid is None:
                if InicioSinTarjeta is None:
                    InicioSinTarjeta = Ahora

                elif ticks_diff(
                    Ahora,
                    InicioSinTarjeta
                ) >= self.TiempoRetiroMs:
                    self.TarjetaActual = None
                    return

            else:
                # Todavía hay una tarjeta sobre el lector.
                InicioSinTarjeta = None

            sleep_ms(40)


# =====================================================================
# CLASE JugadorHardware
# =====================================================================

class JugadorHardware:

    def __init__(
        self,
        Identificador,
        Uid
    ):
        self.Identificador = Identificador
        self.Uid = Uid

    def EsSuTarjeta(self, Uid):
        return self.Uid == Uid


# =====================================================================
# CLASE Dados
# =====================================================================

class Dados:

    PAUSAS_RODANDO = [
        50,
        50,
        50,
        60,
        70,
        80,
        100,
        120,
        150,
        190,
        240,
        300
    ]

    PAUSA_ENTRE_DADOS = 600

    def __init__(self, PantallaDisplay):
        self.Pantalla = PantallaDisplay

        self.Dado1 = 0
        self.Dado2 = 0

    def Suma(self):
        return self.Dado1 + self.Dado2

    def EsDoble(self):
        return self.Dado1 == self.Dado2

    def _MostrarDado(
        self,
        NumeroDado,
        LetrasDado,
        LetrasOtra,
        DuracionMs
    ):
        if NumeroDado == 1:
            self.Pantalla.Mostrar(
                LetrasDado,
                LetrasOtra,
                DuracionMs
            )

        else:
            self.Pantalla.Mostrar(
                LetrasOtra,
                LetrasDado,
                DuracionMs
            )

    def _LanzarUno(
        self,
        NumeroDado,
        LetrasOtra
    ):
        Resultado = random.randint(
            1,
            6
        )

        Anterior = 0

        for Pausa in self.PAUSAS_RODANDO:
            Cara = random.randint(
                1,
                6
            )

            while Cara == Anterior:
                Cara = random.randint(
                    1,
                    6
                )

            Anterior = Cara

            self._MostrarDado(
                NumeroDado,
                self.Pantalla.Letras(Cara),
                LetrasOtra,
                Pausa
            )

        LetrasResultado = self.Pantalla.Letras(
            Resultado
        )

        for _ in range(2):
            self._MostrarDado(
                NumeroDado,
                LetrasResultado,
                LetrasOtra,
                150
            )

            self._MostrarDado(
                NumeroDado,
                Display.APAGADA,
                LetrasOtra,
                80
            )

        self._MostrarDado(
            NumeroDado,
            LetrasResultado,
            LetrasOtra,
            self.PAUSA_ENTRE_DADOS
        )

        return Resultado

    def Tirar(self):
        P = self.Pantalla

        # Apaga primero.
        P.Mostrar(
            Display.APAGADA,
            Display.APAGADA,
            500
        )

        # Señal de inicio.
        P.Parpadear(
            P.Letras(8),
            P.Letras(8),
            3,
            250,
            250
        )

        # Dado 1.
        self.Dado1 = self._LanzarUno(
            1,
            Display.APAGADA
        )

        # Dado 2.
        self.Dado2 = self._LanzarUno(
            2,
            P.Letras(self.Dado1)
        )

        # Ambos resultados parpadean.
        P.Parpadear(
            P.Letras(self.Dado1),
            P.Letras(self.Dado2),
            3,
            300,
            200
        )

        return self.Suma()


# =====================================================================
# CLASE ControlHardware
# =====================================================================

class ControlHardware:

    MAX_JUGADORES = 4

    def __init__(
        self,
        Pantalla,
        BotonJuego,
        Lector
    ):
        self.Pantalla = Pantalla

        self.BotonJuego = BotonJuego

        self.Lector = Lector

        self.ParDados = Dados(
            Pantalla
        )

        # Ejemplo:
        #
        # "J001" -> JugadorHardware("J001", "F6EAF99D")
        # "J002" -> JugadorHardware("J002", "DA4F0202")
        #
        self.Jugadores = {}

    def BuscarJugadorPorUid(
        self,
        Uid
    ):
        for JugadorGuardado in self.Jugadores.values():
            if JugadorGuardado.EsSuTarjeta(Uid):
                return JugadorGuardado

        return None

    def EsperarTarjeta(self):
        self.Lector.Olvidar()

        while True:
            Uid = self.Lector.TarjetaNueva()

            if Uid is not None:
                return Uid

            sleep_ms(40)

    def EsperarTarjetaCorrecta(
        self,
        JugadorEsperado
    ):
        self.Lector.Olvidar()

        while True:
            Uid = self.Lector.TarjetaNueva()

            if Uid is not None:
                if JugadorEsperado.EsSuTarjeta(Uid):
                    return

                print(
                    "RFID_INCORRECTO;{};{}".format(
                        JugadorEsperado.Identificador,
                        Uid
                    )
                )

            sleep_ms(40)

    def EsperarBoton(self):
        self.BotonJuego.Ignorar()

        while True:
            if self.BotonJuego.FuePulsado():
                sleep_ms(30)
                return

            sleep_ms(10)

    # -----------------------------------------------------------------
    # REGISTRO DE RFID
    # -----------------------------------------------------------------

    def RegistrarJugador(
        self,
        Identificador
    ):
        # No registrar el mismo identificador dos veces.
        if Identificador in self.Jugadores:
            print(
                "ERROR;JUGADOR_YA_REGISTRADO;{}".format(
                    Identificador
                )
            )

            return

        # Máximo cuatro jugadores.
        if len(self.Jugadores) >= self.MAX_JUGADORES:
            print(
                "ERROR;MAXIMO_JUGADORES"
            )

            return

        print(
            "ESPERANDO_RFID;"
            + Identificador
        )

        Uid = self.EsperarTarjeta()

        DueñoActual = self.BuscarJugadorPorUid(
            Uid
        )

        # La misma tarjeta no puede pertenecer a dos jugadores.
        if DueñoActual is not None:
            print(
                "ERROR;RFID_YA_REGISTRADO;{}".format(
                    DueñoActual.Identificador
                )
            )

            return

        self.Jugadores[
            Identificador
        ] = JugadorHardware(
            Identificador,
            Uid
        )

        # Java guarda el UID recibido.
        print(
            "RFID;{};{}".format(
                Identificador,
                Uid
            )
        )

        # IMPORTANTE:
        # No iniciamos el siguiente registro hasta que la tarjeta
        # haya sido retirada físicamente.
        print(
            "RETIRAR_RFID;"
            + Identificador
        )

        self.Lector.EsperarRetiro()

        print(
            "RFID_RETIRADO;"
            + Identificador
        )

    # -----------------------------------------------------------------
    # TIRO DE DADOS
    # -----------------------------------------------------------------

    def ProcesarTiro(
        self,
        Identificador
    ):
        if Identificador not in self.Jugadores:
            print(
                "ERROR;JUGADOR_NO_REGISTRADO;{}".format(
                    Identificador
                )
            )

            return

        JugadorActual = self.Jugadores[
            Identificador
        ]

        # 1. Validación física por RFID.
        print(
            "ESPERANDO_RFID;"
            + Identificador
        )

        self.EsperarTarjetaCorrecta(
            JugadorActual
        )

        print(
            "RFID_OK;"
            + Identificador
        )

        # 2. Para evitar que la misma tarjeta quede sobre el lector
        #    y cause lecturas accidentales en el siguiente turno.
        print(
            "RETIRAR_RFID;"
            + Identificador
        )

        self.Lector.EsperarRetiro()

        print(
            "RFID_RETIRADO;"
            + Identificador
        )

        # 3. Esperar el botón físico.
        print(
            "ESPERANDO_BOTON;"
            + Identificador
        )

        self.EsperarBoton()

        # 4. Animación y generación de los dos dados.
        Suma = self.ParDados.Tirar()

        # 5. Mostrar la suma en los dos displays durante 5 segundos.
        self.Pantalla.MostrarNumero(
            Suma,
            5000
        )

        # 6. Informar el resultado a Java.
        print(
            "DADOS;{};{};{};{}".format(
                Identificador,
                self.ParDados.Dado1,
                self.ParDados.Dado2,
                Suma
            )
        )

    # -----------------------------------------------------------------
    # VALIDACIÓN DE PAGOS
    # -----------------------------------------------------------------

    def ValidarPago(
        self,
        Identificador
    ):
        if Identificador not in self.Jugadores:
            print(
                "ERROR;JUGADOR_NO_REGISTRADO;{}".format(
                    Identificador
                )
            )

            return

        JugadorPagador = self.Jugadores[
            Identificador
        ]

        # 1. Solo la tarjeta del jugador que paga autoriza el pago.
        print(
            "ESPERANDO_RFID;"
            + Identificador
        )

        self.EsperarTarjetaCorrecta(
            JugadorPagador
        )

        print(
            "RFID_OK;"
            + Identificador
        )

        # 2. Esperar a que retire la tarjeta antes de la siguiente lectura.
        print(
            "RETIRAR_RFID;"
            + Identificador
        )

        self.Lector.EsperarRetiro()

        print(
            "RFID_RETIRADO;"
            + Identificador
        )

        # 3. Informar a Java que el pago quedó autorizado.
        print(
            "PAGO_OK;"
            + Identificador
        )

    # -----------------------------------------------------------------
    # PROTOCOLO USB
    # -----------------------------------------------------------------

    def ProcesarComando(
        self,
        Comando
    ):
        if Comando == "PING":
            print("PONG")
            return

        Partes = Comando.split(";")

        if (
            len(Partes) == 2
            and Partes[0] == "REGISTRAR"
        ):
            self.RegistrarJugador(
                Partes[1]
            )

            return

        if (
            len(Partes) == 2
            and Partes[0] == "TIRAR"
        ):
            self.ProcesarTiro(
                Partes[1]
            )

            return

        if (
            len(Partes) == 2
            and Partes[0] == "VALIDAR_PAGO"
        ):
            self.ValidarPago(
                Partes[1]
            )

            return

        print(
            "ERROR;COMANDO_INVALIDO"
        )

    def Ejecutar(self):
        if not self.Lector.EstaConectado():
            print(
                "ERROR;RFID_NO_DETECTADO"
            )

            return

        print(
            "HARDWARE_LISTO"
        )

        while True:
            Comando = sys.stdin.readline()

            if Comando is None:
                continue

            Comando = Comando.strip()

            if Comando:
                self.ProcesarComando(
                    Comando
                )


# =====================================================================
# PROGRAMA PRINCIPAL
# =====================================================================

PantallaPrincipal = Display(
    PinesSegmentos={
        "a": 15,
        "b": 13,
        "c": 18,
        "d": 20,
        "e": 19,
        "f": 14,
        "g": 21
    },

    PinComun1=17,

    PinComun2=16,

    AnodoComun=True
)


BotonPrincipal = Boton(
    22
)


LectorPrincipal = LectorRFID(
    Rst=2,
    Miso=3,
    Mosi=4,
    Sck=5,
    Sda=6
)


ControlPrincipal = ControlHardware(
    PantallaPrincipal,
    BotonPrincipal,
    LectorPrincipal
)


try:
    ControlPrincipal.Ejecutar()

finally:
    PantallaPrincipal.Apagar()
