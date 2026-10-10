# Casos de Uso Adicionais — StudyMatch (Sprint 1)

Documentação dos 2 casos de uso criativos de valor acrescentado propostos pela equipa para complementar a especificação funcional do StudyMatch, em conformidade com os requisitos do Sprint 1.

---

### UC07: Gerir Disponibilidade Horária e Modalidade de Trabalho
* **Objetivo:** Permitir ao estudante definir os períodos horários em que se encontra disponível para colaborar em grupo e indicar a sua modalidade de trabalho de preferência.
* **Ator Principal:** Estudante.
* **Cenário Principal de Sucesso:**
    1. O estudante acede à secção de disponibilidade no seu perfil.
    2. Preenche a matriz horária semanal selecionando os blocos livres (Manhãs, Tardes, Pós-laboral / Noite, Fins de semana).
    3. Seleciona a modalidade de trabalho prioritária: Presencial (campus), Híbrida ou Totalmente Remota.
    4. O sistema valida as opções selecionadas e persiste a grelha de disponibilidade associada ao perfil do estudante.
* **Fluxos Alternativos e Exceções:**
    - *2a. Nenhuma disponibilidade indicada:* O sistema alerta que a ausência de horários pode prejudicar o emparelhamento com colegas compatíveis.
* **Regras de Negócio:**
    - Dois estudantes apenas são considerados com horário viável pelo motor de agrupamento se apresentarem uma sobreposição mínima de pelo menos 4 horas semanais comuns.
* **Conceitos de Domínio:** `TimeAvailability`, `TimeSlot`, `WorkModality`.
* **Justificação de Valor Acrescentado:**
    - **Problema real:** Na vida académica, o maior foco de conflito e insucesso em trabalhos de grupo deve-se à incompatibilidade severa de horários (por exemplo, trabalhadores-estudantes agrupados com alunos em regime integral diurno) e preferências logísticas divergentes.
    - **Impacto no StudyMatch:** Acrescenta uma camada de viabilidade prática que complementa a afinidade puramente técnica das competências, assegurando que as equipas formadas têm condições reais de reunir e produzir sem atrito.

---

### UC08: Definir Preferências e Restrições de Afinidade (Affinities)
* **Objetivo:** Permitir ao estudante registar colegas com quem possui histórico prévio de colaboração positiva (afinidade favorável) ou incompatibilidades interpessoais graves a evitar (restrição de exclusão suave).
* **Ator Principal:** Estudante.
* **Cenário Principal de Sucesso:**
    1. O estudante acede ao módulo de preferências antes da formação de grupos para uma dada unidade curricular.
    2. Pesquisa um colega pelo número de estudante ou nome institucional.
    3. Assinala a natureza da relação: *Preferência Positiva* (desejo de trabalhar em conjunto) ou *Evitar Agrupamento* (atrito prévio comprovado).
    4. O sistema regista as preferências de forma confidencial.
* **Fluxos Alternativos e Exceções:**
    - *2a. Colega não inscrito na UC:* O sistema notifica que o utilizador selecionado não faz parte da atividade de agrupamento.
    - *3a. Excesso de restrições negativas:* O sistema limita as restrições a um máximo de 2 elementos para não inviabilizar o algoritmo de agrupamento.
* **Regras de Negócio:**
    - Confidencialidade estrita: nenhum estudante é notificado nem tem visibilidade sobre quem o adicionou a listas de exclusão.
    - As preferências atuam como restrições suaves (*soft constraints*) com pesos heurísticos, nunca impedindo a formação de grupos caso a diversidade de perfis assim o exija.
* **Conceitos de Domínio:** `AffinityPreference`, `AffinityType` (POSITIVE, NEGATIVE), `PrivacyConstraint`.
* **Justificação de Valor Acrescentado:**
    - **Problema real:** Agrupamentos puramente automáticos baseados apenas em competências podem forçar o trabalho conjunto entre indivíduos com histórico de atritos graves ou ignorar sinergias prévias consolidadas.
    - **Impacto no StudyMatch:** Reduz a rejeição psicológica à ferramenta, aumentando a satisfação e a cooperação dos membros da equipa sem comprometer os objetivos pedagógicos do docente.

## UC09: Consultar e Aceitar Proposta de Agrupamento
- **Objetivo:** Permitir ao estudante visualizar a equipa gerada para uma determinada atividade de grupo e confirmar formalmente a sua atribuição.
- **Ator Principal:** Estudante.
- **Cenário Principal de Sucesso:**
  1. O estudante acede à secção de atividades/agrupamentos na sua área pessoal.
  2. Seleciona a unidade curricular e a atividade de grupo ativa.
  3. O sistema apresenta a composição da equipa atribuída, exibindo os colegas de grupo, a sobreposição horária calculada e a complementaridade de competências.
  4. O estudante clica no botão de confirmação para aceitar a alocação.
  5. O sistema regista o estado de aceitação do estudante e atualiza o indicador visual da equipa.
- **Fluxos Alternativos e Exceções:**
  - **1a. Agrupamento em processamento:** O sistema notifica que a distribuição ainda se encontra em fase de rascunho/validação pelo docente.
  - **4a. Expiração do prazo de confirmação:** O sistema altera automaticamente o estado do estudante para aceito por omissão (*default*) após a data limite.
