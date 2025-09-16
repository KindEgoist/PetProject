Reserve - сервис отвечающий за резервирование товара.

Наполним таблицу products SQL запросом:  
INSERT INTO reserve.products (name, available, price, reserved) VALUES
('рюкзак', 10, 1200, 0),
('ручка', 50, 50, 0),
('тетрадь', 35, 75, 0);

Проверяем через HTTPie

Резервирование товара:

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

Подтверждение резерва:

Post: http://localhost:8081/reserve/commit  
Body:  
{  "productId" : 1,
"quantity" : 2
}  

Ответ:  
{
"success": true,
"message": "Продукт продан"
}

Отмена резерва:

Post: http://localhost:8081/reserve/cancel  
Body:   
{ 
"productId" : 1,
"quantity" : 2
}  

Ответ:  
{
"success": false,
"message": "Ошибка при отмене: Невозможно отменить резерв: запрошено больше, чем зарезервировано"
}