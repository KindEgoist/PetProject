Store - сервис магазина

Тестирование магазина:

Проверяем через HTTPie


Post: http://localhost:8888/store/purchase

Body:   
{
"productId" : 1,
"quantity" : 2,
"accountId" : 6
}

Ответ:  
{
"success": true,
"message": "Покупка успешно завершена!"
}