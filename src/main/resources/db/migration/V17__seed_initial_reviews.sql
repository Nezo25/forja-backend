-- Seed initial authentic customer reviews for Forja do Chico
INSERT INTO customer_reviews (customer_name, rating, comment, product_name, customer_city, is_approved, created_at)
SELECT 'Lucas M.', 5, 'A pintura do Zorua ficou absurda de linda! O degradê nos detalhes e olhos é perfeito. Veio muito bem embalado.', 'Zorua (Pintado à Mão)', 'Curitiba - PR', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_reviews WHERE customer_name = 'Lucas M.' AND product_name = 'Zorua (Pintado à Mão)');

INSERT INTO customer_reviews (customer_name, rating, comment, product_name, customer_city, is_approved, created_at)
SELECT 'Beatriz S.', 5, 'Comprei o Mimikyu e superou todas as expectativas. Textura limpa sem fiapos de impressão, peso muito bom e o acabamento é impecável na estante. Atendimento pelo WhatsApp nota 10!', 'Mimikyu (10 cm)', 'São Paulo - SP', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_reviews WHERE customer_name = 'Beatriz S.' AND product_name = 'Mimikyu (10 cm)');

INSERT INTO customer_reviews (customer_name, rating, comment, product_name, customer_city, is_approved, created_at)
SELECT 'Rodrigo Albuquerque', 5, 'O relevo 3D da arte do Guts é sensacional. O contraste e os detalhes das sombras ficaram muito fiéis ao mangá. Chegou rápido e extremamente protegido.', 'Quadro Guts Berserk sem moldura', 'Florianópolis - SC', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_reviews WHERE customer_name = 'Rodrigo Albuquerque' AND product_name = 'Quadro Guts Berserk sem moldura)');

INSERT INTO customer_reviews (customer_name, rating, comment, product_name, customer_city, is_approved, created_at)
SELECT 'Matheus K.', 5, 'Simplesmente perfeito! As cores vivas e o acabamento manual deixaram a peça viva. Já encomendei outra para presentear meu irmão. Parabéns pelo trampo, Chico!', 'Fuecoco (Escala 1:10)', 'Belo Horizonte - MG', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM customer_reviews WHERE customer_name = 'Matheus K.' AND product_name = 'Fuecoco (Escala 1:10)');
