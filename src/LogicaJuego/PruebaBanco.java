package LogicaJuego;

public class PruebaBanco {

    public static void main(String[] args) {

        Jugador jose = new Jugador("J001", "Jose", 1500);
        Jugador luis = new Jugador("J002", "Luis", 1000);

        Banco banco = new Banco(10000);

        System.out.println("=== ESTADO INICIAL ===");
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());

        // Banco paga a Jose
        boolean pagoRealizado = banco.pagar(jose, 500);

        System.out.println("\n=== BANCO PAGA 500 A JOSE ===");
        System.out.println("Pago realizado: " + pagoRealizado);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());

        // Jose paga al banco
        boolean recepcionRealizada = banco.recibir(jose, 300);

        System.out.println("\n=== JOSE PAGA 300 AL BANCO ===");
        System.out.println("Recepción realizada: " + recepcionRealizada);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Jose: " + jose.getSaldo());

        // Jose transfiere a Luis
        boolean transferencia = banco.transferir(jose, luis, 400);

        System.out.println("\n=== JOSE TRANSFIERE 400 A LUIS ===");
        System.out.println("Transferencia realizada: " + transferencia);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());

        // Prueba de saldo insuficiente
        boolean transferenciaInvalida = banco.transferir(luis, jose, 5000);

        System.out.println("\n=== TRANSFERENCIA INVALIDA ===");
        System.out.println("Transferencia realizada: " + transferenciaInvalida);
        System.out.println("Jose: " + jose.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());
        
        // Prueba de recepción inválida
        boolean recepcionInvalida = banco.recibir(luis, 2000);

        System.out.println("\n=== RECEPCIÓN INVALIDA ===");
        System.out.println("Recepción realizada: " + recepcionInvalida);
        System.out.println("Banco: " + banco.getSaldo());
        System.out.println("Luis: " + luis.getSaldo());
    }
}