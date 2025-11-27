package com.fas.project.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fas.project.model.Ubicacion;
import com.fas.project.repository.UbicacionRepository;

@Component
public class DataLoader implements CommandLineRunner {

    // Record privado para estructurar la información (Localidad, Barrio)
    private record UbicacionData(String localidad, String barrio) {
    }

    private final UbicacionRepository ubicacionRepository;

    public DataLoader(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        List<UbicacionData> ubicacionesACargar = new ArrayList<>();
        // 1. Usaquén
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Usaquén", "Santa Bárbara Central"),
                new UbicacionData("Usaquén", "Cedritos"),
                new UbicacionData("Usaquén", "Usaquén Centro"),
                new UbicacionData("Usaquén", "El Chicó Norte"),
                new UbicacionData("Usaquén", "Bella Suiza"),
                new UbicacionData("Usaquén", "Bosque Medina"),
                new UbicacionData("Usaquén", "La Carolina"),
                new UbicacionData("Usaquén", "San Cristóbal Norte"),
                new UbicacionData("Usaquén", "Barrancas")));

        // 2. Chapinero
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Chapinero", "Quinta Camacho"),
                new UbicacionData("Chapinero", "El Nogal"),
                new UbicacionData("Chapinero", "Rosales"),
                new UbicacionData("Chapinero", "Chapinero Central"),
                new UbicacionData("Chapinero", "Chicó Reservado"),
                new UbicacionData("Chapinero", "Marly"),
                new UbicacionData("Chapinero", "La Merced"),
                new UbicacionData("Chapinero", "Pardo Rubio")));

        // 3. Santa Fe
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Santa Fe", "La Candelaria"),
                new UbicacionData("Santa Fe", "Las Nieves"),
                new UbicacionData("Santa Fe", "Centro Internacional"),
                new UbicacionData("Santa Fe", "La Perseverancia"),
                new UbicacionData("Santa Fe", "El Paraíso"),
                new UbicacionData("Santa Fe", "Voto Nacional")));

        // 4. San Cristóbal
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("San Cristóbal", "20 de Julio"),
                new UbicacionData("San Cristóbal", "San Blas"),
                new UbicacionData("San Cristóbal", "Montecarlo"),
                new UbicacionData("San Cristóbal", "Los Libertadores"),
                new UbicacionData("San Cristóbal", "La Victoria")));

        // 5. Usme
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Usme", "Usme Pueblo"),
                new UbicacionData("Usme", "Betania"),
                new UbicacionData("Usme", "Gran Yomasa"),
                new UbicacionData("Usme", "Sierra Morena"),
                new UbicacionData("Usme", "Santa Librada")));

        // 6. Tunjuelito
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Tunjuelito", "Venecia"),
                new UbicacionData("Tunjuelito", "Tunjuelito Central"),
                new UbicacionData("Tunjuelito", "Fátima"),
                new UbicacionData("Tunjuelito", "San Benito")));

        // 7. Bosa
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Bosa", "Bosa Central"),
                new UbicacionData("Bosa", "El Porvenir"),
                new UbicacionData("Bosa", "Laureles"),
                new UbicacionData("Bosa", "El Tintal"),
                new UbicacionData("Bosa", "Bosa Nova")));

        // 8. Kennedy
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Kennedy", "Kennedy Central"),
                new UbicacionData("Kennedy", "Patio Bonito"),
                new UbicacionData("Kennedy", "Castilla"),
                new UbicacionData("Kennedy", "Timiza"),
                new UbicacionData("Kennedy", "Gran Britalia"),
                new UbicacionData("Kennedy", "Corabastos")));

        // 9. Fontibón
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Fontibón", "Fontibón Centro"),
                new UbicacionData("Fontibón", "Modelia"),
                new UbicacionData("Fontibón", "La Felicidad"),
                new UbicacionData("Fontibón", "Ciudad Salitre"),
                new UbicacionData("Fontibón", "Puente Grande")));

        // 10. Engativá
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Engativá", "Álamos Norte"),
                new UbicacionData("Engativá", "Boyacá Real"),
                new UbicacionData("Engativá", "Engativá Pueblo"),
                new UbicacionData("Engativá", "Garcés Navas"),
                new UbicacionData("Engativá", "Quinta Paredes"),
                new UbicacionData("Engativá", "El Dorado")));

        // 11. Suba
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Suba", "Suba Centro"),
                new UbicacionData("Suba", "Mazurén"),
                new UbicacionData("Suba", "Tuna Alta"),
                new UbicacionData("Suba", "El Rincón"),
                new UbicacionData("Suba", "Tibabuyes"),
                new UbicacionData("Suba", "La Campiña")));

        // 12. Barrios Unidos
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Barrios Unidos", "La Castellana"),
                new UbicacionData("Barrios Unidos", "12 de Octubre"),
                new UbicacionData("Barrios Unidos", "Alfonso López"),
                new UbicacionData("Barrios Unidos", "Los Andes")));

        // 13. Teusaquillo
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Teusaquillo", "Park Way"),
                new UbicacionData("Teusaquillo", "La Soledad"),
                new UbicacionData("Teusaquillo", "Teusaquillo Central"),
                new UbicacionData("Teusaquillo", "Santa Teresita")));

        // 14. Los Mártires
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Los Mártires", "Palacio"),
                new UbicacionData("Los Mártires", "Santa Inés"),
                new UbicacionData("Los Mártires", "La Estanzuela")));

        // 15. Antonio Nariño
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Antonio Nariño", "Ciudad Jardín"),
                new UbicacionData("Antonio Nariño", "Restrepo"),
                new UbicacionData("Antonio Nariño", "Policarpa")));

        // 16. Puente Aranda
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Puente Aranda", "Zona Industrial"),
                new UbicacionData("Puente Aranda", "Puente Aranda Central"),
                new UbicacionData("Puente Aranda", "Comuneros")));

        // 17. Candelaria
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Candelaria", "La Candelaria Centro"),
                new UbicacionData("Candelaria", "Belén"),
                new UbicacionData("Candelaria", "San Bernardo")));

        // 18. Rafael Uribe Uribe
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Rafael Uribe Uribe", "Quiroga"),
                new UbicacionData("Rafael Uribe Uribe", "Marco Fidel Suárez"),
                new UbicacionData("Rafael Uribe Uribe", "San Jorge Central")));

        // 19. Ciudad Bolívar
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Ciudad Bolívar", "Sierra Morena"),
                new UbicacionData("Ciudad Bolívar", "Madelena"),
                new UbicacionData("Ciudad Bolívar", "Perdomo"),
                new UbicacionData("Ciudad Bolívar", "Arborizadora Alta")));

        // 20. Sumapaz (Es una zona rural, por eso tiene menos sectores urbanos/barrios)
        ubicacionesACargar.addAll(List.of(
                new UbicacionData("Sumapaz", "Betania"),
                new UbicacionData("Sumapaz", "Atrato"),
                new UbicacionData("Sumapaz", "El Mirador")));

        // --- PROCESO IDEMPOTENTE DE GUARDADO ---
        for (UbicacionData data : ubicacionesACargar) {

            // 1. Buscamos si la combinación (Localidad, Barrio) ya existe
            Ubicacion existente = ubicacionRepository.findByLocalidadAndBarrio(
                    data.localidad(),
                    data.barrio());

            // 2. Si no existe, la guardamos
            if (existente == null) {
                Ubicacion nuevaUbicacion = new Ubicacion();
                nuevaUbicacion.setLocalidad(data.localidad());
                nuevaUbicacion.setBarrio(data.barrio());

                // Nos aseguramos de que ambos campos existan antes de guardar
                if (!data.localidad().isBlank() && !data.barrio().isBlank()) {
                    ubicacionRepository.save(nuevaUbicacion);
                }
            }
        }
    }
}