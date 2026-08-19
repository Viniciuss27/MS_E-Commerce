package vinix.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinix.dto.ItemPedidoDTO;
import vinix.dto.ProdutoRequestDTO;
import vinix.dto.ProdutoResponseDTO;
import vinix.entities.Produto;
import vinix.feign.CategoriaFeignClient;
import vinix.feign.CategoriaFeignDTO;
import vinix.mapper.ProdutoMapper;
import vinix.repositories.ProdutoRepository;
import vinix.services.exceptions.EstoqueInsuficienteException;
import vinix.services.exceptions.ProdutoExistente;
import vinix.services.exceptions.ResourceNotFoundException;
import vinix.services.exceptions.ServicoIndisponivelException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;
    private final ProdutoMapper mapper;
    private final CategoriaFeignClient feign;

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarTodos() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        return mapper.toDTO(repository.findByIdAndAtivoTrue(id).orElseThrow(() ->
            new ResourceNotFoundException("Produto não encontrado com o ID: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaIdAndAtivoTrue(categoriaId).stream()
            .map(mapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarDisponiveis() {
        // Busca produtos com estoque maior que 0 e que estejam ativos
        return repository.findByAtivoTrueAndEstoqueGreaterThan(0).stream()
            .map(mapper::toDTO).toList();
    }

    @Override
    @Transactional
    public ProdutoResponseDTO salvar(ProdutoRequestDTO dto) {
        if (repository.existsBySku(dto.sku())) {
            throw new ProdutoExistente(
                "Já existe produto com o SKU digitado -> " + dto.sku());
        }

        validarCategoriaExistente(dto.categoriaId());

        Produto produto = mapper.toEntity(dto);
        produto.setAtivo(true);
        Produto produtoSalvo = repository.save(produto);

        log.info("Produto cadastrado com sucesso. ID: {}, SKU: {}", produtoSalvo.getId(), produtoSalvo.getSku());
        return mapper.toDTO(produtoSalvo);
    }

    private void validarCategoriaExistente(Long categoriaId) {
        ResponseEntity<CategoriaFeignDTO> response = feign.buscarPorId(categoriaId);

        if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            throw new ServicoIndisponivelException(
                "Serviço de categorias está indisponível no momento Tente novamente mais tarde!");
        }

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ResourceNotFoundException(
                "Categoria não encontrada com o ID: " + categoriaId);
        }
    }

    @Override
    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = repository.findById(id).orElseThrow(() ->
            new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        mapper.updateEntityFromDTO(dto, produto);
        Produto produtoSalvo = repository.save(produto);
        log.info("Produto ID: {} atualizado com sucesso", id);
        return mapper.toDTO(produtoSalvo);
    }

    @Override
    @Transactional
    public void desativar(Long id) {
        Produto produto = repository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));

        produto.setAtivo(false);
        repository.save(produto);
        log.info("Produto ID: {} desativado com sucesso", id);
    }

    @Override
    @Transactional
    public void reservarEstoque(List<ItemPedidoDTO> itens) {
        for (ItemPedidoDTO item : itens) {
            Produto produto = repository.findById(item.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Produto não encontrado com ID: " + item.produtoId()));

            int linhasAfetadas = repository.abaterEstoque(item.produtoId(), item.quantidade());

            if (linhasAfetadas == 0) {
                log.error("Estoque insuficiente para o produto ID: {}. Solicitado: {}, Disponível: {}",
                    produto.getId(), item.quantidade(), produto.getEstoque());
                throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o produto: " + produto.getNome());
            }

            log.info("Estoque reservado para o produto ID: {}. Quantidade abatida: {}",
                item.produtoId(), item.quantidade());
        }
    }
}