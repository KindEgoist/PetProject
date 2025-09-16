Payment - сервис для оплаты покупки.


Наполним таблицу accounts SQL запросом

INSERT INTO payment.accounts (owner, balance) VALUES
('Иван', 10000),
('Мария', 5000),
('Алексей', 15000),
('Екатерина', 7500),
('Дмитрий', 3000);

Проверяем через HTTPie

Сначала сделаем резерв товара:

Post: http://localhost:8081/reserve/res

Body:  
{ 
"productId" : 1,
"quantity" : 2
}  
Ответ:  
{
"success": true,
"message": "Продукт зарезервирован",
"price": 1200
}

Осуществление оплаты:

Post: http://localhost:8082/payment/pay

Body:  
{"accountId" : 6,
"amount" : 1,
"productId" : 1,
"quantity" : 2
}  
Ответ:  
{
"success": true,
"message": "Оплата прошла успешно"
}