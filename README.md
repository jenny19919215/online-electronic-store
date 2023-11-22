# Online-electronic-store

This is a poc of an online electronic store project based on spring boot.
Optimistic lock solution has been implemented when add/remove items from basket to avoid concurrent read/write.

Project requirement can be found in
file [Bullish Technical Assessment (Take home).pdf](./Bullish%20Technical%20Assessment%20(Take%20home).pdf)

## Build

* Run all the unit tests and integration tests
    ```
   mvn clean install
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

* Or pull image from docker repo
*

* Run

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
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A' \
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

* update product discount percentage to 40% by product id = 1 only authorized by admin

```
curl --location --request PUT 'localhost:8080/product/1/update-discount' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46MTIzNDU2' \
--header 'Cookie: JSESSIONID=04AAA7FFAF5C02204E410046DAD67727' \
--data '40'
```

* delete product id = 2 only authorized by admin

```
curl --location --request DELETE 'localhost:8080/product/2' \
--header 'Authorization: Basic YWRtaW46MTIzNDU2' \
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A'
```

### Basket endpoints

* get all basket items from basket for customer id = 1

```
curl --location 'localhost:8080/basket/1/items' \
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A'
```

* add 5 products whose id =1 to basket of customer id =1

```
curl --location 'localhost:8080/basket/1/add-to-basket/1' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A' \
--data '5'
```

* remove 1 product whose id =1 to basket of customer id =1

```
curl --location 'localhost:8080/basket/1/remove-from-basket/1' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A' \
--data '1'
```

* calculate receipt detail for basket of customer id = 1

```
curl --location 'localhost:8080/basket/1/calculateBasket' \
--header 'Cookie: JSESSIONID=61C867076B3D8F2C64419A38FEFB9B2A'
```

You can find more details by http://localhost:8080/swagger-ui/index.html

Metrics is exposed on http://localhost:8080/actuator/metrics


