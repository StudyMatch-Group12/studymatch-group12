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