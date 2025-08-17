package com.microservices_system.business_service.services;
import com.microservices_system.business_service.client.DataServiceClient;
import com.microservices_system.business_service.dto.CategoriaDTO;
import com.microservices_system.business_service.exceptions.MicroserviceCommunicationException;
import com.microservices_system.business_service.service.CategoriaBusinessService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaBusinessServiceTest {

    @Mock
    private DataServiceClient dataServiceClient;

    @InjectMocks
    private CategoriaBusinessService categoriaBusinessService;

    @Test
    void cuandoObtenerTodosLasCategorias_entoncesRetornaLista() {
        // Arrange
        List<CategoriaDTO> categoriasEsperadas = Arrays.asList(
                new CategoriaDTO(1L, "Electrónicos", "Productos electrónicos diversos"),
                new CategoriaDTO(2L, "Ropa", "Prendas de vestir"),
                new CategoriaDTO(3L, "Libros", "Libros y revistas")
        );

        when(dataServiceClient.obtenerTodasLasCategorias()).thenReturn(categoriasEsperadas);

        // Act
        List<CategoriaDTO> resultado = categoriaBusinessService.obtenerTodosLasCategorias();

        // Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertEquals("Electrónicos", resultado.get(0).getNombre());
        assertEquals("Ropa", resultado.get(1).getNombre());
        assertEquals("Libros", resultado.get(2).getNombre());
        verify(dataServiceClient).obtenerTodasLasCategorias();
    }

    @Test
    void cuandoObtenerTodosLasCategorias_yOcurreFeignException_entoncesLanzaMicroserviceCommunicationException() {
        // Arrange
        when(dataServiceClient.obtenerTodasLasCategorias())
                .thenThrow(mock(FeignException.class));

        // Act & Assert
        MicroserviceCommunicationException exception = assertThrows(MicroserviceCommunicationException.class, () -> {
            categoriaBusinessService.obtenerTodosLasCategorias();
        });

        assertEquals("Error de comunicación con el servicio de datos", exception.getMessage());
        verify(dataServiceClient).obtenerTodasLasCategorias();
    }

    @Test
    void cuandoObtenerTodosLasCategorias_yNoHayCategorias_entoncesRetornaListaVacia() {
        // Arrange
        when(dataServiceClient.obtenerTodasLasCategorias())
                .thenReturn(Collections.emptyList());

        // Act
        List<CategoriaDTO> resultado = categoriaBusinessService.obtenerTodosLasCategorias();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(dataServiceClient).obtenerTodasLasCategorias();
    }

    @Test
    void cuandoObtenerTodosLasCategorias_entoncesLlamaAlDataServiceUnaVez() {
        // Arrange
        List<CategoriaDTO> categorias = Arrays.asList(
                new CategoriaDTO(1L, "Categoría Test", "Descripción Test")
        );

        when(dataServiceClient.obtenerTodasLasCategorias()).thenReturn(categorias);

        // Act
        List<CategoriaDTO> resultado = categoriaBusinessService.obtenerTodosLasCategorias();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Categoría Test", resultado.get(0).getNombre());
        verify(dataServiceClient, times(1)).obtenerTodasLasCategorias();
        verifyNoMoreInteractions(dataServiceClient);
    }
}
