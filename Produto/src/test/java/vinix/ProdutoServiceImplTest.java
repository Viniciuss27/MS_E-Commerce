package vinix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vinix.dto.ItemPedidoDTO;
import vinix.dto.ProdutoRequestDTO;
import vinix.dto.ProdutoResponseDTO;
import vinix.entities.Produto;
import vinix.feigh.CategoriaFeighClient;
import vinix.feigh.CategoriaFeighDTO;
import vinix.mapper.ProdutoMapper;
import vinix.repositories.ProdutoRepository;
import vinix.services.ProdutoServiceImpl;
import vinix.services.exceptions.EstoqueInsuficienteException;
import vinix.services.exceptions.ProdutoExistente;
import vinix.services.exceptions.ResourceNotFoundException;
import vinix.services.exceptions.ServicoIndisponivelException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoServiceImpl - Testes Unitários")
class ProdutoServiceImplTest {

    @Mock
    private ProdutoRepository repository;

    @Mock
    private ProdutoMapper mapper;

    @Mock
    private CategoriaFeighClient feigh;

    @InjectMocks
    private ProdutoServiceImpl service;

    private Produto produto;
    private ProdutoRequestDTO requestDTO;
    private ProdutoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        // Objeto base reaproveitado entre os testes
        produto = Produto.builder()
                .id(1L)
                .nome("Notebook Gamer X15")
                .descricao("Notebook 15.6 RTX 4060")
                .sku("SKU-NB-001")
                .preco(new BigDecimal("4599.90"))
                .estoque(15)
                .categoriaId(1L)
                .ativo(true)
                .build();

        requestDTO = new ProdutoRequestDTO(
                "Notebook Gamer X15", "Notebook 15.6 RTX 4060",
                "SKU-NB-001", new BigDecimal("4599.90"), 15, 1L
        );

