# Online-electronic-store

This is a poc of an online electronic store back end project based on spring boot.
Optimistic lock solution has been implemented when add/remove items from basket to avoid concurrent read/write issue.

Project requirement can be found in
file [Bullish Technical Assessment (Take home).pdf](./Bullish%20Technical%20Assessment%20(Take%20home).pdf)

## Build

* Run all the unit tests and integration tests
    ```
   mvn clean test
    ```

* Launch the project
    ```
    mvn spring-boot:run
    ```

## Use docker image

* Build image locally

```
mvn clean install

docker build -t jenny19919215/electronic-store.jar:latest -f Dockerfile .
```

* Or docker image has been pushed to docker hub, you can pull image from docker public hub

```
docker pull jenny19919215/electronic-store.jar
```

* Then run

```
docker run -p 8080:8080 jenny19919215/electronic-store.jar
```

## Useful testing Urls

Only Admin user authenticated is able to create/update/delete a product

http basic authentication\
username:admin\
password:123456

### Product endpoints

* get product list

```
curl --location 'localhost:8080/product'
```

* create new product only authorized by admin

```
curl --location 'localhost:8080/product/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46MTIzNDU2' \
--data '{
    "productName": "pad",
    "description": "it is a pad",
    "originalPrice":"300.0",
    "sellingPrice":"300.0",
    "hasDiscountPolicy":false,
    "category":"MOBILES",
    "stockNum":30,
    "createUser":2
}'
```

* update product discount percentage to 40% for second product by product id = 1 only authorized by admin

```
curl --location --request PUT 'localhost:8080/product/1/update-discount' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46MTIzNDU2' \
--header 'Cookie: JSESSIONID=167C38BD7848E1401B052F72D2F5213F' \
--data '40'
```

* delete product id = 2 only authorized by admin

```
curl --location --request DELETE 'localhost:8080/product/2' \
--header 'Authorization: Basic YWRtaW46MTIzNDU2' \
--header 'Cookie: JSESSIONID=167C38BD7848E1401B052F72D2F5213F'
```

### Basket endpoints

* get all basket items from basket for customer id = 1

```
curl --location 'localhost:8080/basket/1/items'
```

* add 5 products whose id =1 to basket of customer id =1

```
curl --location 'localhost:8080/basket/1/add-to-basket/1' \
--header 'Content-Type: application/json' \
--data '5'
```

* remove 1 product whose id =1 to basket of customer id =1

```
curl --location 'localhost:8080/basket/1/remove-from-basket/1' \
--header 'Content-Type: application/json' \
--data '2'
```

* calculate receipt detail for basket of customer id = 1

```
curl --location 'localhost:8080/basket/1/calculateBasket'
```

You can find more details by http://localhost:8080/swagger-ui/index.html

Metrics is exposed on http://localhost:8080/actuator/metrics


