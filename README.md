
# 📦 Storage Wester

API desenvolvida para controle de estoque e movimentações com visualização 3D. Utiliza Java, Spring Boot e PostgreSQL, além de integração com Swagger para documentação da API.

---

## 🚀 Tecnologias Utilizadas

- Java 17  
- Spring Boot 3.x  
- Maven 3.6.3  
- PostgreSQL  
- Springdoc OpenAPI (Swagger)  
- Spring Security  
- JPA / Hibernate

---

## ▶️ Como Executar o Projeto

### 1. Clonar o Repositório

```bash
git clone https://github.com/Vinicius-E/Storage_Wester.git
cd Storage_Wester
```

### 2. Criar o Banco de Dados

```sql
CREATE DATABASE storage;
CREATE USER postgres WITH ENCRYPTED PASSWORD 'Vini13lagoa$';
GRANT ALL PRIVILEGES ON DATABASE storage TO postgres;
```

> ⚠️ Atualize o `application.yml` se você mudar nome do banco ou credenciais.

### 3. Rodar o Projeto

```bash
./mvnw spring-boot:run
```

---

## 🌐 Acesso à Documentação Swagger

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📂 Estrutura

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── wester/
│   │           └── storage/
│   │               ├── config/        # Configurações globais (CORS, segurança, etc.)
│   │               ├── controller/    # Camada responsável por expor endpoints REST
│   │               ├── dto/           # Objetos de transferência de dados (Data Transfer Object)
│   │               ├── model/         # Entidades de domínio persistidas no banco de dados
│   │               ├── repository/    # Interfaces que acessam o banco via Spring Data JPA
│   │               ├── service/       # Lógica de negócio da aplicação
│   │               └── StorageApplication.java  # Classe principal que inicia a aplicação Spring Boot
│   └── resources/
│       └── application.yml            # Configurações de banco, Swagger, logs, etc.
```

---

## 📎 Anexos

As imagens a seguir demonstram parte do funcionamento e estrutura do projeto:

<img width="1547" height="886" alt="Schemas_01" src="https://github.com/user-attachments/assets/73f0e96f-4484-40a1-9083-14e32a9497bd" />  
<img width="1545" height="779" alt="Swagger_04" src="https://github.com/user-attachments/assets/4561417d-2a00-4b3a-b3fb-82c0236204f6" />
<img width="1525" height="880" alt="Swagger_03" src="https://github.com/user-attachments/assets/4d0efd48-7ba0-4ead-91e3-14822b48ab3d" />
<img width="1530" height="957" alt="Swagger_02" src="https://github.com/user-attachments/assets/41d5ece1-28ff-49d3-9d24-95b3cb608416" />
<img width="1532" height="852" alt="Swagger_01" src="https://github.com/user-attachments/assets/e04aa97f-8301-4fa0-8b2b-726ba147e6af" />
<img width="1534" height="966" alt="Schemas_02" src="https://github.com/user-attachments/assets/767d30f3-303d-4952-b2f1-7db53fd93b75" />


> Coloque suas imagens na pasta `/anexos/` dentro do projeto.

---

## 👤 Autor

**Vinicius Eduardo Da Silva**  
viniciuseduardo0702@hotmail.com

---

## 📄 Licença

Este projeto está sob a licença MIT.

