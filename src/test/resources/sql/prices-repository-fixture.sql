DELETE FROM prices;

INSERT INTO prices
(id, brand_id, product_id, start_date, end_date, price_list, priority, price, currency)
VALUES
-- El ganador no es ni la primera ni la última fila del grupo, ni la menor prioridad.
(101, 10, 100, '2020-06-14 00:00:00+02:00', '2020-06-14 23:59:59+02:00', 101, 1, 35.50, 'EUR'),
(102, 10, 100, '2020-06-14 15:00:00+02:00', '2020-06-14 18:30:00+02:00', 102, 5, 25.45, 'EUR'),
(103, 10, 100, '2020-06-14 15:00:00+02:00', '2020-06-14 18:30:00+02:00', 103, 3, 30.50, 'EUR'),
-- Señuelos de prioridad superior: marca, producto o vigencia incorrectos.
(104, 20, 100, '2020-06-14 15:00:00+02:00', '2020-06-14 18:30:00+02:00', 104, 90, 10.00, 'EUR'),
(105, 10, 200, '2020-06-14 15:00:00+02:00', '2020-06-14 18:30:00+02:00', 105, 90, 10.00, 'EUR'),
(106, 10, 100, '2020-06-14 16:00:01+02:00', '2020-06-14 18:30:00+02:00', 106, 99, 10.00, 'EUR'),
(107, 10, 100, '2020-06-14 15:00:00+02:00', '2020-06-14 15:59:59+02:00', 107, 99, 10.00, 'EUR'),
-- Tarifa aislada para límites inclusivos y consultas con offsets equivalentes.
(108, 30, 300, '2020-06-14 15:00:00+02:00', '2020-06-14 18:30:00+02:00', 108, 1, 20.00, 'EUR');
