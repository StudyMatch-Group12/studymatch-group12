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