        responseDTO = new ProdutoResponseDTO(
                1L, "Notebook Gamer X15", "Notebook 15.6 RTX 4060",
                "SKU-NB-001", new BigDecimal("4599.90"), 15, 1L, true, null
        );
    }

    @Nested
    @DisplayName("buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar o produto quando o ID existir e estiver ativo")
        void deveRetornarProdutoQuandoIdExistir() {
            // Arrange
            when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(produto));
            when(mapper.toDTO(produto)).thenReturn(responseDTO);

            // Act
            ProdutoResponseDTO resultado = service.buscarPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.sku()).isEqualTo("SKU-NB-001");
            verify(repository).findByIdAndAtivoTrue(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
        void deveLancarExcecaoQuandoIdNaoExistir() {
            // Arrange
            when(repository.findByIdAndAtivoTrue(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.buscarPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            // mapper não chamado, fluxo parado no orElseThrow
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("salvar")
    class Salvar {

        @Test
        @DisplayName("Deve salvar o produto quando o SKU não existir")
        void deveSalvarQuandoSkuNaoExistir() {
            // Arrange
            when(repository.existsBySku("SKU-NB-001")).thenReturn(false);

            // NOVO: precisa simular a categoria existindo, já que salvar() agora valida isso
            CategoriaFeighDTO categoriaDTO = new CategoriaFeighDTO(1L, "Informática");
            when(feigh.buscarPorId(1L))
                .thenReturn(ResponseEntity.ok(categoriaDTO));

            when(mapper.toEntity(requestDTO)).thenReturn(produto);
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toDTO(produto)).thenReturn(responseDTO);

            // Act
            ProdutoResponseDTO resultado = service.salvar(requestDTO);

            // Assert
            assertThat(resultado.sku()).isEqualTo("SKU-NB-001");
            verify(repository).save(produto);
        }

        @Test
        @DisplayName("Deve lançar ProdutoExistente quando o SKU já existir")
        void deveLancarExcecaoQuandoSkuJaExistir() {
            // Arrange
            when(repository.existsBySku("SKU-NB-001")).thenReturn(true);

            // Act + Assert
            assertThatThrownBy(() -> service.salvar(requestDTO))
                    .isInstanceOf(ProdutoExistente.class)
                    .hasMessageContaining("SKU-NB-001");

            //  Não salva produto com SKU duplicado
            verify(repository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("reservarEstoque")
    class ReservarEstoque {

        @Test
        @DisplayName("Deve abater o estoque quando houver quantidade suficiente")
        void deveAbaterEstoqueQuandoSuficiente() {
            // Arrange
            ItemPedidoDTO item = new ItemPedidoDTO(1L, 5);
            when(repository.findById(1L)).thenReturn(Optional.of(produto));
            // Simula UPDATE afetando 1 linha = sucesso
            when(repository.abaterEstoque(1L, 5)).thenReturn(1);

            // Act
            service.reservarEstoque(List.of(item));

            // Assert
            verify(repository).abaterEstoque(1L, 5);
        }

        @Test
        @DisplayName("Deve lançar EstoqueInsuficienteException quando o UPDATE não afetar linhas")
        void deveLancarExcecaoQuandoEstoqueInsuficiente() {
            // Arrange
            ItemPedidoDTO item = new ItemPedidoDTO(1L, 100); // pede mais que o disponível
            when(repository.findById(1L)).thenReturn(Optional.of(produto));
            // Simula UPDATE não afetando linha nenhuma = estoque insuficiente
            when(repository.abaterEstoque(1L, 100)).thenReturn(0);

            // Act + Assert
            assertThatThrownBy(() -> service.reservarEstoque(List.of(item)))
                    .isInstanceOf(EstoqueInsuficienteException.class)
                    .hasMessageContaining(produto.getNome());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o produto não existir")
        void deveLancarExcecaoQuandoProdutoNaoExistir() {
            // Arrange
            ItemPedidoDTO item = new ItemPedidoDTO(99L, 1);
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // Act + Assert
            assertThatThrownBy(() -> service.reservarEstoque(List.of(item)))
                    .isInstanceOf(ResourceNotFoundException.class);

            // não abater estoque de produto inexistente
            verify(repository, never()).abaterEstoque(anyLong(), anyInt());
        }

        @Test
        @DisplayName("Deve processar múltiplos itens em sequência")
        void deveProcessarMultiplosItens() {
            // Arrange
            Produto produto2 = Produto.builder()
                    .id(2L).nome("Mouse Sem Fio MX").estoque(80).build();

            ItemPedidoDTO item1 = new ItemPedidoDTO(1L, 5);
            ItemPedidoDTO item2 = new ItemPedidoDTO(2L, 10);

            when(repository.findById(1L)).thenReturn(Optional.of(produto));
            when(repository.findById(2L)).thenReturn(Optional.of(produto2));
            when(repository.abaterEstoque(1L, 5)).thenReturn(1);
            when(repository.abaterEstoque(2L, 10)).thenReturn(1);

            // Act
            service.reservarEstoque(List.of(item1, item2));

            // Assert
            verify(repository).abaterEstoque(1L, 5);
            verify(repository).abaterEstoque(2L, 10);
        }
    }

    @Nested
    @DisplayName("desativar")
    class Desativar {

        @Test
        @DisplayName("Deve desativar o produto quando existir")
        void deveDesativarQuandoExistir() {
            // Arrange
            when(repository.findById(1L)).thenReturn(Optional.of(produto));

            // Act
            service.desativar(1L);

            // Assert
            assertThat(produto.getAtivo()).isFalse();
            verify(repository).save(produto);
        }
    }

    @Nested
    @DisplayName("salvar - validação de categoria via Feign")
    class SalvarComValidacaoCategoria {

        @Test
        @DisplayName("Deve salvar quando SKU não existe e categoria existe")
        void deveSalvarQuandoCategoriaExiste() {
            // Arrange
            when(repository.existsBySku("SKU-NB-001")).thenReturn(false);

            // Simula o Feign respondendo 200 OK com a categoria encontrada
            CategoriaFeighDTO categoriaDTO = new CategoriaFeighDTO(1L, "Informática");
            when(feigh.buscarPorId(1L))
                .thenReturn(ResponseEntity.ok(categoriaDTO));

            when(mapper.toEntity(requestDTO)).thenReturn(produto);
            when(repository.save(produto)).thenReturn(produto);
            when(mapper.toDTO(produto)).thenReturn(responseDTO);

            // Act
            ProdutoResponseDTO resultado = service.salvar(requestDTO);

            // Assert
            assertThat(resultado).isNotNull();
            verify(repository).save(produto);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando a categoria não existe")
        void deveLancarExcecaoQuandoCategoriaNaoExiste() {
            // Arrange
            when(repository.existsBySku("SKU-NB-001")).thenReturn(false);

            // Simula o Feign respondendo 404 (categoria realmente não existe)
            when(feigh.buscarPorId(1L))
                .thenReturn(ResponseEntity.notFound().build());

            // Act + Assert
            assertThatThrownBy(() -> service.salvar(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");

            // Não deve tentar salvar produto com categoria inválida
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ServicoIndisponivelException quando ms-categoria está fora do ar")
        void deveLancarExcecaoQuandoServicoCategoriaIndisponivel() {
            // Arrange
            when(repository.existsBySku("SKU-NB-001")).thenReturn(false);

            // Simula o fallback disparando (circuit breaker aberto)
            when(feigh.buscarPorId(1L))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

            // Act + Assert
            assertThatThrownBy(() -> service.salvar(requestDTO))
                .isInstanceOf(ServicoIndisponivelException.class);

            verify(repository, never()).save(any());
        }
    }
}