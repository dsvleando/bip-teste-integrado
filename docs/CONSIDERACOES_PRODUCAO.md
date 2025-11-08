# Considerações e Decisões Técnicas

## 📋 Resumo do Projeto

Este documento descreve as decisões técnicas tomadas durante o desenvolvimento, as funcionalidades implementadas e considerações importantes para produção.

---

## 🏗️ Arquitetura

### Backend - Arquitetura Hexagonal

O backend foi estruturado seguindo os princípios da **Arquitetura Hexagonal (Ports and Adapters)**, separando claramente:

- **Domain Layer** (`ejb-module/domain`): Regras de negócio puras, sem dependências externas
- **Application Layer** (`ejb-module/application`): Casos de uso e orquestração
- **Infrastructure Layer** (`ejb-module/infrastructure`): Adaptadores para persistência, APIs externas, etc.
- **API Layer** (`backend-module/api`): Controllers REST, DTOs, mapeadores

**Benefícios:**
- Testabilidade: Domain e Application podem ser testados sem infraestrutura
- Flexibilidade: Fácil trocar implementações (ex: JPA por MongoDB)
- Manutenibilidade: Separação clara de responsabilidades

---

## 🔒 Controle de Concorrência e Consistência

### Lock Otimista (Optimistic Locking)

Para garantir **consistência dos valores financeiros** em transações concorrentes, foi implementado **Optimistic Locking** usando o campo `version` na entidade `BeneficioEntity`.

**Como funciona:**
- Cada atualização incrementa o `version`
- Se duas transações tentam atualizar simultaneamente, apenas uma terá sucesso
- A outra receberá `OptimisticLockException` e deve retentar

**Por que Lock Otimista:**
- Melhor performance em cenários de baixa/média contenção
- Não bloqueia leituras
- Adequado para transações financeiras onde conflitos são raros mas críticos

### Lock Pessimista por Beneficiário

Além do lock otimista, foi implementado um **sistema de locks por beneficiário** usando `ReentrantLock` através de um cache de locks por ID de beneficiário.

**Objetivos:**
1. **Consistência**: Garante que transferências envolvendo o mesmo beneficiário sejam serializadas
2. **Enfileiramento**: Transações que usam o mesmo beneficiário são enfileiradas automaticamente
3. **Prevenção de Deadlocks**: Ordenação dos locks por ID evita deadlocks
4. **Timeout**: Evita travamentos indefinidos com timeout de 5 segundos

**Como funciona:**
- Cada beneficiário tem seu próprio lock
- Transferências são ordenadas por ID (menor primeiro) para evitar deadlocks
- Locks são reutilizados via cache (`ConcurrentHashMap`)
- Uso de `tryLock()` com timeout de 5 segundos para evitar bloqueios indefinidos
- Se o lock não for adquirido em 5 segundos, a operação falha com exceção informando timeout

**Por que necessário:**
- Em microsserviços, múltiplas instâncias podem processar transferências simultaneamente
- Sem locks, duas transferências do mesmo beneficiário podem causar race conditions
- Garante ordem de processamento e consistência dos saldos
- Timeout previne que threads fiquem bloqueadas indefinidamente em caso de contenção alta

---

## 💰 Frontend - Campo Monetário

### Componente Reutilizável `CurrencyInputComponent`

Foi criado um componente Angular standalone para entrada de valores monetários:

**Funcionalidades:**
- Formatação automática (vírgula como separador decimal)
- Sanitização de entrada (apenas números e vírgula)
- Validação de formato e valor mínimo
- Integração com Angular Reactive Forms via `ControlValueAccessor`

**Decisões técnicas:**
- Usado `@SkipSelf()` para evitar dependência circular com `NgControl`
- Validação delegada ao `FormControl` pai (evita circular dependency)
- Formatação no blur para melhor UX

**Por que componente reutilizável:**
- Consistência na formatação monetária em toda aplicação
- Facilita manutenção (mudanças em um único lugar)
- Reduz código duplicado

### Filtro de Beneficiários Inativos

No diálogo de transferência, apenas **benefícios ativos** são exibidos nas comboboxes através de filtro que verifica o campo `ativo === true`.

**Por que:**
- Evita transferências para/de benefícios inativos
- Melhora UX (menos opções irrelevantes)
- Previne erros do usuário

---

## 🐳 Docker e Infraestrutura

### Docker Compose

A aplicação foi containerizada usando Docker Compose com:

- **Multi-stage builds** para otimizar tamanho das imagens
- **Health checks** para ambos os serviços
- **Networking** isolado entre frontend e backend
- **Nginx** como reverse proxy no frontend

### Configuração Dinâmica de URLs

O frontend permite configurar a URL da API via variáveis de ambiente:

- **Build time**: `API_URL` via `ARG` no Dockerfile
- **Runtime**: `BACKEND_API_HOST` e `BACKEND_API_PORT` para proxy Nginx
- **Frontend**: `window.__env.apiUrl` injetado via `env.js`

**Benefícios:**
- Permite deploy em diferentes ambientes sem rebuild
- Facilita uso de múltiplos nodes/instâncias
- Configuração via variáveis de ambiente

---

## ⚠️ Considerações para Produção

### 1. Campo de Pesquisa para Beneficiários

**Problema atual:** Combobox com todos os beneficiários pode ficar muito grande.

**Solução sugerida:**
- Implementar campo de pesquisa/autocomplete
- Busca incremental via API (`/api/v1/beneficios/search?q=...`)
- Paginação ou limite de resultados
- Usar bibliotecas como `@angular/material/autocomplete`

**Implementação sugerida:**
- Backend: Criar endpoint GET `/search` que recebe parâmetro de query `q` e retorna lista filtrada de beneficiários
- Frontend: Usar componente `mat-autocomplete` com data source reativo que busca na API conforme o usuário digita

### 2. CORS Configurável

**Problema atual:** CORS liberado para todas as origens (`*`).

**Solução sugerida:**
- Configurar propriedades em `application-prod.properties` com lista de origens permitidas
- Usar variáveis de ambiente para definir origens, métodos e headers permitidos
- Modificar `CorsConfig` para ler essas propriedades e configurar CORS dinamicamente
- Permitir credenciais apenas quando necessário

### 3. Módulo EJB

**Status atual:** Módulo EJB mantido para demonstração da arquitetura hexagonal.

**Para produção:**
- Considerar remover o módulo EJB se não houver necessidade real
- Manter apenas Spring Boot com a mesma estrutura hexagonal
- Benefícios: menos complexidade, build mais rápido, menos dependências

**Alternativa:** Se precisar manter EJB:
- Usar EJB apenas para integração com sistemas legados
- Manter lógica de negócio em módulos Spring Boot
- Usar JMS/Messaging para comunicação entre módulos

### 4. Autenticação e Autorização

**Status atual:** Nenhuma autenticação implementada.

**Solução sugerida para produção:**

#### Backend - Spring Security + OAuth2

- Adicionar dependência `spring-boot-starter-oauth2-resource-server` no `pom.xml`
- Criar classe de configuração `SecurityConfig` que implementa `SecurityFilterChain`
- Configurar OAuth2 Resource Server com JWT decoder
- Definir regras de autorização para endpoints protegidos
- Endpoints da API de benefícios devem requerer autenticação

#### Keycloak como OAuth2 Provider

**Configuração sugerida:**
- Keycloak como servidor de autenticação
- Clientes OAuth2 para frontend e backend
- Roles e permissões configuradas no Keycloak
- JWT tokens com claims customizados

**Fluxo:**
1. Usuário faz login no Keycloak
2. Frontend recebe access token
3. Frontend envia token em `Authorization: Bearer <token>`
4. Backend valida token e extrai roles/permissões
5. Spring Security autoriza baseado em roles

**Integração Frontend:**
- Criar interceptor HTTP que adiciona token de autenticação em todas as requisições
- Interceptor deve ler token do serviço de autenticação e incluir no header `Authorization`
- Token deve ser enviado no formato `Bearer <token>`

### 5. Banco de Dados

**Status atual:** H2 in-memory (apenas para desenvolvimento/demo).

**Para produção:**
- Usar PostgreSQL ou MySQL para persistência real
- Configurar connection pooling (HikariCP já incluído no Spring Boot)
- Implementar migrações com Liquibase
- Configurar backups automáticos
- Considerar read replicas para alta disponibilidade

**Configuração sugerida:**
- URL de conexão via variáveis de ambiente (`DB_USER`, `DB_PASSWORD`)
- `spring.jpa.hibernate.ddl-auto=validate` para não criar/alterar schema automaticamente
- Habilitar Liquibase para gerenciar migrações de schema
- Usar variáveis de ambiente para credenciais sensíveis

