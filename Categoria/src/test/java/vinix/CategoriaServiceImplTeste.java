package vinix.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import vinix.dtos.CategoriaRequestDTO;
import vinix.dtos.CategoriaResponseDTO;
import vinix.entities.Categoria;
import vinix.mapper.CategoriaMapper;
import vinix.repositories.CategoriaRepository;
import vinix.services.exceptions.ExistenteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaServiceImpl - testes Unitarios")
public class CategoriaServiceImplTeste {

  @Mock
  private CategoriaRepository repository;

  @Mock
  private CategoriaMapper mapper;

  @org.mockito.InjectMocks
  private CategoriaServiceImpl service;

  private Categoria categoria;
  private CategoriaResponseDTO response;
  private CategoriaRequestDTO request;

  @BeforeEach
  void setup() {
    categoria = Categoria.builder()
        .id(1L)
        .nome("pc")
        .descricao("eletronicos")
        .build();

    response = new CategoriaResponseDTO(1L, "pc", "eletronicos");
    request = new CategoriaRequestDTO("pc", "eletronicos");
  }

  @Nested
  @DisplayName("BuscarPorId")
  class BuscarPorId {

    @Test
    @DisplayName("Deve retornar a categoria quando o ID existir")
    void deveRetornarCategoriaQuandoIdExistir() {
      Mockito.when(repository.findById(1L)).thenReturn(Optional.of(categoria));
      Mockito.when(mapper.toDto(categoria)).thenReturn(response);

      CategoriaResponseDTO resultado = service.buscarPorId(1L);

      assertThat(resultado).isNotNull();
      assertThat(resultado.id()).isEqualTo(1L);
      assertThat(resultado.nome()).isEqualTo("pc");
      assertThat(resultado.descricao()).isEqualTo("eletronicos");
      verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
      Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPorId(99L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("99");

      verifyNoInteractions(mapper);
    }
  }

  @Nested
  @DisplayName("salvar")
  class Salvar {

    @Test
    @DisplayName("Deve salvar a categoria quando o nome não existir")
    void deveSalvarQuandoNomeNaoExistir() {
      // Arrange — a regra real é existsByNome, não existsById
      Mockito.when(repository.existsByNome("pc")).thenReturn(false);
      Mockito.when(mapper.toEntity(request)).thenReturn(categoria);
      Mockito.when(repository.save(categoria)).thenReturn(categoria);
      Mockito.when(mapper.toDto(categoria)).thenReturn(response);

      // Act
      CategoriaResponseDTO resultado = service.salvar(request);

      // Assert
      assertThat(resultado.id()).isEqualTo(1L);
      assertThat(resultado.nome()).isEqualTo("pc");
      verify(repository).save(categoria);
    }

    @Test
    @DisplayName("Deve lançar ExistenteException quando o nome já existir")
    void deveLancarExcecaoQuandoNomeJaExistir() {
      // Arrange
      Mockito.when(repository.existsByNome("pc")).thenReturn(true);

      // Act + Assert
      assertThatThrownBy(() -> service.salvar(request))
          .isInstanceOf(ExistenteException.class)
          .hasMessageContaining("pc");

      // Nunca deve tentar salvar com nome duplicado
      verify(repository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("atualizar")
  class Atualizar {

    @Test
    @DisplayName("Deve atualizar quando o ID existir e o nome não conflitar")
    void deveAtualizarQuandoIdExistirENomeNaoConflitar() {
      // Arrange
      CategoriaRequestDTO novoDto = new CategoriaRequestDTO("notebook", "eletronicos");
      Categoria categoriaAtualizada = Categoria.builder()
          .id(1L).nome("notebook").descricao("eletronicos").build();
      CategoriaResponseDTO responseAtualizado =
          new CategoriaResponseDTO(1L, "notebook", "eletronicos");

      Mockito.when(repository.findById(1L)).thenReturn(Optional.of(categoria));
      Mockito.when(repository.existsByNomeAndIdNot("notebook", 1L)).thenReturn(false);
      Mockito.when(repository.save(categoria)).thenReturn(categoriaAtualizada);
      Mockito.when(mapper.toDto(categoriaAtualizada)).thenReturn(responseAtualizado);

      // Act
      CategoriaResponseDTO resultado = service.atualizar(1L, novoDto);

      // Assert
      assertThat(resultado.nome()).isEqualTo("notebook");
      verify(repository).save(categoria);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
      // Arrange
      Mockito.when(repository.findById(99L)).thenReturn(Optional.empty());

      // Act + Assert
      assertThatThrownBy(() -> service.atualizar(99L, request))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("99");

      // Nunca deve tentar verificar nome nem salvar
      verify(repository, never()).existsByNomeAndIdNot(any(), any());
      verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ExistenteException quando o novo nome pertencer a outra categoria")
    void deveLancarExcecaoQuandoNomeConflitarComOutraCategoria() {
      // Arrange
      CategoriaRequestDTO dtoConflitante = new CategoriaRequestDTO("periféricos", "eletronicos");

      Mockito.when(repository.findById(1L)).thenReturn(Optional.of(categoria));
      Mockito.when(repository.existsByNomeAndIdNot("periféricos", 1L)).thenReturn(true);

      // Act + Assert
      assertThatThrownBy(() -> service.atualizar(1L, dtoConflitante))
          .isInstanceOf(ExistenteException.class)
          .hasMessageContaining("periféricos");

      // Não deve salvar quando há conflito de nome
      verify(repository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("deletar")
  class Deletar {

    @Test
    @DisplayName("Deve deletar quando o ID existir")
    void deveDeletarQuandoIdExistir() {
      // Arrange
      Mockito.when(repository.existsById(1L)).thenReturn(true);

      // Act
      service.deletar(1L);

      // Assert
      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void deveLancarExcecaoQuandoIdNaoExistir() {
      // Arrange
      Mockito.when(repository.existsById(99L)).thenReturn(false);

      // Act + Assert
      assertThatThrownBy(() -> service.deletar(99L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("99");

      // Nunca deve tentar deletar algo que não existe
      verify(repository, never()).deleteById(any());
    }
  }
}