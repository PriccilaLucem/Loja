# Loja API

API RESTful para gerenciamento de produtos em uma loja, desenvolvida com **Spring Boot** e **MySQL**.

## 🚀 Tecnologias Utilizadas

- Java 17+
- Spring Boot
- Spring Web
- Spring Data JPA
- MySQL
- JWT (Autenticação)
- Swagger (Documentação da API)
- SLF4J (Logger)

## 📦 Funcionalidades

- Criar produtos
- Atualizar quantidade em estoque
- Atualizar dados de um produto
- Remover produtos
- Associar produtos a uma loja
- Documentação via Swagger

## 🔧 Requisitos

- Java 17+
- MySQL Server
- Maven 3.8+

##✅ Testes

Para rodar os testes automatizados:

```bash
./mvnw test
```

## ⚙️ Configuração do Projeto

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/loja-api.git
cd loja-api
```


2. Configure o banco de dados no arquivo application.properties:
```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/loja
spring.datasource.username=root
spring.datasource.password=suasenha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

# JWT
```
jwt.secret=secretaChave
jwt.expiration=86400000
```

3. Execute o projeto
```bash
./mvnw spring-boot:run
```


## 🔐 Autenticação
A API utiliza JWT para autenticação. Após login, um token deve ser incluído no header de todas as requisições protegidas:
Authorization: Bearer <seu_token_jwt>

## 📘 Documentação da API
Acesse a documentação Swagger através:
http://localhost:8080/swagger-ui.html

## 👩‍💻 Autor(a)
- Desenvolvido por PriccilaLucem/Frehlya
- https://linkedin.com/in/priccila-lucem | https://github.com/PriccilaLucem

# 📄 Licença
Copyright © 2025 Seu Nome

Todos os direitos reservados.

Este software é fornecido apenas para fins de visualização.  
Não é permitido copiar, modificar, redistribuir, sublicenciar, ou usar este software de qualquer forma, parcial ou total, sem permissão expressa e por escrito do autor.

Contato: priccilalucem@gmail.com