### 6. Logging e Monitoramento

**Sugestões:**
- **Logging estruturado**: Usar Logback com JSON format para integração com ELK Stack
- **Métricas**: Prometheus + Grafana para monitoramento
- **Tracing**: Jaeger ou Zipkin para rastreamento distribuído
- **Health checks**: Expandir endpoints de health para incluir dependências (DB, etc.)

**Implementação de Health Checks:**
- Criar classes que implementam `HealthIndicator` para verificar saúde de componentes específicos
- Health indicators devem retornar status `UP` ou `DOWN` com detalhes sobre o estado do componente
- Integrar verificações de banco de dados, APIs externas e outros serviços dependentes

### 7. Testes

**Status atual:** Testes unitários e de integração implementados.

**Melhorias sugeridas:**
- Testes end-to-end (E2E) com Cypress ou Playwright
- Testes de carga com JMeter ou Gatling
- Testes de contrato com Pact para APIs
- Cobertura de código mantida acima de 80%

### 8. CI/CD

**Status atual:** Pipeline GitHub Actions configurado.

**Melhorias sugeridas:**
- Adicionar testes de segurança (OWASP Dependency Check, Snyk)
- Análise estática de código (SonarQube)
- Deploy automático em ambientes de staging/produção
- Rollback automático em caso de falhas

### 9. Arquitetura de Microfrontends com Single-SPA

**Status atual:** Frontend monolítico Angular standalone.

**Solução sugerida para produção:**

#### Por que Microfrontends?

Para aplicações que crescem e precisam de:
- **Equipes independentes**: Cada equipe pode desenvolver e deployar seu módulo independentemente
- **Escalabilidade**: Adicionar novos módulos sem impactar existentes
- **Tecnologias diversas**: Permitir diferentes frameworks (Angular, React, Vue) no mesmo sistema
- **Deploy independente**: Atualizar um módulo sem redeployar toda aplicação
- **Isolamento de falhas**: Problemas em um módulo não derrubam toda aplicação

#### Single-SPA como Orquestrador

**Single-SPA** é um framework JavaScript para orquestrar múltiplos microfrontends em uma única aplicação.

**Arquitetura sugerida:**

A arquitetura consiste em um Shell App (Single-SPA Root) que gerencia roteamento, autenticação compartilhada e layout base (header, sidebar, etc). Abaixo do shell, múltiplos microfrontends são carregados dinamicamente: Benefícios (Angular), Usuários (React), Relatórios (Vue) e Administração (Angular).

**Estrutura de módulos:**

1. **Shell Application** (Root Config)
   - Gerencia roteamento
   - Carrega microfrontends dinamicamente
   - Compartilha serviços comuns (auth, i18n, etc)

2. **Microfrontends** (Applications)
   - Benefícios (Angular) - módulo atual
   - Usuários (React)
   - Relatórios (Vue)
   - Administração (Angular)

**Implementação:**

#### 1. Shell App (Root Config)

- Criar arquivo `root-config.js` que importa e registra aplicações usando `registerApplication`
- Cada aplicação deve ter nome único, função de carregamento e condição de ativação baseada em rotas
- Chamar `start()` para iniciar o Single-SPA após registrar todas as aplicações

#### 2. Módulo Benefícios (Angular)

- Criar arquivo `main.single-spa.ts` que exporta lifecycle hooks (`bootstrap`, `mount`, `unmount`)
- Usar `singleSpaAngular` helper para integrar Angular com Single-SPA
- Configurar template, Router e NgZone para funcionamento correto do Angular dentro do microfrontend

#### 3. Module Federation (Webpack 5)

Alternativa moderna usando **Module Federation**:

- **Shell App**: Configurar `ModuleFederationPlugin` com lista de remotes (outros microfrontends)
- Definir dependências compartilhadas como singletons para evitar duplicação
- **Módulos**: Expor módulos específicos via `exposes` e configurar dependências compartilhadas
- Cada módulo gera um `remoteEntry.js` que é carregado dinamicamente pelo shell

**Benefícios da abordagem:**

✅ **Deploy independente**: Cada módulo pode ser deployado separadamente  
✅ **Escalabilidade**: Fácil adicionar novos módulos sem tocar nos existentes  
✅ **Isolamento**: Falhas em um módulo não afetam outros  
✅ **Performance**: Carregamento lazy de módulos conforme necessário  
✅ **Tecnologia agnóstica**: Permite diferentes frameworks coexistindo  

