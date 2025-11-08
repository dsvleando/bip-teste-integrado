# 🏛️ Arquitetura Hexagonal

Este projeto foi organizado seguindo os princípios da **Arquitetura Hexagonal** (Ports and Adapters), separando claramente o domínio da infraestrutura.

## 📐 Estrutura da Arquitetura

```
ejb-module/
├── domain/                          # Domínio (Core)
│   ├── model/                      # Entidades de domínio
│   │   └── Beneficio.java
│   ├── port/
│   │   ├── input/                  # Portas primárias (entrada)
│   │   │   └── BeneficioUseCase.java
│   │   └── output/                 # Portas secundárias (saída)
│   │       └── BeneficioRepository.java
│   └── exception/                  # Exceções de domínio
│       └── BeneficioException.java
│
├── application/                     # Camada de aplicação
│   └── service/
│       └── BeneficioService.java
│
└── infrastructure/                  # Infraestrutura (Adaptadores)
    ├── adapter/
    │   ├── input/                  # Adaptadores primários (entrada)
    │   │   └── BeneficioEjbAdapter.java
    │   └── output/                 # Adaptadores secundários (saída)
    │       └── JpaBeneficioRepository.java
    └── mapper/
        └── BeneficioMapper.java
```

## 🎯 Camadas

### 1. **Domain (Domínio/Core)**
- **Responsabilidade**: Contém a lógica de negócio pura, sem dependências externas
- **Componentes**:
  - **Model**: Entidades de domínio (`Beneficio`)
  - **Ports**: Interfaces que definem contratos
    - **Input Ports**: Casos de uso (ex: `BeneficioUseCase`)
    - **Output Ports**: Repositórios e serviços externos (ex: `BeneficioRepository`)
  - **Exceptions**: Exceções de domínio

### 2. **Application (Aplicação)**
- **Responsabilidade**: Orquestra casos de uso e coordena fluxos
- **Componentes**:
  - **Services**: Implementam os casos de uso (ex: `BeneficioService`)

### 3. **Infrastructure (Infraestrutura)**
- **Responsabilidade**: Implementa adaptadores que conectam o domínio com tecnologias externas
- **Componentes**:
  - **Input Adapters**: Adaptadores primários (entrada)
    - EJB Adapter (`BeneficioEjbAdapter`)
    - REST Controller (Spring Boot - a ser criado)
  - **Output Adapters**: Adaptadores secundários (saída)
    - JPA Repository (`JpaBeneficioRepository`)
  - **Mappers**: Mapeamento entre domínio e infraestrutura

## 🔄 Fluxo de Dados

### Transferência de Benefício

```
1. REST Controller (Adaptador Primário)
   ↓
2. BeneficioService (Backend - Adaptador Primário)
   ↓
3. BeneficioUseCase (Porta Primária)
   ↓
4. BeneficioService (Aplicação - Implementação)
   ↓
5. BeneficioRepository (Porta Secundária)
   ↓
6. JpaBeneficioRepository (Adaptador Secundário)
   ↓
7. EntityManager (JPA)
```

### Operações CRUD

```
1. REST Controller (Adaptador Primário)
   ↓
2. BeneficioService (Backend - Adaptador Primário)
   ↓
3. BeneficioUseCase (Porta Primária)
   ↓
4. BeneficioService (Aplicação - Implementação)
   ↓
5. BeneficioRepository (Porta Secundária)
   ↓
6. JpaBeneficioRepository (Adaptador Secundário)
```

## ✅ Benefícios da Arquitetura Hexagonal

1. **Desacoplamento**: O domínio não depende de tecnologias específicas
2. **Testabilidade**: Fácil criar mocks/stubs para testes
3. **Flexibilidade**: Pode trocar JPA por MongoDB, REST por GraphQL, etc.
4. **Manutenibilidade**: Código organizado e responsabilidades claras
5. **Independência**: Domínio pode ser testado isoladamente

## 🔌 Portas e Adaptadores

### Portas Primárias (Input)
- **BeneficioUseCase**: Define o contrato para todas as operações de benefícios (listar, buscar, criar, atualizar, remover, transferir)

### Portas Secundárias (Output)
- **BeneficioRepository**: Define o contrato para persistência

### Adaptadores Primários (Input)
- **BeneficioEjbAdapter**: Adapta chamadas EJB para o caso de uso
- **BeneficioController** (Spring Boot): Adapta chamadas REST para o caso de uso

### Adaptadores Secundários (Output)
- **JpaBeneficioRepository**: Implementa persistência usando JPA

## 📝 Notas Importantes

- O domínio **não deve** ter dependências de frameworks (Spring, JPA, EJB)
- Os adaptadores são os únicos que conhecem tecnologias específicas
- As portas definem contratos que o domínio espera
- Os adaptadores implementam essas portas usando tecnologias específicas