- **Regras de Negócio:**
  - O grupo transita de estado rascunho (**DRAFT**) para confirmado (**CONFIRMED**) apenas após a validação do docente ou confirmação da maioria dos membros.
- **Conceitos de Domínio:** `GroupAssignment`, `AssignmentStatus` (PENDING, CONFIRMED, EXPIRED), `TeamView`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** A falta de transparência e de confirmação explícita sobre a constituição das equipas gera incerteza sobre quem são os colegas e se todos estão cientes da alocação.
  - **Impacto no StudyMatch:** Garante o compromisso inicial de cada estudante com a equipa e fornece visibilidade imediata sobre os elementos de contacto e sinergias do grupo.

## UC10: Executar Motor de Agrupamento Automático
- **Objetivo:** Permitir ao docente disparar a geração automática das equipas com base nas regras de agrupamento, matriz de competências, disponibilidade horária e preferências de afinidade.
- **Ator Principal:** Docente.
- **Cenário Principal de Sucesso:**
  1. O docente acede à atividade de grupo previamente configurada na unidade curricular.
  2. Seleciona a opção de geração automática de grupos.
  3. O sistema executa o algoritmo de otimização cruzando os limites de dimensão, pesos de competências, janelas horárias comuns e restrições de afinidade.
  4. O sistema gera uma proposta de distribuição e apresenta a métrica global de compatibilidade da turma.
  5. O docente revê a distribuição e publica os grupos gerados.
- **Fluxos Alternativos e Exceções:**
  - **3a. Estudo de caso com número de alunos ímpar/sobrante:** O sistema alerta para a existência de um grupo com dimensão fora do intervalo padrão e sugere o ajuste manual de um membro.
  - **4a. Rejeição da proposta:** O docente pode reexecutar o algoritmo alterando os pesos dos critérios ou efetuar ajustes manuais por *drag-and-drop*.
- **Regras de Negócio:**
  - A execução do algoritmo não altera de imediato os grupos visíveis aos estudantes até que o docente acione explicitamente a publicação.
  - As restrições duras (tamanho do grupo) têm prioridade absoluta sobre as restrições suaves (afinidades e horários).
- **Conceitos de Domínio:** `GroupGeneratorService`, `TeamDraft`, `CompatibilityScore`, `OptimizationPolicy`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** A formação manual de grupos em turmas grandes é um processo moroso, propenso a vieses e incapaz de cruzar eficientemente múltiplas variáveis complexas (horários, notas e preferências).
  - **Impacto no StudyMatch:** Automatiza o processo nuclear da plataforma, garantindo equipas pedagogicamente equilibradas e logisticamente viáveis em questão de segundos.

## UC11: Consultar Detalhes da Equipa e Membros

- **Objetivo:** Permitir ao estudante visualizar as informações de contacto, preferências de trabalho e mapa de competências dos colegas do seu grupo atribuído.
- **Ator Principal:** Estudante.
- **Cenário Principal de Sucesso:**
  1. O estudante acede à área do seu grupo ativo na unidade curricular.
  2. O sistema apresenta o painel da equipa com a lista dos membros do grupo.
  3. O estudante seleciona um colega de equipa para ver os detalhes.
  4. O sistema exibe o e-mail institucional do colega, a sua disponibilidade horária e as áreas de maior competência.
- **Fluxos Alternativos e Exceções:**
  - **1a. Estudante ainda não alocado a um grupo:** O sistema exibe uma mensagem informativa a indicar que a atribuição de grupos ainda está pendente.
- **Regras de Negócio:**
  - Apenas membros pertencentes ao mesmo grupo têm permissão para visualizar os detalhes de contacto direto e horários entre si.
- **Conceitos de Domínio:** `TeamMemberView`, `MemberDetails`, `PrivacyPolicy`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** Após a formação automática do grupo, os estudantes precisam de uma forma rápida e centralizada para entrar em contacto com os colegas e perceber quem domina cada área do trabalho.
  - **Impacto no StudyMatch:** Facilita a comunicação inicial e a divisão interna de tarefas com base no perfil de competências de cada membro, sem exigir integrações complexas.

  ### **UC12: Consultar Atividades de Grupo Disponíveis**


- **Objetivo:** Permitir ao estudante consultar as atividades de grupo disponíveis nas unidades curriculares em que está inscrito, visualizando informações como o nome, a descrição e o estado de cada atividade.
- **Ator Principal:** Estudante.
- **Cenário Principal de Sucesso:**
  1. O estudante acede à área de atividades de grupo da plataforma.
  2. O sistema identifica as unidades curriculares em que o estudante está inscrito.
  3. O sistema apresenta uma lista das atividades de grupo disponíveis nessas unidades curriculares.
  4. O estudante consulta as informações de cada atividade, incluindo o nome, a descrição e o estado.
- **Fluxos Alternativos e Exceções:**
  - **3a. Não existem atividades disponíveis:** O sistema apresenta uma mensagem informativa a indicar que não existem atividades de grupo disponíveis nas unidades curriculares do estudante.
  - **3b. Erro ao carregar as atividades:** O sistema apresenta uma mensagem de erro e permite ao estudante tentar novamente.