**Desafios e considerações:**

⚠️ **Complexidade inicial**: Setup mais complexo que aplicação monolítica  
⚠️ **Versionamento**: Gerenciar versões de dependências compartilhadas  
⚠️ **Comunicação**: Definir contratos claros entre módulos  
⚠️ **Testes**: Testes E2E mais complexos com múltiplos módulos  
⚠️ **Bundle size**: Pode aumentar se dependências não forem compartilhadas corretamente  

**Estratégia de migração:**

1. **Fase 1**: Criar shell app com Single-SPA
2. **Fase 2**: Converter módulo atual de Benefícios em microfrontend
3. **Fase 3**: Adicionar novos módulos como microfrontends
4. **Fase 4**: Migrar gradualmente outros módulos existentes

**Ferramentas recomendadas:**

- **Single-SPA**: Orquestração de microfrontends
- **Module Federation**: Compartilhamento de código (Webpack 5)
- **Nx**: Monorepo para gerenciar múltiplos apps
- **Lerna**: Versionamento e publicação de pacotes
- **Rush**: Build system para monorepos grandes

**Estrutura de projeto sugerida:**

- `shell/`: Shell app (Single-SPA root) que orquestra todos os microfrontends
- `apps/`: Diretório contendo todos os microfrontends (beneficios, usuarios, relatorios)
- `shared/`: Bibliotecas compartilhadas entre microfrontends
  - `ui-components/`: Componentes reutilizáveis
  - `auth/`: Serviço de autenticação compartilhado
  - `utils/`: Utilitários comuns
- `tools/`: Scripts e ferramentas de build/deploy

### 10. Deploy em Kubernetes/Rancher

**Status atual:** Deploy via Docker Compose (apenas para desenvolvimento/local).

**Solução sugerida para produção:**

#### Kubernetes

Para ambientes de produção, recomenda-se deploy em **Kubernetes** para obter:

- **Alta disponibilidade**: Múltiplas réplicas dos serviços
- **Escalabilidade automática**: Horizontal Pod Autoscaler (HPA)
- **Auto-healing**: Restart automático de pods com falha
- **Load balancing**: Distribuição de carga entre pods
- **Rolling updates**: Atualizações sem downtime
- **Resource management**: Limites de CPU e memória

**Estrutura de recursos Kubernetes:**

- **Namespace**: Isolamento lógico (`bip-beneficios`)
- **Deployments**: Backend e Frontend com múltiplas réplicas
- **Services**: ClusterIP para comunicação interna, NodePort/LoadBalancer para exposição externa
- **ConfigMaps**: Configurações não sensíveis (URLs, ports, etc)
- **Secrets**: Credenciais de banco de dados, tokens, etc
- **Ingress**: Roteamento HTTP/HTTPS e SSL termination
- **HorizontalPodAutoscaler**: Escalamento automático baseado em CPU/memória
- **PodDisruptionBudget**: Garantir disponibilidade durante manutenções

**Configurações recomendadas:**

- **Backend Deployment**:
  - Réplicas: mínimo 2, máximo 5
  - Resources: requests (500m CPU, 512Mi mem), limits (1000m CPU, 1Gi mem)
  - Health checks: liveness e readiness probes no endpoint `/actuator/health`
  - Strategy: RollingUpdate com maxSurge 1 e maxUnavailable 0

- **Frontend Deployment**:
  - Réplicas: mínimo 2, máximo 5
  - Resources: requests (100m CPU, 128Mi mem), limits (200m CPU, 256Mi mem)
  - Health checks: liveness e readiness no endpoint `/health`

- **Database**:
  - Usar StatefulSet para PostgreSQL/MySQL com volumes persistentes
  - Configurar backups automáticos
  - Considerar operadores (PostgreSQL Operator, MySQL Operator)

