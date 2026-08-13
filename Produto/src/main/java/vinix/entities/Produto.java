package vinix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@Builder @EqualsAndHashCode(of = "id")
@NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "tb_produto")
public class Produto implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Integer estoque;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private OffsetDateTime dataCriacao;

    @Version
    private Long version;
}
