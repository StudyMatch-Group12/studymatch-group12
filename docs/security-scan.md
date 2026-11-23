# Scan de segurança

O projeto usa o [Drogonsec](https://github.com/filipi86/drogonsec) para analisar o código à procura de problemas de segurança.

## O que analisa

- SAST: vulnerabilidades no código.
- SCA: vulnerabilidades conhecidas nas dependências do frontend (npm) e do backend (Maven).
- Leaks: passwords e chaves esquecidas no código.

## Quando corre

O workflow `.github/workflows/security-scan.yml` corre em cada Pull Request para a `main` e em cada push na `main`.

O scan só informa e não bloqueia o Pull Request. Mesmo que encontre vulnerabilidades HIGH ou CRITICAL, o PR pode ser aprovado, mas as pessoas que fazem a review devem ler o relatório.

## Como ver o resultado

1. No GitHub, abre o separador **Actions** e escolhe a execução do **Security Scan**.
2. Em **Artifacts**, descarrega o `drogonsec-report`.
3. Abre o ficheiro `drogonsec-report.html` no browser.

O relatório fica disponível durante 14 dias.

## Backend (Maven)

Antes do scan, o workflow instala o Java 21 e descarrega as dependências do backend com `./mvnw dependency:resolve`. O contentor do Drogonsec recebe essa pasta em modo de leitura, para conseguir analisar o `pom.xml`.

## Configuração

- O ficheiro `.drogonsec.yaml`, na raiz do projeto, define os motores ativos e as pastas ignoradas (`.mvn`, `docs` e `src/test`).
- A versão do Drogonsec está fixa no workflow (v0.4.0). Para atualizar, muda a tag da imagem no `security-scan.yml`.

## Nota

O scan corre apenas no GitHub Actions. O Docker é usado só lá dentro, no servidor do GitHub, e não é preciso instalá-lo para trabalhar no projeto.
