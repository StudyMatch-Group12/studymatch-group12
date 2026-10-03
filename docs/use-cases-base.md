# Casos de Uso Base — StudyMatch (Sprint 1)

Documentação dos casos de uso correspondentes às áreas de capacidade nucleares do sistema StudyMatch, em conformidade com o guião do Sprint 1.

---

### UC01: Registar e Gerir Perfil do Estudante
* **Objetivo:** Permitir a criação e manutenção da identidade e dados académicos do estudante na plataforma.
* **Ator Principal:** Estudante.
* **Cenário de Sucesso:**
    1. O estudante acede à área do seu perfil académico.
    2. Introduz e confirma os seus dados institucionais: identificador/número de estudante, nome completo, e-mail institucional e curso de licenciatura associado.
    3. O sistema valida os campos obrigatórios e confirma o formato institucional do e-mail.
    4. O sistema persiste as informações e exibe o perfil atualizado.
* **Fluxos Alternativos e Exceções:**
    - *3a. Dados em falta ou e-mail inválido:* O sistema exibe mensagem de erro e solicita a correção dos campos.
    - *3b. Identificador já em uso:* O sistema bloqueia a criação e notifica duplicação.
* **Regras de Negócio:**
    - O e-mail e o número de estudante são identificadores únicos e imutáveis após verificação.
* **Conceitos de Domínio:** `Student`, `DegreeProgram`, `Email`, `StudentNumber`.

---

### UC02: Registar Histórico e Trajetória Académica
* **Objetivo:** Registar as inscrições, tentativas, unidades curriculares (UCs) concluídas e classificações obtidas.
* **Ator Principal:** Docente / Administrador Académico (ou importação via sistema).
* **Cenário de Sucesso:**
    1. O ator seleciona o estudante a associar.
    2. Indica a unidade curricular, ano letivo, semestre e modalidade de inscrição (primeira inscrição ou repetição).
    3. Insere a classificação final obtida (escala de 0 a 20 valores) e o estado de aproveitamento.
    4. O sistema persiste o registo no histórico curricular do estudante.
* **Fluxos Alternativos e Exceções:**
    - *3a. Nota fora do intervalo regulamentar (< 0 ou > 20):* O sistema rejeita o valor e solicita correção.
    - *3b. Inscrição duplicada no mesmo período:* O sistema alerta e exige confirmação de atualização.
* **Regras de Negócio:**
    - Unidade curricular concluída com nota igual ou superior a 10 valores.
    - Reprovações anteriores permanecem registadas como histórico de tentativas e não são eliminadas.
* **Conceitos de Domínio:** `CourseUnit`, `Enrollment`, `Attempt`, `Grade`, `AcademicHistory`.

---

### UC03: Mapear e Calcular Competências Académicas
* **Objetivo:** Associar competências a unidades curriculares e quantificar o nível de aptidão alcançado pelo estudante.
* **Ator Principal:** Sistema (com parametrização pelo Docente/Coordenador).
* **Cenário de Sucesso:**
    1. O docente associa competências a unidades curriculares com pesos percentuais ponderados.
    2. Após registo de aproveitamento de uma UC pelo estudante, o sistema executa o recálculo automático das competências correlacionadas.
    3. A matriz individual de competências do estudante é atualizada.
* **Fluxos Alternativos e Exceções:**
    - *1a. Ponderação de competências inconsistente:* O sistema exige que a soma dos pesos na UC seja válida antes de gravar.
* **Regras de Negócio:**
    - O nível de proficiência é uma métrica derivada das classificações obtidas nas unidades curriculares associadas à competência.
* **Conceitos de Domínio:** `Competency`, `CompetencyWeight`, `ProficiencyLevel`.

---

### UC04: Consultar Perfil Integrado de Competências
* **Objetivo:** Exibir a representação consolidada do histórico, créditos e maturidade de competências do estudante.
* **Ator Principal:** Estudante / Docente.
* **Cenário de Sucesso:**
    1. O utilizador acede à visualização do perfil detalhado do estudante.
    2. O sistema compila o histórico curricular, créditos completados e o mapa multidimensional de competências.
    3. O sistema apresenta o relatório gráfico consolidado.
* **Fluxos Alternativos e Exceções:**
    - *2a. Ausência de histórico:* O sistema sinaliza que o perfil se encontra em estado inicial (Cold Start).
* **Regras de Negócio:**
    - A visualização integral respeita regras de privacidade: acessível apenas ao estudante e a docentes das turmas em que está inscrito.
* **Conceitos de Domínio:** `StudentProfile`, `SkillRadar`, `AcademicProgress`.

---

### UC05: Resolver Cold Start de Novo Estudante
* **Objetivo:** Obter uma representação inicial de perfil para estudantes sem histórico curricular registado (ex.: caloiros).
* **Ator Principal:** Estudante.
* **Cenário de Sucesso:**
    1. O estudante recém-chegado acede à plataforma.
    2. O sistema deteta histórico curricular nulo e ativa o questionário inicial de autoavaliação (Cold Start).
    3. O estudante declara competências prévias, interesses temáticos e preferências de papel na equipa.
    4. O sistema gera uma pontuação provisória que permite a inclusão imediata do estudante nos processos de agrupamento.
* **Fluxos Alternativos e Exceções:**
    - *3a. Omissão do preenchimento:* O sistema aplica um valor base de neutralidade com aviso de recomendação subótima.
* **Regras de Negócio:**
    - As pontuações obtidas por autoavaliação perdem peso gradualmente à medida que avaliações formais e notas reais forem introduzidas.
* **Conceitos de Domínio:** `ColdStartProfile`, `SelfAssessment`, `InterestCategory`, `WorkPreference`.

---

### UC06: Definir Contexto de Agrupamento
* **Objetivo:** Configurar os parâmetros, restrições e regras para uma sessão de formação de grupos de trabalho.
* **Ator Principal:** Docente.
* **Cenário de Sucesso:**
    1. O docente seleciona a unidade curricular e cria uma atividade de grupo.
    2. Estabelece a dimensão das equipas (limite mínimo e máximo de membros).
    3. Seleciona o critério de distribuição pedagógica pretendido (ex.: equilíbrio heterogéneo de competências vs. equipas homogéneas).
    4. O sistema regista o contexto de agrupamento e prepara a lista de inscritos aptos.
* **Fluxos Alternativos e Exceções:**
    - *2a. Dimensão incompatível com o número total de alunos inscritos:* O sistema emite alerta com recomendações de ajuste.
* **Regras de Negócio:**
    - O número de membros de cada grupo gerado deve obedecer estritamente aos limites configurados na atividade.
* **Conceitos de Domínio:** `GroupingContext`, `Activity`, `GroupPolicy`, `GroupSizeConstraint`.