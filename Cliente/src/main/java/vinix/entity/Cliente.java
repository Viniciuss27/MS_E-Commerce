package vinix.entity;

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
@Setter @Getter
@Builder @EqualsAndHashCode(of = "id")
@Entity @Table(name = "tb_clientes")
public class Cliente implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String nome;

  @Column(nullable = false, unique = true, length = 14)
  private String cpf;

  @Column(nullable = false, unique = true, length = 120)
  private String email;

  @Column(nullable = false, length = 20)
  private String telefone;
}
