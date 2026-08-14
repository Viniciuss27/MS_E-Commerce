package vinix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Builder @EqualsAndHashCode(of = "id")
@Entity @Table(name = "tb_categoria")
public class Categoria implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nome;

  @Column(nullable = false, length = 150)
  private String descricao;
}
