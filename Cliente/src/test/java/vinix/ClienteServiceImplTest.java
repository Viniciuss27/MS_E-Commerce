package vinix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import vinix.dto.ClienteRequestDTO;
import vinix.dto.ClienteResponseDTO;
import vinix.dto.ClienteUpdateDTO;
import vinix.entity.Cliente;
import vinix.mapper.ClienteMapper;
import vinix.repositories.ClienteRepository;
import vinix.services.ClienteServiceImpl;
import vinix.services.exceptions.ExistenteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cliente - teste")
public class ClienteServiceImplTest {

  @Mock
  private ClienteRepository repository;

  @Mock
  private ClienteMapper mapper;

  @InjectMocks
  private ClienteServiceImpl service;

  private Cliente cliente;
  private ClienteResponseDTO responseDTO;
  private ClienteRequestDTO requestDTO;
  private ClienteUpdateDTO updateDTO;

  @BeforeEach
  void setUp() {
    cliente = Cliente.builder()
        .id(1L)
        .nome("maria")
        .cpf("111.222.333-78")
        .email("maria@gmail.com")
        .telefone("99988-2233").build();

    responseDTO = new ClienteResponseDTO(1L, "maria", "111.222.333-78", "maria@gmail.com", "99988-2233");
    requestDTO = new ClienteRequestDTO("maria", "111.222.333-78", "maria@gmail.com", "99988-2233");
    updateDTO = new ClienteUpdateDTO("maria", "maria@gmail.com", "99988-2233");
  }

  @Nested
  @DisplayName("BuscarPorId")
  class BuscarPorId {

    @Test
    @DisplayName("Deve Retornar o Cliente se o Id existir")
    void clienteExiste() {
      when(repository.findById(1L)).thenReturn(Optional.of(cliente));
      when(mapper.toDTO(cliente)).thenReturn(responseDTO);

      ClienteResponseDTO resultado = service.buscarPorId(1L);

      assertThat(resultado).isNotNull();
      assertThat(resultado.id()).isEqualTo(1L);
      assertThat(resultado.nome()).isEqualTo("maria");
      assertThat(resultado.cpf()).isEqualTo("111.222.333-78");
      verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção se o cliente não existir")
    void clienteNaoExiste() {
      when(repository.findById(2L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPorId(2L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("2");

      verifyNoInteractions(mapper);
    }
  }

  @Nested
  @DisplayName("BuscarPorEmail")
  class BuscarPorEmail {

    @Test
    @DisplayName("Deve retornar o Cliente se o email existir")
    void clienteExiste() {
      when(repository.findByEmail("maria@gmail.com")).thenReturn(Optional.of(cliente));
      when(mapper.toDTO(cliente)).thenReturn(responseDTO);

      ClienteResponseDTO resultado = service.buscarPorEmail("maria@gmail.com");

      assertThat(resultado).isNotNull();
      assertThat(resultado.id()).isEqualTo(1L);
      assertThat(resultado.email()).isEqualTo("maria@gmail.com");

      verify(repository).findByEmail("maria@gmail.com");
    }

    @Test
    @DisplayName("Deve lançar exceção se o email não existir")
    void clienteNaoExiste() {
      when(repository.findByEmail("maria@gmail.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPorEmail("maria@gmail.com"))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("maria@gmail.com");

      verifyNoInteractions(mapper);
    }
  }

  @Nested
  @DisplayName("Salvar")
  class Salvar {

    @Test
    @DisplayName("Deveria salvar o cliente com sucesso")
    void salvarComSucesso() {
      when(repository.existsByEmail("maria@gmail.com")).thenReturn(false);
      when(mapper.toEntity(requestDTO)).thenReturn(cliente);
      when(repository.save(cliente)).thenReturn(cliente);
      when(mapper.toDTO(cliente)).thenReturn(responseDTO);

      ClienteResponseDTO resultado = service.salvar(requestDTO);

      assertThat(resultado.id()).isEqualTo(1L);
      assertThat(resultado.nome()).isEqualTo("maria");
      verify(repository).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar exceção se o email já existir")
    void naoSalvarEmailExistente() {
      when(repository.existsByEmail("maria@gmail.com")).thenReturn(true);

      assertThatThrownBy(() -> service.salvar(requestDTO))
          .isInstanceOf(ExistenteException.class)
          .hasMessageContaining("maria@gmail.com");

      verify(repository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Atualizar")
  class Atualizar {

    @Test
    @DisplayName("Deve atualizar o cliente com sucesso")
    void atualizarComSucesso() {
      ClienteUpdateDTO novoDTO = new ClienteUpdateDTO(
          "marcos", "marcos@gmail.com", "99998-0011");

      Cliente clienteAtualizado = Cliente.builder()
          .nome("marcos").email("marcos@gmail.com").telefone("99998-0011").build();

      ClienteResponseDTO responseAtualizado = new ClienteResponseDTO(
          1L, "marcos", "111.222.333-78", "marcos@gmail.com", "99998-0011");

      when(repository.findById(1L)).thenReturn(Optional.of(cliente));
      when(repository.existsByEmailAndIdNot("marcos@gmail.com", 1L)).thenReturn(false);
      when(repository.save(cliente)).thenReturn(clienteAtualizado);
      when(mapper.toDTO(clienteAtualizado)).thenReturn(responseAtualizado);

      ClienteResponseDTO resultado = service.atualizar(1L, novoDTO);

      assertThat(resultado.email()).isEqualTo("marcos@gmail.com");
      verify(repository).save(cliente);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void idNaoExiste() {
      when(repository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.atualizar(99L, updateDTO))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("99");

      verify(repository, never()).existsByEmailAndIdNot(any(), any());
      verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ExistenteException quando o novo email já existe")
    void emailJaExiste() {
      ClienteUpdateDTO dtoConflitante = new ClienteUpdateDTO("marcos", "marcos@gmail.com", "99998-0011");

      when(repository.findById(1L)).thenReturn(Optional.of(cliente));
      when(repository.existsByEmailAndIdNot("marcos@gmail.com", 1L)).thenReturn(true);

      assertThatThrownBy(() -> service.atualizar(1L, dtoConflitante))
          .isInstanceOf(ExistenteException.class)
          .hasMessageContaining("marcos@gmail.com");

      verify(repository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Deletar")
  class Deletar {

    @Test
    @DisplayName("Deve deletar quando o ID existir")
    void deveDeletar() {
      when(repository.existsById(1L)).thenReturn(true);

      service.deletar(1L);

      verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void idNaoExistir() {
      when(repository.existsById(99L)).thenReturn(false);

      assertThatThrownBy(() -> service.deletar(99L))
          .isInstanceOf(ResourceNotFoundException.class)
          .hasMessageContaining("99");

      verify(repository, never()).deleteById(any());
    }
  }
}