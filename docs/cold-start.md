# Cold Start (issue #42)

Um estudante sem classificações pode preencher um questionário no seu perfil. O questionário guarda níveis de competência de 1 a 5, interesses e preferência de papel na equipa. Se preferir omitir a autoavaliação, a opção de avançar atribui nível neutro 3 a todas as competências disponíveis. O catálogo inclui três competências iniciais quando a base de dados ainda não contém nenhuma; outras competências existentes continuam disponíveis.

O `ColdStartProfile` preserva as respostas originais. `PerfilCompetenciasService` calcula o perfil mostrado ao utilizador. Sem notas, o nível é o da autoavaliação e a origem é `AUTOAVALIACAO`. Para cada competência com notas, converte primeiro a média académica ponderada para a escala 1–5 já usada pelo projeto. Depois combina esse nível com a autoavaliação:

`nível final = arredondar((nível autoavaliado + N × nível académico) / (N + 1))`

`N` é o número de classificações que contribuem para essa competência. Assim, uma nota dá 50% de peso à autoavaliação, duas dão 33%, e o peso continua a diminuir. A origem passa a `MISTO`. Competências só com notas têm origem `HISTORICO_ACADEMICO`; competências sem notas mantêm o nível provisório. Os interesses e a preferência ficam guardados para utilização futura, mas não alteram artificialmente os níveis de competência.

O frontend obtém o aluno, o seu histórico e o perfil. Se não houver notas nem perfil provisório, mostra o questionário. `POST /api/estudantes/{id}/cold-start` valida os níveis e recusa alunos já classificados ou com perfil provisório. `GET /api/competencias` fornece as opções do questionário. A gravação e o recálculo são transacionais. Registar uma nota através do serviço de histórico recalcula o perfil automaticamente.

Para demonstrar: criar um aluno, abrir o perfil, responder ou escolher níveis neutros, confirmar a origem `AUTOAVALIACAO`, registar uma nota para uma unidade curricular associada a uma competência e confirmar a origem `MISTO` e a nova pontuação. Os testes `ColdStartServiceTest`, `PerfilCompetenciasColdStartTest` e `ColdStartIntegrationTest` cobrem validação, ponderação progressiva e persistência.
