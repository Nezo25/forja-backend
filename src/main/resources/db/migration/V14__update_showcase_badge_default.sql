-- Atualiza o badge padrão dos slots 1 a 3 para a linguagem contextualizada da oficina
UPDATE showcase_slots 
SET badge_label = '🔥 OFERTA DESTAQUE DA OFICINA' 
WHERE slot_number BETWEEN 1 AND 3;
