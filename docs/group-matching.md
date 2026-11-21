# Estratégia de matching e regras de dimensão

O `GroupMatcher` forma grupos de estudantes a partir dos perfis de competências existentes. O resultado é determinístico: a mesma entrada produz sempre os mesmos grupos.

## Regras de dimensão

- Cada grupo tem 4 ou 5 estudantes.
- São necessários pelo menos 4 estudantes.
- Não se repete nem se omite nenhum estudante.
- Preferem-se grupos de 5. Por exemplo, 23 estudantes dão 5, 5, 5, 4 e 4.
- Se não for possível formar apenas grupos de 4 ou 5 (por exemplo com 6 ou 7 estudantes), o algoritmo explica o motivo com um erro em vez de produzir grupos inválidos.

## Pontuação

- A pontuação de cada estudante é a média dos níveis nas competências existentes.
- Cold start: se um estudante não tem nível numa competência, ou não tem perfil, essa competência conta como nível neutro (3).
- Os níveis provisórios do cold start são usados da mesma forma que os restantes.

## Distribuição

1. Os estudantes são ordenados da pontuação mais alta para a mais baixa, com desempate pelo identificador.
2. Cada estudante é colocado, por esta ordem, num dos grupos que ainda tem vaga.
3. Num grupo vazio entra sempre o primeiro estudante disponível.
4. Num grupo já iniciado, o estudante vai para o grupo onde mais complementa as competências (níveis acima do melhor nível já presente no grupo), penalizando os grupos que já estão mais fortes.
5. Em caso de empate de pontuação, escolhe-se o grupo de menor índice.