- **Regras de Negócio:**
  - Apenas estudantes autenticados podem consultar as atividades de grupo.
  - Cada estudante apenas pode visualizar atividades associadas às unidades curriculares em que está inscrito.
  - A consulta de atividades não permite alterar ou eliminar informações das mesmas.
- **Conceitos de Domínio:** `GroupActivity`, `Course`, `Student`, `Enrollment`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** Os estudantes precisam de uma forma simples e centralizada de consultar as atividades de grupo disponíveis nas suas unidades curriculares, sem terem de procurar informações em diferentes locais.
  - **Impacto no StudyMatch:** Facilita o acesso às atividades de grupo e melhora a organização académica dos estudantes, complementando as funcionalidades de definição de contextos de agrupamento e formação automática de equipas, sem exigir algoritmos adicionais complexos.

  ### **UC13: Consultar Estudantes Inscritos numa Unidade Curricular**

- **Objetivo:** Permitir ao docente consultar a lista de estudantes inscritos numa determinada unidade curricular, visualizando informações académicas básicas de cada estudante.
- **Ator Principal:** Docente.
- **Cenário Principal de Sucesso:**
  1. O docente acede à área de gestão das suas unidades curriculares.
  2. O sistema apresenta as unidades curriculares associadas ao docente.
  3. O docente seleciona a unidade curricular que pretende consultar.
  4. O sistema apresenta a lista de estudantes inscritos nessa unidade curricular.
  5. O docente visualiza as informações básicas dos estudantes, incluindo o nome e o número de estudante.
- **Fluxos Alternativos e Exceções:**
  - **4a. Não existem estudantes inscritos:** O sistema apresenta uma mensagem informativa a indicar que não existem estudantes inscritos na unidade curricular selecionada.
  - **4b. Erro ao carregar a lista de estudantes:** O sistema apresenta uma mensagem de erro e permite ao docente tentar novamente.
- **Regras de Negócio:**
  - Apenas docentes autenticados e autorizados podem consultar a lista de estudantes de uma unidade curricular.
  - Cada docente apenas pode visualizar os estudantes inscritos nas unidades curriculares às quais está associado.
  - A consulta da lista de estudantes não permite alterar ou eliminar os seus dados.
- **Conceitos de Domínio:** `Student`, `Course`, `Enrollment`, `Teacher`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** Os docentes necessitam de consultar facilmente os estudantes inscritos nas suas unidades curriculares para acompanhar a participação nas atividades e organizar a formação de grupos.
  - **Impacto no StudyMatch:** Facilita a identificação dos estudantes disponíveis para agrupamento e complementa as funcionalidades de definição de contextos de agrupamento e formação automática de equipas, sem exigir processamento complexo.

  ### **UC14: Exportar Lista de Grupos Formados**

- **Objetivo:** Permitir ao docente exportar a lista de grupos formados numa determinada atividade de uma unidade curricular, incluindo os estudantes pertencentes a cada grupo, para um ficheiro CSV.
- **Ator Principal:** Docente.
- **Cenário Principal de Sucesso:**
  1. O docente acede à área de gestão de grupos da plataforma.
  2. O sistema apresenta as unidades curriculares e atividades de agrupamento associadas ao docente.
  3. O docente seleciona a atividade cujos grupos pretende exportar.
  4. O sistema apresenta os grupos formados nessa atividade.
  5. O docente seleciona a opção "Exportar Grupos".
  6. O sistema gera um ficheiro CSV com a identificação dos grupos, os nomes e os números dos estudantes e a respetiva unidade curricular.
  7. O docente descarrega o ficheiro para o seu dispositivo.
- **Fluxos Alternativos e Exceções:**
  - **4a. Ainda não existem grupos formados:** O sistema apresenta uma mensagem informativa a indicar que não existem grupos disponíveis para exportação.
  - **6a. Erro ao gerar o ficheiro:** O sistema apresenta uma mensagem de erro e permite ao docente repetir a operação.
- **Regras de Negócio:**
  - Apenas docentes autenticados e autorizados podem exportar listas de grupos das atividades pelas quais são responsáveis.
  - A exportação apenas inclui grupos efetivamente formados na atividade selecionada.
  - O ficheiro exportado deve apresentar os dados de forma organizada, identificando claramente cada grupo e os respetivos membros.
  - A exportação não altera a composição dos grupos nem os dados dos estudantes.
- **Conceitos de Domínio:** `StudyGroup`, `GroupMember`, `Course`, `GroupActivity`, `GroupExport`.
- **Justificação de Valor Acrescentado:**
  - **Problema real:** Após a formação dos grupos, os docentes precisam frequentemente de consultar e partilhar a distribuição dos estudantes, sem terem de copiar manualmente a informação da plataforma.
  - **Impacto no StudyMatch:** Facilita a organização e o acompanhamento dos grupos, permitindo obter rapidamente uma lista estruturada dos estudantes e das respetivas equipas, sem exigir integrações externas ou algoritmos adicionais.