- **Ingress**:
  - Configurar TLS/SSL com certificados (Let's Encrypt via cert-manager)
  - Roteamento: `/api/*` para backend, `/*` para frontend
  - Rate limiting e WAF (Web Application Firewall)

#### Rancher

**Rancher** é uma plataforma de gerenciamento de Kubernetes que facilita:

- **Gestão multi-cluster**: Gerenciar múltiplos clusters Kubernetes
- **UI amigável**: Interface gráfica para operações
- **CI/CD integrado**: GitOps com Rancher Fleet
- **Monitoramento**: Integração com Prometheus/Grafana
- **RBAC**: Controle de acesso baseado em roles

**Vantagens do Rancher:**

- Simplifica operações Kubernetes para equipes menos experientes
- Gestão centralizada de múltiplos clusters
- Deploy via UI ou GitOps (Fleet)
- Monitoramento e alertas integrados
- Backup e restore de clusters

**Estratégia de deploy:**

1. **Preparação**:
   - Criar namespace `bip-beneficios`
   - Configurar secrets para credenciais de banco de dados
   - Criar ConfigMaps para configurações

2. **Deploy Backend**:
   - Criar Deployment com imagem do GitHub Container Registry
   - Configurar Service (ClusterIP)
   - Configurar Ingress para exposição externa
   - Configurar HPA para escalamento automático

3. **Deploy Frontend**:
   - Criar Deployment com imagem do GitHub Container Registry
   - Configurar Service (ClusterIP)
   - Configurar Ingress apontando para o mesmo domínio do backend

4. **Deploy Database**:
   - Usar StatefulSet ou operador de banco de dados
   - Configurar volumes persistentes
   - Configurar backups automáticos

5. **Monitoramento**:
   - Instalar Prometheus Operator
   - Configurar ServiceMonitor para scraping de métricas
   - Configurar Grafana dashboards
   - Configurar alertas (AlertManager)

**Exemplo de estrutura de arquivos:**

```
k8s/
├── namespace.yaml
├── backend/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── secret.yaml (template)
│   └── hpa.yaml
├── frontend/
│   ├── deployment.yaml
│   ├── service.yaml
│   └── configmap.yaml
├── database/
│   ├── statefulset.yaml
│   ├── service.yaml
│   └── pvc.yaml
└── ingress.yaml
```

**Ferramentas recomendadas:**

- **kubectl**: CLI para Kubernetes
- **Helm**: Gerenciador de pacotes Kubernetes (opcional)
- **Rancher**: Plataforma de gerenciamento
- **k9s**: Terminal UI para Kubernetes
- **Lens**: IDE para Kubernetes
- **ArgoCD**: GitOps para deploy contínuo
- **cert-manager**: Gerenciamento automático de certificados TLS

**Considerações importantes:**

- Usar **Secrets** para dados sensíveis (nunca commitados no Git)
- Configurar **Resource Quotas** e **Limit Ranges** no namespace
- Implementar **Network Policies** para segurança de rede
- Configurar **Pod Security Policies** ou **Pod Security Standards**
- Usar **Service Mesh** (Istio, Linkerd) para observabilidade avançada
- Implementar **GitOps** com ArgoCD ou Rancher Fleet para deploy automatizado

---

## 📊 Resumo das Decisões Técnicas

| Aspecto | Decisão Atual | Sugestão para Produção |
|---------|---------------|------------------------|
| **Lock** | Otimista + Pessimista por beneficiário | Manter, adicionar métricas de contenção |
| **CORS** | Liberado para todos (`*`) | Configurar origens específicas via env |
| **Autenticação** | Não implementada | Keycloak + Spring Security OAuth2 |
| **Banco de Dados** | H2 in-memory | PostgreSQL/MySQL com migrações |
| **Módulo EJB** | Mantido para demo | Remover se não necessário |
| **Frontend Beneficiários** | Combobox simples | Autocomplete com busca incremental |
| **Arquitetura Frontend** | Monolítico Angular | Microfrontends com Single-SPA |
| **Deploy** | Docker Compose (local) | Kubernetes/Rancher |
| **Monitoramento** | Básico (health checks) | Prometheus + Grafana + ELK Stack |
| **Testes** | Unitários + Integração | Adicionar E2E + Carga + Contrato |

---

## 🎯 Conclusão

Este projeto demonstra uma arquitetura bem estruturada com separação de responsabilidades, controle de concorrência adequado para transações financeiras, e uma base sólida para evolução. As sugestões apresentadas visam tornar a aplicação pronta para produção com segurança, escalabilidade e manutenibilidade.

**Próximos passos recomendados:**
1. Implementar autenticação/autorização
2. Migrar para banco de dados de produção
3. Configurar CORS adequadamente
4. Implementar busca incremental de beneficiários
5. Adicionar monitoramento e observabilidade
6. Expandir testes (E2E, carga, segurança)
7. Migrar para arquitetura de microfrontends com Single-SPA
8. Configurar deploy em Kubernetes/Rancher para produção

