INSERT INTO tb_user (name, email, password) VALUES
('Admin Teste', 'admin@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4H9mzMv/xzz9pKR7ZI7QAeqzVjRq'),
('Cliente Teste', 'cliente@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4H9mzMv/xzz9pKR7ZI7QAeqzVjRq');

-- Vincula o Admin Teste à role ADMIN, e Cliente Teste à role CLIENT
INSERT INTO tb_user_role (user_id, role_id)
SELECT u.id, r.id FROM tb_user u, tb_role r
WHERE u.email = 'admin@gmail.com' AND r.role_name = 'ADMIN';

INSERT INTO tb_user_role (user_id, role_id)
SELECT u.id, r.id FROM tb_user u, tb_role r
WHERE u.email = 'cliente@gmail.com' AND r.role_name = 'CLIENT';